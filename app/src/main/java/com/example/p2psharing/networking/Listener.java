package com.example.p2psharing.networking;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Listener
{
    private ServerSocket listener;

    public Client start(int port) throws IOException
    {
        if (listener != null)
        {
            listener.close();
            listener = null;
        }
        listener = new ServerSocket(port);
        Socket socket = listener.accept();

        return new Client(socket);
    }
}
