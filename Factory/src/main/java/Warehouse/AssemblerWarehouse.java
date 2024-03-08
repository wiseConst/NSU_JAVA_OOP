package Warehouse;

import FactoryObject.*;
import Log.Log;
import ThreadPool.ThreadPool;

public class AssemblerWarehouse extends Warehouse {

    private ThreadPool m_WorkerPool = null;
    private Integer m_WorkerCount = 0;

    public AssemblerWarehouse(String name, Integer capacity, Integer workerCount) {
        super(name, capacity);

        m_WorkerCount = workerCount;
        m_WorkerPool = new ThreadPool(m_WorkerCount);
    }

    public void MakeCars(PartsWarehouse bodykitWH, PartsWarehouse engineWH, PartsWarehouse accessoryWH) {
        Runnable task = () -> {

            //try {
            // wait and then wake up if requested.
            //      Thread.sleep(m_DelayMS);
            Car car = new Car((BodyKit) bodykitWH.getFactoryObject(), (Engine) engineWH.getFactoryObject(), (Accessory) accessoryWH.getFactoryObject());
            addPart(car);

            Log.GetLogger().info("Dealer: " + Thread.currentThread().threadId() + ", Car: " + car.getID() + ", Engine: " + car.getEngine().getID() + ", BodyKit: " + car.getBodyKit().getID() + ", Accessory: " + car.getAccessory().getID());

          /*  } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }*/

        };

        // 1 part per delay
        for (int i = 0; i < m_WorkerCount; ++i)
            m_WorkerPool.submitTask(task);
    }

    public void Shutdown() {
        m_WorkerPool.shutdown();
    }

}
