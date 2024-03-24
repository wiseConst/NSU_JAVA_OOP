package Warehouse;

import FactoryObject.*;
import ThreadPool.ThreadPool;

public class AssemblerWarehouse extends Warehouse {

    private ThreadPool m_WorkerPool = null;
    private Integer m_WorkerCount = 0;

    private PartsWarehouse m_EngineWarehouse = null;
    private PartsWarehouse m_BodyKitWarehouse = null;
    private PartsWarehouse m_AccessoryWarehouse = null;

    private Object m_ControllerLock = null;
    private Thread m_Controller = null;

    public AssemblerWarehouse(String name, Integer capacity, Integer workerCount) {
        super(name, capacity);

        m_WorkerCount = workerCount;
        m_WorkerPool = new ThreadPool(m_WorkerCount);

        m_ControllerLock = new Object();
        m_Controller = new Thread() {
            @Override
            public void run() {
                synchronized (m_ControllerLock) {
                    while (true) {
                        try {
                            m_ControllerLock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt(); // restore interrupted status
                        }
                        MakeCars();
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
        if (getCurrentStorageSize() > 0) return;

        synchronized (m_ControllerLock) {
            m_ControllerLock.notify();
        }
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
