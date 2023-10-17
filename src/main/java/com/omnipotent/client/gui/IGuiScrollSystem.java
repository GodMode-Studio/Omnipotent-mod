package com.omnipotent.client.gui;

public interface IGuiScrollSystem {
    void updateScrollOffset(int scroll);
    void renderElements(int mouseX, int mouseY, float partialTicks);
}
