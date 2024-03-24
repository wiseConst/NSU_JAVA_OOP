package ru.nsu.fit.galkin;

import Server.Server;

import java.io.*;
import java.net.*;

public class Main {
    public static void main(String[] args) throws IOException {

        Server server = new Server(new ServerSocket(1234));
        server.startServer();
        server.closeSocketServer();

    }
}