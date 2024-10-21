package net.minecraftforge.fml;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class CustomClassVisitor extends ClassVisitor {

    private final MethodVisitor methodVisitorField;

    public CustomClassVisitor(int api, MethodVisitor methodVisitor) {
        super(api);
        this.methodVisitorField = methodVisitor;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        return methodVisitorField;
    }
}
