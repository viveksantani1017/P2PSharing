package com.example.p2psharing.networking;

import android.content.Context;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Locale;

public class Client
{
    private Socket socket;
    private Context context;

    public Client(Context context)
    {
        this.context = context;
    }

    Client(Socket socket)
    {
        this.socket = socket;
    }

    public void setContext(Context context)
    {
        this.context = context;
    }

    public void connect(String ip, int port) throws IOException
    {
        socket = new Socket(ip, port);
    }

    public void sendFile(String filePath) throws IOException
    {
        if (!socket.isConnected())
            throw new IOException("Socket not connected.");

        File file = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(file);

        int MAX_PART_SIZE = 1500;
        int bytesRead = 0;
        int fileLength = (int)file.length();

        String headerMessage = String.format(new Locale("English"), "%255s|%10d", file.getName(), fileLength);
        byte[] headerBytes = headerMessage.getBytes();

        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(headerBytes, 0, headerBytes.length);

        while (bytesRead < fileLength)
        {
            int bytesToRead = Math.min(fileLength - bytesRead, MAX_PART_SIZE);

            byte[] bytes = new byte[bytesToRead];
            int actualBytesRead = fileInputStream.read(bytes, 0, bytesToRead);

            bytesRead += actualBytesRead;
            outputStream.write(bytes, 0, bytes.length);
        }

        fileInputStream.close();
    }

    public void receiveFile(String location) throws IOException
    {
        final int BUFFER_SIZE = 1500;
        InputStream inputStream = socket.getInputStream();

        byte[] headerBytes = new byte[266];
        int headerBytesRead = 0;

        while (headerBytesRead < headerBytes.length)
            headerBytesRead += inputStream.read(headerBytes, headerBytesRead, headerBytes.length - headerBytesRead);

        String header = new String(headerBytes);
        String[] headerParts = header.split("\\|");

        String fileName = headerParts[0].trim();
        FileOutputStream receivedFIStream = new FileOutputStream(String.format(new Locale("English"), "%s/%s", location, fileName));

        int fileLength = Integer.parseInt(headerParts[1].trim());
        int fileBytesRead = 0;

        while (fileBytesRead < fileLength)
        {
            byte[] fileBytes = new byte[BUFFER_SIZE];
            int bytesRead = inputStream.read(fileBytes, 0, BUFFER_SIZE);

            receivedFIStream.write(fileBytes, 0, bytesRead);
            fileBytesRead += bytesRead;
        }

        receivedFIStream.close();
    }

    public void disconnect() throws IOException
    {
        if (socket.isConnected())
            socket.close();
    }
}
