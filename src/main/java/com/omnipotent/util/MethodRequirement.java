package com.omnipotent.util;

import net.minecraftforge.fml.common.FMLLog;
import org.objectweb.asm.Type;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;

public final class MethodRequirement {
    private final Method completeMethod;

    public MethodRequirement(@Nonnull Method method) {
        this.completeMethod = method;
    }

    public String getByteCodeRepresentation() {
        return completeMethod.getDeclaringClass().getCanonicalName() + '.' + completeMethod.getName() + Type.getMethodDescriptor(completeMethod);
    }

    public Method getMethod() {
        return this.completeMethod;
    }
}
