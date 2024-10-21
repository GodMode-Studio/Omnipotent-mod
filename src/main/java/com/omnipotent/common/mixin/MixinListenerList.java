package com.omnipotent.common.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.ListenerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ListenerList.class, remap = false)
public abstract class MixinListenerList {

    @Shadow private static ImmutableList<ListenerList> allLists;

    @Shadow public abstract void unregister(int id, IEventListener listener);

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static void unregisterAll(int id, IEventListener listener) {}
}
