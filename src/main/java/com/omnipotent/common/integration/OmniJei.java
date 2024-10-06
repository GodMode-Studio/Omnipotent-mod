package com.omnipotent.common.integration;

import com.omnipotent.common.specialgui.ContainerKaia;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

import java.util.ArrayList;
import java.util.List;

@JEIPlugin
public class OmniJei implements IModPlugin {

    @Override
    public void register(IModRegistry registry) {
        IRecipeTransferRegistry recipeTransferRegistry = registry.getRecipeTransferRegistry();
        recipeTransferRegistry.addRecipeTransferHandler(new IRecipeTransferInfo<ContainerKaia>() {
            @Override
            public Class<ContainerKaia> getContainerClass() {
                return ContainerKaia.class;
            }

            @Override
            public String getRecipeCategoryUid() {
                return VanillaRecipeCategoryUid.CRAFTING;
            }

            @Override
            public boolean canHandle(ContainerKaia container) {
                return true;
            }

            @Override
            public List<Slot> getRecipeSlots(ContainerKaia container) {
                List<Slot> slots = new ArrayList<>();
                for (int i = 1; i < 10; i++) {
                    Slot slot = container.getSlot(i);
                    slots.add(slot);
                }
                return slots;
            }

            @Override
            public List<Slot> getInventorySlots(ContainerKaia container) {
                List<Slot> slots = new ArrayList<>();
                for (int i = 10; i < 126; i++) {
                    Slot slot = container.getSlot(i);
                    slots.add(slot);
                }
                return slots;
            }
        });
    }
}
