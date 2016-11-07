package server.processing;

import server.networking.PlayerOneNetworkHandler;
import server.networking.PlayerTwoNetworkHandler;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Daniel on 2016/11/02.
 */
public class GameHandler implements Runnable
{
    private PlayerOneNetworkHandler blackPlayer;
    private PlayerTwoNetworkHandler whitePlayer;
    private int currentTurn = 0;

    public GameHandler(Socket white, Socket black) throws IOException
    {
        System.out.println("Joining matches");
        this.whitePlayer = new PlayerTwoNetworkHandler(white);
        this.blackPlayer = new PlayerOneNetworkHandler(black);
        System.out.println("Sockets assigned");
    }


    @Override
    public void run() {
        boolean running = true;
        while (running) {
            System.out.println("Game control");
            if (currentTurn == 0) {
                try {
                    blackPlayer.sendImage(whitePlayer.getImage());
                    String answer = blackPlayer.getAnswer();
                    if (answer.equalsIgnoreCase(whitePlayer.getQuestion()))
                    {
                        blackPlayer.addPoint();
                    }
                    blackPlayer.notifyTurn();
                    currentTurn = 1;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (currentTurn == 1) {
                try {
                    whitePlayer.sendImage(blackPlayer.getImage());
                    String answer = whitePlayer.getAnswer();
                    if (answer.equalsIgnoreCase(blackPlayer.getQuestion()))
                    {
                        whitePlayer.addPoint();
                    }
                    whitePlayer.notifyTurn();
                    currentTurn = 0;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
