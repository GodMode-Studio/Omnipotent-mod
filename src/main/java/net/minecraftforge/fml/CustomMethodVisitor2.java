package net.minecraftforge.fml;

import net.minecraft.launchwrapper.Launch;
import org.objectweb.asm.MethodVisitor;

import java.util.Set;

public final class CustomMethodVisitor2 extends MethodVisitor {

    private Set<String> calledMethods;
    private static boolean deobfuscatedEnvironment = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

    public CustomMethodVisitor2(int api, Set<String> methods) {
        super(api);
        this.calledMethods = methods;
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if ("<init>".equals(name))
            return;
        String className = owner.replace('/', '.');
        className = changeNameIfNecessary(name, className);
        calledMethods.add(className + "." + name + descriptor);
    }

    private static String changeNameIfNecessary(final String name, final String className) {
        String targetMethodName = deobfuscatedEnvironment ? "isServerRunning" : "func_71278_l";
        String integratedServer = "net.minecraft.server.integrated.IntegratedServer";
        String dedicatedServer = "net.minecraft.server.dedicated.DedicatedServer";
        String minecraftServer = "net.minecraft.server.MinecraftServer";
        return name.equals(targetMethodName) && (className.equals(integratedServer) || className.equals(dedicatedServer))
                ? minecraftServer : className;
    }
}

