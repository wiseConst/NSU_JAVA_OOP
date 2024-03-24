package ru.nsu.fit.galkin;

import Client.Client;

import java.io.*;
import java.net.*;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {

        // ports free: [1024, 49151]

        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter your username for group chat: ");
            var username = scanner.nextLine();
            var socket = new Socket("localhost", 1234);

            if (socket != null) {
                var client = new Client(socket, username);
                client.listenForMessage();
                client.sendMessage();
            }
        } catch (IOException e) {
            System.out.println("Failed to open socket: " + e.getMessage());
        }

    }
}