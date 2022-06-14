package com.example.betaversion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * The client Object
 *
 * @author Ori Ofek <oriofek106@gmail.com>
 * @version 1
 * @since 21 /4/2021 this object contains the client
 */
public class Client {
    private Socket clientSocket;

    /**
     * Start connection.
     *
     * @param ip   the ip that I want to connect to
     * @param port the port that i want to connect
     */
    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
    }

    /**
     * Gets sock.
     *
     * @return the socket
     */
    public Socket getSock()
    {
        return clientSocket;
    }


    /**
     * Stop connection.
     *
     */
    public void stopConnection() throws IOException {
        clientSocket.close();
    }

}
