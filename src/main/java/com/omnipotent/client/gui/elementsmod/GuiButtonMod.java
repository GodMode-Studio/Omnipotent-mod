package com.omnipotent.client.gui.elementsmod;

import net.minecraft.client.gui.GuiButton;

public class GuiButtonMod extends GuiButton {
    public Runnable runnable;

    public GuiButtonMod(int buttonId, int x, int y, String buttonText) {
        super(buttonId, x, y, buttonText);
    }

    public GuiButtonMod(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }

    public GuiButtonMod(int buttonId, int x, int y, String buttonText, Runnable runnable) {
        super(buttonId, x, y, buttonText);
        this.runnable = runnable;
    }

    public GuiButtonMod(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, Runnable runnable) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        this.runnable = runnable;
    }
}
