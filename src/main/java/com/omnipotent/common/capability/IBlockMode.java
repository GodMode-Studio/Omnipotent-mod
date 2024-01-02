package com.omnipotent.common.capability;

public interface IBlockMode {
    public void setBlockNoEditMode(boolean block);
    public void setBlockCreativeMode(boolean block);
    public boolean getBlockNoEditMode();
    public boolean getBlockCreativeMode();
}
