package com.omnipotent.server.capability;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnbanEntities implements IUnbanEntities {

    private List<Class> entitiesCannotBannable = new ArrayList<>();

    @Override
    public void unmarkEntityAsUnbanable(Class entity) {
        entitiesCannotBannable.remove(entity);
    }

    @Override
    public void markEntitiAsUnBanable(Class entity) {
        if (!entitiesCannotBannable.contains(entity))
            entitiesCannotBannable.add(entity);
    }

    @Override
    public List<Class> entitiesCannotBannable() {
        return Collections.unmodifiableList(entitiesCannotBannable);
    }
}
