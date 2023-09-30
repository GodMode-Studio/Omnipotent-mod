package com.omnipotent.server.capability;

import net.minecraft.entity.Entity;

import java.util.List;

public interface IAntiEntitySpawn {
    public void dennySpawnInWorld(Class entity);

    public void allowSpawnInWorld(Class entity);

    public List<Class> entitiesDontSpawnInWorld();
}
