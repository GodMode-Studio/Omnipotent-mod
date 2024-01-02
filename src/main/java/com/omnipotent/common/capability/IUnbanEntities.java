package com.omnipotent.common.capability;

import java.util.List;

public interface IUnbanEntities {

    public void unmarkEntityAsUnbanable(Class entity);

    public void markEntitiAsUnBanable(Class entity);

    public List<Class> entitiesCannotBannable();

}
