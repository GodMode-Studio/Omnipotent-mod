package com.omnipotent.client.event;

import com.omnipotent.common.tool.Kaia;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Language;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

import java.util.List;
import java.util.Objects;

import static com.omnipotent.util.KaiaConstantsNbt.ownerName;

public class KaiaToolTip {
    private int tick = 0;
    private int curColor = 0;
    private int tickForNameItemKaia = 3;
    private int currentColorForNameItemKaia = 0;


    private final TextFormatting[] colors = {TextFormatting.YELLOW, TextFormatting.GOLD, TextFormatting.AQUA, TextFormatting.BLUE, TextFormatting.RED, TextFormatting.GREEN, TextFormatting.LIGHT_PURPLE};
    private final TextFormatting[] colors2 = {TextFormatting.WHITE, TextFormatting.WHITE, TextFormatting.WHITE, TextFormatting.GOLD, TextFormatting.GOLD, TextFormatting.GOLD, TextFormatting.GOLD};

    @SubscribeEvent
    public void kaiaToolTipRender(ItemTooltipEvent event) {
        if (!event.getItemStack().isEmpty() && event.getItemStack().getItem() instanceof Kaia) {
            EntityPlayer player = null;
            if (event.getEntityPlayer() != null) {
                player = event.getEntityPlayer();
            }
            List<String> tooltip = event.getToolTip();
            for (int c = 0; c < tooltip.size(); c++) {
                String tipOfDisplay = tooltip.get(c);
                Language lang = null;
                if (player != null) {
                    lang = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage();
                }
                if (lang != null && lang.getLanguageCode().equals("pt_br")) {
                    versionBR(event, player, tooltip, c, tipOfDisplay);
                } else if (lang != null && lang.getLanguageCode().equals("pt_pt")) {
                    versionPT(event, player, tooltip, c, tipOfDisplay);
                } else {
                    versionEN(event, player, tooltip, c, tipOfDisplay);
                }
            }
        }
    }

