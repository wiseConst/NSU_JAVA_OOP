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


    public void AcquireCars(AssemblerWarehouse assemblerWarehouse, boolean bLogSale) {
        if (!m_DealerPool.isValid()) return;

        Runnable task = () -> {
            if (Thread.interrupted()) return;

            try {
                Thread.sleep(m_DelayMS);

                var car = (Car) assemblerWarehouse.getFactoryObject();
                // addPart(car);

                if (bLogSale) {
                    Log.GetLogger().info("Dealer: " + Thread.currentThread().threadId() + ", Car: " + car.getID() + ", Engine: " + car.getEngine().getID() + ", BodyKit: " + car.getBodyKit().getID() + ", Accessory: " + car.getAccessory().getID());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // restore interrupted status
            }

            // New dealer arrived.
            // getFactoryObject();
        };

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // restore interrupted status
        }
        assemblerWarehouse.checkAvailableCars();

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
