package ru.nsu.fit.galkin;

import Config.ClientConfig;
import UI.ClientLogin;

import java.io.IOException;

public class Main {
    private static final String clientConfigPath = "client.cfg";

    public static void main(String[] args) throws IOException {

        ClientConfig.load(clientConfigPath);
        ClientLogin app = new ClientLogin();

    }

}