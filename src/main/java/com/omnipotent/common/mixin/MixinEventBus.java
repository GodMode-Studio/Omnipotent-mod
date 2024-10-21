package com.omnipotent.common.mixin;

import com.mojang.authlib.GameProfile;
import com.omnipotent.util.MethodRequirement;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.CustomClassVisitor;
import net.minecraftforge.fml.CustomMethodVisitor;
import net.minecraftforge.fml.CustomMethodVisitor2;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.*;
import org.objectweb.asm.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(value = EventBus.class, remap = false)
public abstract class MixinEventBus implements IEventExceptionHandler {
    private static Set<MethodRequirement> requiredMethods;
    private static Set<MethodRequirement> requiredMethodsWeak;
    @Shadow
    private ConcurrentHashMap<Object, ArrayList<IEventListener>> listeners;
    @Shadow
    private Map<Object, ModContainer> listenerOwners;

    @Shadow
    protected abstract void register(Class<?> eventType, Object target, Method method, ModContainer owner);

    @Unique
    private static final List<String> omnipotent_mod$negMods = new ArrayList<>();
    @Unique
    private static final List<String> omnipotent_mod$secMods = new ArrayList<>();

    @Inject(method = "register(Ljava/lang/Object;)V", at = @At("HEAD"), cancellable = true)
    public void register(Object target, CallbackInfo ci) {
        try {
            if (checkInsecure(target, false) || checkInsecure(target, true))
                ci.cancel();
            else if (!omnipotent_mod$secMods.contains(target.getClass().getCanonicalName()))
                omnipotent_mod$secMods.add(target.getClass().getCanonicalName());
        } catch (Exception e) {
            FMLLog.log.warn("Error in security check. Please submit a comment about this error with the mods you are " +
                    "using in the comments section of the mod on CurseForge.", e);
        }
    }

    @Unique
    private static boolean checkInsecure(Object target, boolean weak) {
        if (target == null || target.getClass().getCanonicalName() == null) return false;
        Class<?> classObj = target.getClass();
        String canonicalName = classObj.getCanonicalName();
        if (canonicalName.startsWith("net.minecraftforge") || canonicalName.startsWith("java") ||
                canonicalName.startsWith("com.omnipotent")) {
            return false;
        }
        if (omnipotent_mod$negMods.contains(canonicalName))
            return true;
        if (omnipotent_mod$secMods.contains(canonicalName))
            return false;

        boolean firstCondition;
        ClassLoader classLoader = classObj.getClassLoader();
        requiredMethods = requiredMethods == null ? getMethodRequirements(classLoader, false) : requiredMethods;
        requiredMethodsWeak = requiredMethodsWeak == null ? getMethodRequirements(classLoader, true) : requiredMethodsWeak;

        try {
            firstCondition = analyzeClass(target, weak, classLoader);
        } catch (Exception e) {
            return false;
        }

        boolean hasSynchronized = false;

        for (Method method : classObj.getDeclaredMethods()) {
            if (Modifier.isSynchronized(method.getModifiers())) {
                hasSynchronized = true;
                break;
            }
        }

        boolean b = false;

        if (!weak) {
            b = hasSynchronized && firstCondition && checkFields(classObj);
        } else {
            if (hasSynchronized && firstCondition && checkFields(classObj)) {
                b = true;
            } else if (firstCondition) {
                try {
                    b = makesReference(classObj, classLoader);
                } catch (IOException e) {
                    FMLLog.log.error("error in makesReference and ignored ", e);
                    return false;
                }
            }
        }

        if (b) FMLLog.log.warn("checkInsecure true");
        return b;
    }

    @Unique
    private static boolean makesReference(Class<?> classObj, ClassLoader classLoader) throws IOException {
        CustomMethodVisitor methodVisitor = new CustomMethodVisitor(Opcodes.ASM5, omnipotent_mod$negMods);
        ClassReader classReader = getClassReader(classLoader, classObj.getCanonicalName());
        ClassVisitor classVisitor = new CustomClassVisitor(Opcodes.ASM5, methodVisitor);
        try {
            classReader.accept(classVisitor, 0);
        } catch (RuntimeException e) {
            return true;
        }

        return false;
    }

