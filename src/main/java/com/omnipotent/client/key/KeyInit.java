package com.omnipotent.client.key;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class KeyInit {
    public static KeyBinding KaiaGui = new KeyBinding(I18n.format("keykaia.config"), Keyboard.KEY_R, I18n.format("keykaia.category"));
    public static KeyBinding keyReturnKaia = new KeyBinding(I18n.format("keykaia.returnkaia"), Keyboard.KEY_G, I18n.format("keykaia.category"));
    public static KeyBinding kaiaGuiEnchantment = new KeyBinding(I18n.format("keykaia.enchantmentkaia"), Keyboard.KEY_L, I18n.format("keykaia.category"));
    public static KeyBinding kaiaGuiBackpack = new KeyBinding(I18n.format("keykaia.backpack"), Keyboard.KEY_P, I18n.format("keykaia.category"));
    public static List<KeyBinding> keyBindingList = new ArrayList<KeyBinding>();
    public static KeyBinding kaiaGuiPotion = new KeyBinding(I18n.format("keykaia.potion"), Keyboard.KEY_O, I18n.format("keykaia.category"));
    public static KeyBinding kaiaGuiDimension = new KeyBinding(I18n.format("keykaia.dimension"), Keyboard.KEY_K, I18n.format("keykaia.category"));


    public static void initKeys(){
        keyBindingList.add(KaiaGui);
        keyBindingList.add(keyReturnKaia);
        keyBindingList.add(kaiaGuiEnchantment);
        keyBindingList.add(kaiaGuiBackpack);
        keyBindingList.add(kaiaGuiPotion);
        keyBindingList.add(kaiaGuiDimension);
        for(KeyBinding keyBinding :keyBindingList){
            ClientRegistry.registerKeyBinding(keyBinding);
        }
    }
}