    private void versionEN(ItemTooltipEvent event, EntityPlayer player, List<String> tooltip, int c, String tipOfDisplay) {
        if (tipOfDisplay.endsWith(I18n.format("attribute.name.generic.attackDamage"))) {
            String str = I18n.format("kaia.damage");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < str.length(); i++) {
                sb.append(colors[(curColor + i) % colors.length].toString());
                sb.append(str.charAt(i));
            }
            tooltip.set(c, " " + I18n.format("attribute.modifier.equals.0", sb.toString() + TextFormatting.GRAY, I18n.format("attribute.name.generic.attackDamage")));
        } else if (tipOfDisplay.endsWith(I18n.format("attribute.name.generic.attackSpeed"))) {
            String str = I18n.format("kaia.speed");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < str.length(); i++) {
                sb.append(colors[(curColor + i) % colors.length].toString());
                sb.append(str.charAt(i));
            }
            tooltip.set(c, " " + I18n.format("attribute.modifier.equals.0", sb.toString() + TextFormatting.GRAY, I18n.format("attribute.name.generic.attackSpeed")));
        } else if (tipOfDisplay.endsWith("donoverdadeiro")) {
            String str = "True Owner: " + I18n.format("gamerYToffi");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < str.length(); i++) {
                sb.append(colors[(curColor + i) % colors.length].toString());
                sb.append(str.charAt(i));
            }
            tooltip.set(c, " " + I18n.format("attribute.modifier.equals.0", sb.toString() + TextFormatting.GRAY, ""));
        } else if (tipOfDisplay.endsWith("dono") && event.getItemStack().getTagCompound() != null) {
            if (player != null && player.getName().equals("gamerYToffi")) {
                if (event.getItemStack().getTagCompound().getString(ownerName).equals("gamerYToffi")) {
                    tooltip.set(c, " " + I18n.format("attribute.modifier.equals.0", String.valueOf(TextFormatting.GRAY), ""));
                    return;
                }
            }
            String str = "Current Owner: " + Objects.requireNonNull(event.getItemStack().getTagCompound()).getString(ownerName);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < str.length(); i++) {
                sb.append(colors2[(curColor + i) % colors2.length].toString());
                sb.append(str.charAt(i));
            }
            tooltip.set(c, " " + I18n.format("attribute.modifier.equals.0", sb.toString() + TextFormatting.GRAY, ""));
        } else if (tipOfDisplay.startsWith("Kaia")) {
            NBTTagCompound tagCompound = event.getItemStack().getTagCompound();
            String currentOwner = null;
            if (tagCompound != null)
                currentOwner = tagCompound.getString(ownerName);
            String str = "Kaia";
            String[] separation = null;
            StringBuilder sb = new StringBuilder();
            if (player != null && tagCompound != null && currentOwner.equals("gamerYToffi")) {
                str = "Kaia THE TRUE FORM";
                separation = str.split("THE");
                String Kaia = separation[0];
                for (int i = 0; i < Kaia.length(); i++) {
                    sb.append(colors[(currentColorForNameItemKaia + (i * 50)) % colors.length].toString());
                    sb.append(Kaia.charAt(i));
                }
                String specialName = separation[1];
                for (int i = 0; i < specialName.length(); i++) {
                    sb.append(colors[(currentColorForNameItemKaia + (i * 0)) % colors.length].toString());
                    sb.append(specialName.charAt(i));
                }
            } else {
                for (int i = 0; i < str.length(); i++) {
                    sb.append(colors[(currentColorForNameItemKaia + (i * 50)) % colors.length].toString());
                    sb.append(str.charAt(i));
                }
            }
            tooltip.set(c, " " + sb.toString() + TextFormatting.GRAY);
        }
    }

    private void versionBR(ItemTooltipEvent event, EntityPlayer player, List<String> tooltip, int c, String tipOfDisplay) {
        if (!tipOfDisplay.isEmpty() && tipOfDisplay.charAt(0) == ' ' && tipOfDisplay.length() > 1) {
            tipOfDisplay = tipOfDisplay.substring(1);
        }
        if (tipOfDisplay.startsWith(I18n.format("attribute.name.generic.attackDamage"))) {
            String str = I18n.format("kaia.damage");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < str.length(); i++) {
                sb.append(colors[(curColor + i) % colors.length].toString());
                sb.append(str.charAt(i));
            }
            tooltip.set(c, " " + I18n.format("attribute.modifier.equals.0", sb.toString() + TextFormatting.GRAY, I18n.format("attribute.name.generic.attackDamage")));
        } else if (tipOfDisplay.startsWith(I18n.format("attribute.name.generic.attackSpeed"))) {
            String str = I18n.format("kaia.speed");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < str.length(); i++) {
                sb.append(colors[(curColor + i) % colors.length].toString());
                sb.append(str.charAt(i));
            }
            tooltip.set(c, " " + I18n.format("attribute.modifier.equals.0", sb.toString() + TextFormatting.GRAY, I18n.format("attribute.name.generic.attackSpeed")));
        } else if (tipOfDisplay.endsWith("donoverdadeiro")) {
            String str = "Verdadeiro Dono: " + I18n.format("gamerYToffi");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < str.length(); i++) {
                sb.append(colors[(curColor + i) % colors.length].toString());
                sb.append(str.charAt(i));
            }
            tooltip.set(c, " " + I18n.format("attribute.modifier.equals.0", sb.toString() + TextFormatting.GRAY, ""));
            deleteOneAndSecongCharInPortugueseIdiome(tooltip, c);
        } else {
            NBTTagCompound tagCompound = event.getItemStack().getTagCompound();
            String currentOwner = null;
            if (tagCompound != null)
                currentOwner = tagCompound.getString(ownerName);
            if (tipOfDisplay.endsWith("dono") && tagCompound != null) {
                if (player != null && player.getName().equals("gamerYToffi")) {
                    if (currentOwner.equals("gamerYToffi")) {
                        tooltip.set(c, " " + I18n.format("attribute.modifier.equals.0", String.valueOf(TextFormatting.GRAY), ""));
                        deleteOneAndSecongCharInPortugueseIdiome(tooltip, c);
                        return;
                    }
                }
                String str = "Dono Atual: " + Objects.requireNonNull(tagCompound).getString(ownerName);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < str.length(); i++) {
                    sb.append(colors2[(curColor + i) % colors2.length].toString());
                    sb.append(str.charAt(i));
                }
                tooltip.set(c, " " + I18n.format("attribute.modifier.equals.0", sb.toString() + TextFormatting.GRAY, ""));
                deleteOneAndSecongCharInPortugueseIdiome(tooltip, c);
            } else if (tipOfDisplay.startsWith("Kaia")) {
                String str = "Kaia";
                String[] separation = null;
                StringBuilder sb = new StringBuilder();
                if (player != null && tagCompound != null && currentOwner.equals("gamerYToffi")) {
                    str = "Kaia THE VERDADEIRA FORMA";
                    separation = str.split("THE");
                    String Kaia = separation[0];
                    for (int i = 0; i < Kaia.length(); i++) {
                        sb.append(colors[(currentColorForNameItemKaia + (i * 50)) % colors.length].toString());
                        sb.append(Kaia.charAt(i));
                    }
                    String specialName = separation[1];
                    for (int i = 0; i < specialName.length(); i++) {
                        sb.append(colors[(currentColorForNameItemKaia + (i * 0)) % colors.length].toString());
                        sb.append(specialName.charAt(i));
                    }
                } else {
                    for (int i = 0; i < str.length(); i++) {
                        sb.append(colors[(currentColorForNameItemKaia + (i * 50)) % colors.length].toString());
                        sb.append(str.charAt(i));
                    }
                }
                tooltip.set(c, " " + sb.toString() + TextFormatting.GRAY);
            }
        }
    }

    private void versionPT(ItemTooltipEvent event, EntityPlayer player, List<String> tooltip, int c, String tipOfDisplay) {
        NBTTagCompound tagCompound = event.getItemStack().getTagCompound();
        String currentOwner = null;
        if (tagCompound != null)
            currentOwner = tagCompound.getString(ownerName);
        if (!tipOfDisplay.isEmpty() && tipOfDisplay.charAt(0) == ' ' && tipOfDisplay.length() > 1) {
            tipOfDisplay = tipOfDisplay.substring(1);
        }
        if (tipOfDisplay.endsWith(I18n.format("attribute.name.generic.attackDamage"))) {
            String str = I18n.format("kaia.damage");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < str.length(); i++) {
                sb.append(colors[(curColor + i) % colors.length].toString());
                sb.append(str.charAt(i));
            }
            tooltip.set(c, " " + I18n.format("attribute.modifier.equals.0", sb.toString() + TextFormatting.GRAY, I18n.format("attribute.name.generic.attackDamage")));
        } else if (tipOfDisplay.endsWith(I18n.format("attribute.name.generic.attackSpeed"))) {
            String str = I18n.format("kaia.speed");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < str.length(); i++) {
                sb.append(colors[(curColor + i) % colors.length].toString());
                sb.append(str.charAt(i));
            }
            tooltip.set(c, " " + I18n.format("attribute.modifier.equals.0", sb.toString() + TextFormatting.GRAY, I18n.format("attribute.name.generic.attackSpeed")));
        } else if (tipOfDisplay.endsWith("donoverdadeiro")) {
            String str = "Verdadeiro Dono: " + I18n.format("gamerYToffi");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < str.length(); i++) {
                sb.append(colors[(curColor + i) % colors.length].toString());
                sb.append(str.charAt(i));
            }
            tooltip.set(c, " " + I18n.format("attribute.modifier.equals.0", sb.toString() + TextFormatting.GRAY, ""));
            deleteOneAndSecongCharInPortugueseIdiome(tooltip, c);
        } else if (tipOfDisplay.endsWith("dono") && event.getItemStack().getTagCompound() != null) {
            if (player != null && player.getName().equals("gamerYToffi")) {
                if (event.getItemStack().getTagCompound().getString(ownerName).equals("gamerYToffi")) {
                    tooltip.set(c, " " + I18n.format("attribute.modifier.equals.0", String.valueOf(TextFormatting.GRAY), ""));
                    deleteOneAndSecongCharInPortugueseIdiome(tooltip, c);
                    return;
                }
            }
            String str = "Dono Atual: " + Objects.requireNonNull(event.getItemStack().getTagCompound()).getString(ownerName);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < str.length(); i++) {
                sb.append(colors2[(curColor + i) % colors2.length].toString());
                sb.append(str.charAt(i));
            }
            tooltip.set(c, " " + I18n.format("attribute.modifier.equals.0", sb.toString() + TextFormatting.GRAY, ""));
            deleteOneAndSecongCharInPortugueseIdiome(tooltip, c);
        } else if (tipOfDisplay.startsWith("Kaia")) {
            String str = "Kaia";
            String[] separation = null;
            StringBuilder sb = new StringBuilder();
            if (player != null && tagCompound != null && currentOwner.equals("gamerYToffi")) {
                str = "Kaia THE VERDADEIRA FORMA";
                separation = str.split("THE");
                String Kaia = separation[0];
                for (int i = 0; i < Kaia.length(); i++) {
                    sb.append(colors[(currentColorForNameItemKaia + (i * 50)) % colors.length].toString());
                    sb.append(Kaia.charAt(i));
                }
                String specialName = separation[1];
                for (int i = 0; i < specialName.length(); i++) {
                    sb.append(colors[(currentColorForNameItemKaia + (i * 0)) % colors.length].toString());
                    sb.append(specialName.charAt(i));
                }
            } else {
                for (int i = 0; i < str.length(); i++) {
                    sb.append(colors[(currentColorForNameItemKaia + (i * 50)) % colors.length].toString());
                    sb.append(str.charAt(i));
                }
            }
            tooltip.set(c, " " + sb.toString() + TextFormatting.GRAY);
        }
    }

    private static void deleteOneAndSecongCharInPortugueseIdiome(List<String> tooltip, int c) {
        if (tooltip.get(c).charAt(1) == ':') {
            String characterOne = tooltip.get(c);
            if (characterOne.length() > 1) {
                characterOne = characterOne.substring(2);
                tooltip.set(c, characterOne);
            }
        }
    }

    @SubscribeEvent
    public void ClientTick(ClientTickEvent event) {
        if (event.phase == Phase.START) {
            if (++tick >= 3) {
                tick = 0;
                if (--curColor < 0) {
                    curColor = colors.length - 1;
                }
            }
            if (++tickForNameItemKaia > 1) {
                tickForNameItemKaia = 0;
                if (--currentColorForNameItemKaia < 0) {
                    currentColorForNameItemKaia = colors.length - 5;
                }
            }
        }
    }
}