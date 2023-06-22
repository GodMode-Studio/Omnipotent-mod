package com.omnipotent.server.damage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nullable;

public class AbsoluteOfCreatorDamage extends EntityDamageSource {

    public AbsoluteOfCreatorDamage(@Nullable Entity damageSourceEntityIn) {
        super("ABSOLUTE OF CREATOR", damageSourceEntityIn);
    }
    @Override
    public ITextComponent getDeathMessage(EntityLivingBase entity) {
        ItemStack itemstack = damageSourceEntity instanceof EntityLivingBase ? ((EntityLivingBase) damageSourceEntity).getHeldItem(EnumHand.MAIN_HAND) : null;
        TextFormatting darkPurple = TextFormatting.DARK_PURPLE;
        String fallen = ""+TextFormatting.BLACK+TextFormatting.ITALIC+TextFormatting.OBFUSCATED;
        TextFormatting red = TextFormatting.RED;
        TextFormatting darkRed = TextFormatting.DARK_RED;
        String namePlayerDead = entity.getDisplayName().getFormattedText();
        String currentLanguage = FMLCommonHandler.instance().getCurrentLanguage();
        String s;
        if(currentLanguage.equals("pt_br")){
            s = darkRed+namePlayerDead+darkPurple+" MORTO POR " +fallen+ " S@#@#@#"+ TextFormatting.GRAY +" ABSOLUTE OF CREATOR";
        }else{
            s = darkRed+namePlayerDead+darkPurple+" KILLED BY " +fallen+ " S@#@#@#"+ TextFormatting.GRAY +" ABSOLUTE OF CREATOR";
        }
        return new TextComponentTranslation(s, namePlayerDead, itemstack.getDisplayName());
    }

}
