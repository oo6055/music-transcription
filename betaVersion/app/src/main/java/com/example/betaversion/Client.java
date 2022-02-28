package com.example.betaversion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private Socket clientSocket;

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
    }

    public Socket getSock()
    {
        return clientSocket;
    }

    public void stopConnection() throws IOException {
        clientSocket.close();
    }

}
