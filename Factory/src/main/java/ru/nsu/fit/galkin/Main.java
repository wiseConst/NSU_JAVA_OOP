package ru.nsu.fit.galkin;

import Factory.Factory;
import GUI.FactoryObserver;

public class Main {
    private static final String s_FACTORY_CONFIG_PATH = "Factory/src/main/resources/factory.cfg";

    public static void main(String[] args) {

        Factory factory = new Factory(s_FACTORY_CONFIG_PATH);
        FactoryObserver factoryObserver = new FactoryObserver(factory);
    }
}