    @Unique
    private static boolean analyzeClass(Object object, boolean weak, ClassLoader classLoader) throws IOException {
        Class<?> classObj = object.getClass();
        String className = classObj.getCanonicalName();
        Objects.requireNonNull(classLoader, "ClassLoader null");
        ClassReader classReader = getClassReader(classLoader, className);
        Set<String> calledMethods = new HashSet<>();
        MethodVisitor methodVisitor = new CustomMethodVisitor2(Opcodes.ASM5, calledMethods);
        ClassVisitor classVisitor = new CustomClassVisitor(Opcodes.ASM5, methodVisitor);
        classReader.accept(classVisitor, 0);
        for (MethodRequirement methodInfo : !weak ? requiredMethods : requiredMethodsWeak) {
            if (!calledMethods.contains(methodInfo.getByteCodeRepresentation()))
                return false;
        }
        omnipotent_mod$negMods.add(classObj.getCanonicalName());
        FMLLog.log.warn("First condition true");
        return true;
    }

    @Unique
    private static ClassReader getClassReader(ClassLoader customClassLoader, String className) throws IOException {
        try (InputStream classStream = customClassLoader.getResourceAsStream(className.replace('.', '/') + ".class")) {
            return new ClassReader(classStream);
        }
    }

    @Unique
    private static Set<MethodRequirement> getMethodRequirements(ClassLoader classLoader, boolean weak) {
        Set<MethodRequirement> requiredMethods;
        try {
            requiredMethods = !weak ?
                    new HashSet<>(Arrays.asList(
                            new MethodRequirement(getMethodOrSuperMethod(classLoader, EventBus.class, "register", Object.class)),
                            new MethodRequirement(getMethodOrSuperMethod(Throwable.class, "getStackTrace")),
                            new MethodRequirement(getMethodOrSuperMethod(Thread.class, "interrupt")),
                            new MethodRequirement(getMethodOrSuperMethod(classLoader, ObfuscationReflectionHelper.class, "setPrivateValue",
                                    Class.class, Object.class, Object.class, String.class)),
                            new MethodRequirement(getOBfMethodOrSuperMethod(MinecraftServer.class, "func_71278_l", boolean.class)),
                            new MethodRequirement(getMethodOrSuperMethod(classLoader, ListenerList.class, "unregisterAll",
                                    int.class, IEventListener.class))
                    )) : new HashSet<>(Arrays.asList(
                    new MethodRequirement(getMethodOrSuperMethod(classLoader, EventBus.class, "unregister", Object.class)),
//                new MethodRequirement(getMethodOrSuperMethod(classLoader,World.class, "removeEntity", Entity.class)),
                    new MethodRequirement(getMethodOrSuperMethod(classLoader, NonNullList.class, "set", int.class, Object.class)),
                    new MethodRequirement(getOBfMethodOrSuperMethod(InventoryPlayer.class, "func_70299_a", void.class,
                            int.class, ItemStack.class))
            ));
        } catch (Exception e) {
            return null;
        }
        return requiredMethods;
    }

    @Unique
    private static Method getOBfMethodOrSuperMethod(Class<?> clazz, String methodName, Class<?> returnType, Class<?>... parameters) {
        while (clazz != null) {
            try {
                return ObfuscationReflectionHelper.findMethod(clazz, methodName, returnType, parameters);
            } catch (Exception e) {
            }
        }
        return null;
    }

    @Unique
    private static Method getMethodOrSuperMethod(ClassLoader classLoader, Class<?> clazz, String methodName, Class<?>... parameters) throws NoSuchMethodException {
        while (clazz != null) {
            try {
                clazz = classLoader.loadClass(clazz.getCanonicalName());
                return clazz.getDeclaredMethod(methodName, parameters);
            } catch (NoSuchMethodException e) {
                clazz = clazz.getSuperclass();
            } catch (ClassNotFoundException e) {
            }
        }
        return null;
    }

    @Unique
    private static Method getMethodOrSuperMethod(Class<?> clazz, String methodName, Class<?>... parameters) {
        while (clazz != null) {
            try {
                return clazz.getDeclaredMethod(methodName, parameters);
            } catch (NoSuchMethodException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    @Unique
    private static boolean checkFields(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        Set<Class<?>> requiredFieldTypes = new HashSet<>(Arrays.asList(Thread.class, Timer.class, TimerTask.class, GameProfile.class));
        Set<Class<?>> foundFieldTypes = new HashSet<>();
        for (Field field : fields) {
            foundFieldTypes.add(field.getType());
        }

        return foundFieldTypes.containsAll(requiredFieldTypes);
    }
}
