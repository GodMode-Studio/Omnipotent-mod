package com.omnipotent.client.gui.elementsmod;

import com.omnipotent.constant.NbtBooleanValues;
import com.omnipotent.constant.NbtNumberValues;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class GuiTextFieldMod extends GuiTextField {
    private String description;
    private boolean isSelected = false;
    private NbtBooleanValues value;
    private NbtNumberValues nbtNumberValue;


    public NbtNumberValues getNbtNumberValue() {
        return nbtNumberValue;
    }

    public void setNbtNumberValue(NbtNumberValues nbtNumberValue) {
        this.nbtNumberValue = nbtNumberValue;
    }


    public NbtBooleanValues getValue() {
        return value;
    }

    public void setValue(NbtBooleanValues value) {
        this.value = value;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public GuiTextFieldMod(int componentId, FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height, String description) {
        super(componentId, fontrendererObj, x, y, par5Width, par6Height);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GuiTextField)
            return this.getId() == ((GuiTextField) obj).getId();
        else
            return false;
    }
}
