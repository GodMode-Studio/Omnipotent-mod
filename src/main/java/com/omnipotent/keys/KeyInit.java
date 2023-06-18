package com.omnipotent.keys;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class KeyInit {
    public static KeyBinding keyOpenGuiKaia = new KeyBinding(I18n.format("keykaia.config"), Keyboard.KEY_R, I18n.format("keykaia.category"));
    public static KeyBinding keyReturnKaia = new KeyBinding(I18n.format("keykaia.returnkaia"), Keyboard.KEY_G, I18n.format("keykaia.category"));
    public static KeyBinding kaiaGuiEnchantment = new KeyBinding(I18n.format("keykaia.enchantmentkaia"), Keyboard.KEY_L, I18n.format("keykaia.category"));
    public static KeyBinding kaiaGuiConfig = new KeyBinding(I18n.format("gui kaia"), Keyboard.KEY_P, I18n.format("keykaia.category"));
    public static List<KeyBinding> keyBindingList = new ArrayList<KeyBinding>();

    public static void initKeys(){
        keyBindingList.add(keyOpenGuiKaia);
        keyBindingList.add(keyReturnKaia);
        keyBindingList.add(kaiaGuiEnchantment);
        keyBindingList.add(kaiaGuiConfig);
        for(KeyBinding keyBinding :keyBindingList){
            ClientRegistry.registerKeyBinding(keyBinding);
        }
    }
}
