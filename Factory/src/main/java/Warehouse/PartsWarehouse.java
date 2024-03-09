package Warehouse;

import FactoryObject.*;
import Log.Log;
import ThreadPool.*;

import static java.lang.Thread.sleep;

public class PartsWarehouse extends Warehouse {

    public enum ESupplyType {
        SUPPLY_TYPE_ENGINE,
        SUPPLY_TYPE_BODY_KIT,
        SUPPLY_TYPE_ACCESSORY
    }

    private ThreadPool m_FactoryObjectPool = null;

    private Integer m_DelayMS = 0;

    private final ESupplyType m_SupplyType;
    private Integer m_WorkerCount = 0;

    public PartsWarehouse(String name, Integer capacity, Integer supplierCount, Integer supplierIntervalMS, ESupplyType supplyType) {
        super(name, capacity);

        m_WorkerCount = supplierCount;
        m_SupplyType = supplyType;
        m_DelayMS = supplierIntervalMS;
        m_FactoryObjectPool = new ThreadPool(m_WorkerCount);
    }

    public void ReplenishSupplies() {
        if (!m_FactoryObjectPool.isValid()) return;

        Runnable task = () -> {
            if (Thread.interrupted()) return;

            try {
                sleep(m_DelayMS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // restore interrupted status
            }

            switch (m_SupplyType) {
                case SUPPLY_TYPE_ENGINE:
                    addPart(new Engine());
                    break;
                case SUPPLY_TYPE_ACCESSORY:
                    addPart(new Accessory());
                    break;
                case SUPPLY_TYPE_BODY_KIT:
                    addPart(new BodyKit());
                    break;
            }

        };

        try {
            sleep(m_DelayMS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // restore interrupted status
        }
        for (int i = 0; i < m_WorkerCount; ++i)
            m_FactoryObjectPool.submitTask(task);
    }

    public void Shutdown() {
        m_FactoryObjectPool.shutdown();
    }

    public Integer getDelay() {
        return m_DelayMS;
    }

    public void setDelay(Integer delayMS) {
        m_DelayMS = delayMS;
    }

}
