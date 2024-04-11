package ru.nsu.fit.galkin;

import Config.ServerConfig;
import Server.Server;
import Log.*;

import java.io.*;
import java.net.*;

public class Main {
    private static final String serverConfigPath = "server.cfg";

    public static void main(String[] args) throws IOException {
        ServerConfig.load(serverConfigPath);
        Log.setIsLogging(ServerConfig.getLogEvents());

        final var port = ServerConfig.getPort();
        if (port < 1024 || port > 49151) {
            Log.logError("Port is not in valid range! [1024, 49151].");
            return;
        }

        Server server = new Server(new ServerSocket(port), ServerConfig.getServerTimeout(), ServerConfig.getClientTimeout());
        server.startServer();
        server.closeSocketServer();
    }
}