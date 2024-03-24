package Server;

import Utils.ClientHandler;

import java.io.IOException;
import java.net.*;

public class Server {

    private ServerSocket serverSocket = null;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {

                var socket = serverSocket.accept();
                System.out.println("Client has connected!");
                var clientHandler = new ClientHandler(socket);

                var thread = new Thread(clientHandler);
                thread.start();

            }
        } catch (IOException e) {

        }
    }

    public void closeSocketServer() {
        try {
            if (serverSocket != null)
                serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
