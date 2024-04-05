package Server;

import ClientHandler.ClientHandler;

import java.io.IOException;
import java.net.*;
import java.util.Timer;
import java.util.UUID;

import Log.*;

public class Server {
    private ServerSocket serverSocket = null;
    private Integer clientTimeout = 0;
    private long lastServerActivity = 0;

    public Server(ServerSocket serverSocket, Integer clientTimeout) {
        this.serverSocket = serverSocket;
        this.clientTimeout = clientTimeout;
    }

    public void startServer() throws IOException {
        Log.logInfo("Server started!");

        lastServerActivity = System.currentTimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (ClientHandler.getCount() != 0) {
                        try {
                            Thread.sleep(2500);
                            continue;
                        } catch (InterruptedException e) {
                            Log.logWarn(e.getMessage());
                        }
                    }

                    // server no activity for 1 minute.
                    var diff = System.currentTimeMillis() - lastServerActivity;
                    if (diff > 60000) {
                        closeSocketServer();
                        return;
                    }
                }
            }
        }).start();

        while (!serverSocket.isClosed()) {
            try {
                var thread = new Thread(new ClientHandler(serverSocket.accept(), clientTimeout));
                thread.start();
                lastServerActivity = System.currentTimeMillis();
            } catch (IOException e) {
                Log.logWarn("Failed to accept new connection! " + e.getMessage());
            }
        }
    }

    public void closeSocketServer() {
        try {
            if (serverSocket != null)
                serverSocket.close();
        } catch (IOException e) {
            Log.logWarn("Failed to close socket server! " + e.getMessage());
        }
    }

}
