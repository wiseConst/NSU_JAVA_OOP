package FactoryObject;


import java.util.UUID;

public class Car extends FactoryObject {
    private BodyKit m_BodyKit = null;
    private Engine m_Engine = null;
    private Accessory m_Accessory = null;
    private UUID m_ID = UUID.randomUUID();


    public Car(BodyKit bodyKit, Engine engine, Accessory accessory) {
        m_BodyKit = bodyKit;
        m_Engine = engine;
        m_Accessory = accessory;
    }

    public BodyKit getBodyKit() {
        return m_BodyKit;
    }

    public Engine getEngine() {
        return m_Engine;
    }

    public Accessory getAccessory() {
        return m_Accessory;
    }

}
