package net.minecraftforge.fml;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.ArrayList;
import java.util.List;

public final class CustomMethodVisitor extends MethodVisitor {

    private static List<String> omnipotent_mod$negMods = new ArrayList<>();

    public CustomMethodVisitor(int api, List<String> negMods) {
        super(api);
        omnipotent_mod$negMods = negMods;
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        type = type.replace('/', '.');
        if (omnipotent_mod$negMods.contains(type)) {
            throw new RuntimeException();
        }
    }
}

