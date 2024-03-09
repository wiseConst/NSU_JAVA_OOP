package Warehouse;

import FactoryObject.*;
import ThreadPool.ThreadPool;

public class AssemblerWarehouse extends Warehouse {

    private ThreadPool m_WorkerPool = null;
    private Integer m_WorkerCount = 0;

    private PartsWarehouse m_EngineWarehouse = null;
    private PartsWarehouse m_BodyKitWarehouse = null;
    private PartsWarehouse m_AccessoryWarehouse = null;

    private boolean m_bDealersRequestedCars = false;
    private Thread m_Controller = null;

    public AssemblerWarehouse(String name, Integer capacity, Integer workerCount) {
        super(name, capacity);

        m_WorkerCount = workerCount;
        m_WorkerPool = new ThreadPool(m_WorkerCount);
        m_Controller = new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (m_bDealersRequestedCars) {
                        MakeCars();
                    } else {
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt(); // restore interrupted status
                        }
                    }
                }
            }
        };
        m_Controller.start();
    }

    public void MakeCars() {
        if (!m_WorkerPool.isValid()) return;

        Runnable task = () -> {
            if (Thread.interrupted()) return;

            Car car = new Car((BodyKit) m_BodyKitWarehouse.getFactoryObject(), (Engine) m_EngineWarehouse.getFactoryObject(), (Accessory) m_AccessoryWarehouse.getFactoryObject());
            addPart(car);
        };

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // restore interrupted status
        }
        for (int i = 0; i < m_WorkerCount; ++i)
            m_WorkerPool.submitTask(task);
    }

    public synchronized void checkAvailableCars() {
        m_bDealersRequestedCars = getCurrentStorageSize().equals(0);
    }

    public void Shutdown() {
        m_Controller.interrupt();
        m_WorkerPool.shutdown();
    }

    public void setBodyKitWarehouse(PartsWarehouse mBodyKitWarehouse) {
        m_BodyKitWarehouse = mBodyKitWarehouse;
    }

    public void setEngineWarehouse(PartsWarehouse mEngineWarehouse) {
        m_EngineWarehouse = mEngineWarehouse;
    }

    public void setAccessoryWarehouse(PartsWarehouse mAccessoryWarehouse) {
        m_AccessoryWarehouse = mAccessoryWarehouse;
    }
}
