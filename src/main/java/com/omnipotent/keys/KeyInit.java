package com.omnipotent.keys;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class KeyInit {
    public static KeyBinding keyOpenGuiKaia = new KeyBinding("Configurações", Keyboard.KEY_R, "Botões Kaia");
    public static KeyBinding keyReturnKaia = new KeyBinding("Retorna Kaia", Keyboard.KEY_G, "Botões Kaia");
    public static KeyBinding kaiaGuiEnchantment = new KeyBinding("Encantamentos Kaia", Keyboard.KEY_L, "Botões Kaia");
    public static List<KeyBinding> keyBindingList = new ArrayList<KeyBinding>();

    public static void initKeys(){
        keyBindingList.add(keyOpenGuiKaia);
        keyBindingList.add(keyReturnKaia);
        keyBindingList.add(kaiaGuiEnchantment);
        for(KeyBinding keyBinding :keyBindingList){
            ClientRegistry.registerKeyBinding(keyBinding);
        }
    }
}
