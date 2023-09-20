package com.omnipotent.server.capability;

public interface IBlockMode {
    public void setBlockNoEditMode(boolean block);
    public void setBlockCreativeMode(boolean block);
    public boolean getBlockNoEditMode();
    public boolean getBlockCreativeMode();
}
