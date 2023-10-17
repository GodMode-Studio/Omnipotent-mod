package com.omnipotent.client.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public abstract class GuiScrollable extends GuiScreen implements IGuiScrollSystem {
    private String oldTextInSearchBox = "";
    private double currentScrollOffset = 1.0;
    private int maxScrollOffset = Integer.MAX_VALUE;
    private double targetScrollOffset = 1.0;
    private GuiTextField searchBar;

    public String getOldTextInSearchBox() {
        return oldTextInSearchBox;
    }

    public void setOldTextInSearchBox(String actualText) {
        this.oldTextInSearchBox = actualText;
    }

    public double getCurrentScrollOffset() {
        return currentScrollOffset;
    }

    public void setCurrentScrollOffset(double currentScrollOffset) {
        this.currentScrollOffset = currentScrollOffset;
    }

    public int getMaxScrollOffset() {
        return maxScrollOffset;
    }

    public void setMaxScrollOffset(int maxScrollOffset) {
        this.maxScrollOffset = maxScrollOffset;
    }

    public double getTargetScrollOffset() {
        return targetScrollOffset;
    }

    public void setTargetScrollOffset(double targetScrollOffset) {
        this.targetScrollOffset = targetScrollOffset;
    }

    public GuiTextField getSearchBar() {
        return searchBar;
    }

    public void setSearchBar(GuiTextField searchBar) {
        this.searchBar = searchBar;
    }

    public void updateScrollOffset(int scroll) {
        if (scroll == 0)
            return;
        double scrollFactor = 1.1;
        double newScrollOffset = currentScrollOffset + scroll * scrollFactor;
        newScrollOffset = Math.max(1.0, Math.min(maxScrollOffset, newScrollOffset));
        targetScrollOffset = newScrollOffset;
    }
}
