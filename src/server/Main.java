package server;

import server.server.TCPForwarder;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Daniel on 2016/11/07.
 */
public class Main
{
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(7683);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            while(true)
            {
                Socket connection = serverSocket.accept();
                System.out.println("Connection receieved from " + connection.getInetAddress() );
                Runnable forward = new TCPForwarder(connection);
                forward.run();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
