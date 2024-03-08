package FactoryObject;

import java.util.UUID;

public class FactoryObject {

    protected UUID m_ID = UUID.randomUUID();

    protected FactoryObject() {
    }

    public UUID getID() {
        return m_ID;
    }
}
