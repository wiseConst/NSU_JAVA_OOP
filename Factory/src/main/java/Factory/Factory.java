package Factory;

import ConfigParser.ConfigParser;
import Log.Log;
import ThreadPool.*;
import Warehouse.*;
import Warehouse.PartsWarehouse.*;

import java.nio.file.LinkOption;

import static java.lang.Thread.sleep;

public class Factory {

    // Warehouses
    private PartsWarehouse m_BodyKitWarehouse = null;
    private PartsWarehouse m_EngineWarehouse = null;
    private PartsWarehouse m_AccessoryWarehouse = null;
    private AssemblerWarehouse m_AssemblerWarehouse = null;
    private DealersWarehouse m_Dealers = null;

    private static Boolean m_bIsRunning = false;
    // Misc
    private Boolean m_bLogSale = false;
    private Integer m_DealerRequestIntervalMs = 1000;
    private Integer m_SupplierIntervalMs = 1000;
    private boolean m_bIsInitialized = false;

    public Factory(String factoryConfigPath) {

        try {
            ConfigParser configParser = new ConfigParser(factoryConfigPath);

            m_BodyKitWarehouse = new PartsWarehouse("BodyKitWarehouse", configParser.Get("StorageBodySize"), configParser.Get("BodySuppliers"), m_SupplierIntervalMs, ESupplyType.SUPPLY_TYPE_BODY_KIT);
            m_EngineWarehouse = new PartsWarehouse("EngineWarehouse", configParser.Get("StorageMotorSize"), configParser.Get("MotorSuppliers"), m_SupplierIntervalMs, ESupplyType.SUPPLY_TYPE_ENGINE);
            m_AccessoryWarehouse = new PartsWarehouse("AccessoryWarehouse", configParser.Get("StorageAccessorySize"), configParser.Get("AccessorySuppliers"), m_SupplierIntervalMs,
                    ESupplyType.SUPPLY_TYPE_ACCESSORY);

            m_AssemblerWarehouse = new AssemblerWarehouse("AssemblerWarehouse", configParser.Get("StorageAutoSize"), configParser.Get("Workers"));
            m_AssemblerWarehouse.setEngineWarehouse(m_EngineWarehouse);
            m_AssemblerWarehouse.setBodyKitWarehouse(m_BodyKitWarehouse);
            m_AssemblerWarehouse.setAccessoryWarehouse(m_AccessoryWarehouse);

            m_Dealers = new DealersWarehouse("Dealers", configParser.Get("Dealers"), configParser.Get("Dealers"), m_DealerRequestIntervalMs);
            m_bLogSale = configParser.Get("LogSale") == 1;
        } catch (Exception e) {
            Log.GetLogger().info(e.getMessage());

            if (m_AssemblerWarehouse != null)
                m_AssemblerWarehouse.Shutdown();

            if (m_BodyKitWarehouse != null)
                m_BodyKitWarehouse.Shutdown();

            if (m_AccessoryWarehouse != null)
                m_AccessoryWarehouse.Shutdown();

            if (m_EngineWarehouse != null)
                m_EngineWarehouse.Shutdown();

            if (m_Dealers != null)
                m_Dealers.Shutdown();

            return;
        }

        m_bIsInitialized = true;
    }

    public void Run() {
        if (!m_bIsInitialized) return;

        m_bIsRunning = true;
        while (m_bIsRunning) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            m_BodyKitWarehouse.ReplenishSupplies();
            m_EngineWarehouse.ReplenishSupplies();
            m_AccessoryWarehouse.ReplenishSupplies();

            m_Dealers.AcquireCars(m_AssemblerWarehouse, m_bLogSale);
        }

    }

    public void Shutdown() {
        if (!m_bIsInitialized) return;
        m_bIsRunning = false;

        m_AssemblerWarehouse.Shutdown();
        m_Dealers.Shutdown();

        m_BodyKitWarehouse.Shutdown();
        m_AccessoryWarehouse.Shutdown();
        m_EngineWarehouse.Shutdown();
    }

    public Integer getTotalCarsProduced() {
        if (m_AssemblerWarehouse == null) return 0;

        return m_AssemblerWarehouse.getTotalConstructedObjects();
    }

    public Integer getCurrentProducedCarsCount() {
        if (m_AssemblerWarehouse == null) return 0;

        return m_AssemblerWarehouse.getCurrentStorageSize();
    }

    public Integer getCurrentProducedBodyKitsCount() {
        if (m_BodyKitWarehouse == null) return 0;

        return m_BodyKitWarehouse.getCurrentStorageSize();
    }

    public Integer getCurrentProducedEngineCount() {
        if (m_EngineWarehouse == null) return 0;

        return m_EngineWarehouse.getCurrentStorageSize();
    }

    public Integer getCurrentAccessoriesCount() {
        if (m_AccessoryWarehouse == null) return 0;

        return m_AccessoryWarehouse.getCurrentStorageSize();
    }

    public void setEngineSupplierDelay(Integer delayMS) {
        m_EngineWarehouse.setDelay(delayMS);
    }

    public Integer getCurrentEngineSupplierDelay() {
        if (m_EngineWarehouse == null) return m_SupplierIntervalMs;

        return m_EngineWarehouse.getDelay();
    }

    public void setBodyKitSupplierDelay(Integer delayMS) {
        if (m_BodyKitWarehouse == null) return;

        m_BodyKitWarehouse.setDelay(delayMS);
    }

    public Integer getCurrentBodyKitSupplierDelay() {
        if (m_BodyKitWarehouse == null) return m_SupplierIntervalMs;

        return m_BodyKitWarehouse.getDelay();
    }

    public void setDealerDelay(Integer delayMS) {
        if (m_Dealers == null) return;

        m_Dealers.setDelay(delayMS);
    }

    public Integer getCurrentDealerDelay() {
        if (m_Dealers == null) return m_DealerRequestIntervalMs;

        return m_Dealers.getDelay();
    }

    public Integer getCurrentAccessorySupplierDelay() {
        if (m_AccessoryWarehouse == null) return m_SupplierIntervalMs;

        return m_AccessoryWarehouse.getDelay();
    }

    public void setAccessorySupplierDelay(Integer delayMS) {
        if (m_AccessoryWarehouse == null) return;

        m_AccessoryWarehouse.setDelay(delayMS);
    }

}