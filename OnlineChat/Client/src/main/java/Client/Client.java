package Client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket = null;
    private BufferedWriter bufferedWriter = null;
    private BufferedReader bufferedReader = null;
    private String username = null;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.username = username;

            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMessage() {
        try {
            bufferedWriter.write(username + ": ");
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                var messageToSend = scanner.nextLine();
                if (messageToSend.equals("bye")) {
                    System.out.println("bye");
                    closeEverything(socket, bufferedReader, bufferedWriter);
                    System.out.println("bye");
                    break;
                }

                bufferedWriter.write(username + ": " + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }

        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        //  remove();
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (socket.isConnected()) {
                    try {
                        var messageFromChat = bufferedReader.readLine();
                        if (messageFromChat != null)
                            System.out.println(messageFromChat);
                    } catch (IOException e) {
                        System.out.println("closin");
                        closeEverything(socket, bufferedReader, bufferedWriter);
                        break;
                    }

                }
            }
        }).start();


    }
}
