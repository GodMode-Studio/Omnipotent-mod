package com.omnipotent.server.capability;

import java.util.ArrayList;
import java.util.List;

public class AntiEntitySpawn implements IAntiEntitySpawn {

    private List<Class> entitiesDontSpawnInWorld = new ArrayList<>();


    @Override
    public void dennySpawnInWorld(Class entityClass) {
        if (!entitiesDontSpawnInWorld.contains(entityClass))
            entitiesDontSpawnInWorld.add(entityClass);
    }

    @Override
    public void allowSpawnInWorld(Class entity) {
        entitiesDontSpawnInWorld.removeIf(entity1 -> entity1 == entity);
    }

    @Override
    public List<Class> entitiesDontSpawnInWorld() {
        return entitiesDontSpawnInWorld;
    }
}