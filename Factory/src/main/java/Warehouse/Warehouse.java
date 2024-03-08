package Warehouse;

import FactoryObject.FactoryObject;

import java.util.LinkedList;

public class Warehouse {

    private String m_Name = null;
    private Integer m_Capacity = 0;
    private Integer m_TotalConstructedObjects = 0;
    private LinkedList<FactoryObject> m_FactoryObjects = null;

    public Warehouse(String name, Integer capacity) {
        m_Name = name;
        m_Capacity = capacity;

        m_FactoryObjects = new LinkedList<>();
    }

    protected synchronized void addPart(FactoryObject factoryObject) {
        while (m_FactoryObjects.size() >= m_Capacity) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        m_FactoryObjects.add(factoryObject);
        notifyAll();
        ++m_TotalConstructedObjects;
    }

    public synchronized FactoryObject getFactoryObject() {
        while (m_FactoryObjects.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        FactoryObject factoryObject = m_FactoryObjects.removeFirst();
        notifyAll();
        return factoryObject;
    }

    public String getName() {
        return m_Name;
    }

    public void Shutdown() {
    }

    public Integer getTotalConstructedObjects() {
        return m_TotalConstructedObjects;
    }

    public synchronized Integer getCurrentStorageSize() {
        return m_FactoryObjects.size();
    }

}
