package Factory;

import ConfigParser.ConfigParser;
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

    public Factory(String factoryConfigPath) {

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
    }

    public void Run() {

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

            m_Dealers.AcquireCars(m_AssemblerWarehouse);
        }

    }

    public void Shutdown() {
        m_bIsRunning = false;

        m_AssemblerWarehouse.Shutdown();

        m_BodyKitWarehouse.Shutdown();
        m_AccessoryWarehouse.Shutdown();
        m_EngineWarehouse.Shutdown();
    }

    public Integer getTotalCarsProduced() {
        return m_AssemblerWarehouse.getTotalConstructedObjects();
    }

    public Integer getCurrentProducedCarsCount() {
        return m_AssemblerWarehouse.getCurrentStorageSize();
    }

    public Integer getCurrentProducedBodyKitsCount() {
        return m_BodyKitWarehouse.getCurrentStorageSize();
    }

    public Integer getCurrentProducedEngineCount() {
        return m_EngineWarehouse.getCurrentStorageSize();
    }

    public Integer getCurrentAccessoriesCount() {
        return m_AccessoryWarehouse.getCurrentStorageSize();
    }

    public void setEngineSupplierDelay(Integer delayMS) {
        m_EngineWarehouse.setDelay(delayMS);
    }

    public Integer getCurrentEngineSupplierDelay() {
        return m_EngineWarehouse.getDelay();
    }

    public void setBodyKitSupplierDelay(Integer delayMS) {
        m_BodyKitWarehouse.setDelay(delayMS);
    }

    public Integer getCurrentBodyKitSupplierDelay() {
        return m_BodyKitWarehouse.getDelay();
    }

    public void setDealerDelay(Integer delayMS) {
        m_Dealers.setDelay(delayMS);
    }

    public Integer getCurrentDealerDelay() {
        return m_Dealers.getDelay();
    }

    public Integer getCurrentAccessorySupplierDelay() {
        return m_AccessoryWarehouse.getDelay();
    }

    public void setAccessorySupplierDelay(Integer delayMS) {
        m_AccessoryWarehouse.setDelay(delayMS);
    }

}