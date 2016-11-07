package server.server;

import java.net.Socket;

/**
 * Created by Daniel on 2016/11/07.
 */
public class Player
{
    private Socket socket;
    private String yourName;
    private String oppName;

    public Socket getSocket() {
        return socket;
    }

    public String getYourName() {
        return yourName;
    }

    public String getOppName() {
        return oppName;
    }

    public Player(Socket socket, String yourName, String oppName) {

        this.socket = socket;
        this.yourName = yourName;
        this.oppName = oppName;
    }
}
