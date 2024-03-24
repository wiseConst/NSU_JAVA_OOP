package Utils;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    private static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private BufferedReader bufferedReader = null;
    private BufferedWriter bufferedWriter = null;
    private String clientUsername = null;
    private Socket clientSocket = null;

    public ClientHandler(Socket socket) {
        try {
            clientSocket = socket;

            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            clientUsername = bufferedReader.readLine();

            // Shared for multiple threads
            synchronized (clientHandlers) {
                clientHandlers.add(this);
            }

            broadcastMessage("SERVER: " + clientUsername + " has entered the chat!");
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {

        while (clientSocket.isConnected()) {
            try {
                var messageFromClient = bufferedReader.readLine();
                System.out.println("SERVER LOCAL: " + messageFromClient);
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                closeEverything(clientSocket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    private void broadcastMessage(String message) {

        for (var clientHandler : clientHandlers) {
            if (clientHandler == this || clientHandler.equals(clientUsername))
                continue; // No need to broadcast to yourself.

            try {
                clientHandler.bufferedWriter.write(message);
                clientHandler.bufferedWriter.newLine();
                clientHandler.bufferedWriter.flush();
            } catch (IOException e) {
                closeEverything(clientSocket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void remove() {
        synchronized (clientHandlers) {
            clientHandlers.remove(this);
        }
        broadcastMessage("SERVER: " + clientUsername + " has left the chat!");
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        remove();
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
