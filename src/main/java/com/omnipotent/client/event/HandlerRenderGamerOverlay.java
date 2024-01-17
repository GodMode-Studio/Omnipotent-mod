package com.omnipotent.client.event;

import com.omnipotent.util.KaiaUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.util.Optional;

import static com.omnipotent.constant.NbtBooleanValues.showInfo;

public class HandlerRenderGamerOverlay {
    private int displayWidth;
    private int displayHeight;
    private FontRenderer fontRenderer;
    private int spacing = 0;

    @SubscribeEvent //em teste
    public void renderGuiPlayer(RenderGameOverlayEvent.Text event) {
//        Minecraft minecraft = Minecraft.getMinecraft();
//        renderKaiaInfo(minecraft);
    }

    @SubscribeEvent
    public void renderGuiPlayer(RenderGameOverlayEvent.Post event) {
//        Minecraft minecraft = Minecraft.getMinecraft();
//        renderKaiaInfo(minecraft);
    }

    @SubscribeEvent
    public void renderGuiPlayer(RenderGameOverlayEvent.Pre event) {
//        Minecraft minecraft = Minecraft.getMinecraft();
//        renderKaiaInfo(minecraft);
    }

    private void renderKaiaInfo(Minecraft mc) {
//        EntityPlayerSP player = mc.player;
//        displayHeight = mc.displayHeight;
//        displayWidth = mc.displayWidth;
//        GuiScreen currentScreen = mc.currentScreen;
//        NBTTagCompound tagCompound = getNbtTag(player, currentScreen);
//        if (tagCompound == null) return;
//        ArrayList<String> values = new ArrayList<>();
//        for (NbtBooleanValues key : NbtBooleanValues.values()) {
//            if (!(key.getValue().equals(showInfo.getValue()) || key.getValue().equals(playersWhoShouldNotKilledInCounterAttack.getValue()) || key.getValue().equals(playerDontKillInDirectAttack.getValue())))
//                values.add(TextFormatting.GOLD + I18n.format("guikaia.config." + key.getValue()) + ": " + TextFormatting.BLUE + tagCompound.getBoolean(key.getValue()) + " ");
//        }
//        fontRenderer = mc.fontRenderer;
//        spacing = 0;
//        Collections.reverse(values);
//        float topY = 0;
//        for (int c = 0; c < values.size(); c++) {
//            topY = drawDescriptionAndGetLastElement(values.get(c));
//        }
////        createBackground(topY);
    }

    private void createBackground(float topy) {
        GL11.glPushMatrix();
        Gui.drawRect((int) 200, (int) topy, (int) displayWidth, displayHeight, Color.MAGENTA.getRGB());
        GL11.glPopMatrix();
    }

    private static Optional<NBTTagCompound> getNbtTag(EntityPlayerSP player, GuiScreen currentScreen) {
        if (player == null || currentScreen != null) return Optional.empty();
        Optional<ItemStack> kaiaInMainHand = KaiaUtil.getKaiaInMainHand(player);
        if (!kaiaInMainHand.isPresent()) return Optional.empty();
        NBTTagCompound tagCompound = kaiaInMainHand.get().getTagCompound();
        if (tagCompound == null) return Optional.empty();
        if (!tagCompound.getBoolean(showInfo.getValue())) return Optional.empty();
        return Optional.of(tagCompound);
    }

    private float drawDescriptionAndGetLastElement(String description) {
        float y = 0;
        int maxWidth = (int) (displayWidth / 11.0344827586);
        List<String> strings = fontRenderer.listFormattedStringToWidth(description, maxWidth);
        for (String string : strings) {
            y = (float) (displayHeight / 2.05) - spacing;
            fontRenderer.drawStringWithShadow(string, (float) (displayHeight / 1.3), y, Color.WHITE.getRGB());
            spacing += fontRenderer.FONT_HEIGHT;
        }
        return y;
    }
}
