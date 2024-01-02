package com.omnipotent.common.capability;

public class BlockModePlayer implements IBlockMode {

    private boolean blockCreativeMode;

    private boolean blockNoEditMode;


    @Override
    public void setBlockCreativeMode(boolean block) {
        blockCreativeMode = block;
    }

    @Override
    public void setBlockNoEditMode(boolean block) {
        blockNoEditMode = block;
    }

    @Override
    public boolean getBlockCreativeMode(){
        return blockCreativeMode;
    }

    @Override
    public boolean getBlockNoEditMode(){
        return blockNoEditMode;
    }
}
