package Warehouse;

import Factory.Factory;
import FactoryObject.Accessory;
import FactoryObject.BodyKit;
import FactoryObject.Car;
import FactoryObject.Engine;
import Log.Log;
import ThreadPool.ThreadPool;
import FactoryObject.*;

import java.util.concurrent.ExecutorService;

public class DealersWarehouse extends Warehouse {

    private ThreadPool m_DealerPool = null;

    private Integer m_DelayMS = 0;

    private Integer m_WorkerCount = 0;

    public DealersWarehouse(String name, Integer capacity, Integer dealerCount, Integer delayMS) {
        super(name, capacity);

        m_WorkerCount = dealerCount;
        m_DealerPool = new ThreadPool(m_WorkerCount);
        m_DelayMS = delayMS;
    }

    public void Shutdown() {
        m_DealerPool.shutdown();
    }


    public void AcquireCars(AssemblerWarehouse assemblerWarehouse) {

        Runnable task = () -> {

            try {
                // wait and then wake up if requested.
                Thread.sleep(m_DelayMS);
                addPart(assemblerWarehouse.getFactoryObject());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        for (int i = 0; i < m_WorkerCount; ++i)
            m_DealerPool.submitTask(task);
    }

    public Integer getDelay() {
        return m_DelayMS;
    }

    public void setDelay(Integer delayMS) {
        m_DelayMS = delayMS;
    }
}
