package server.processing;

import server.networking.PlayerOneNetworkHandler;
import server.networking.PlayerTwoNetworkHandler;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Daniel on 2016/11/02.
 */
public class GameHandler implements Runnable {
    private PlayerOneNetworkHandler blackPlayer;
    private PlayerTwoNetworkHandler whitePlayer;
    private int currentTurn = 0;

    /**
     * Default constructor for the game room and server handles
     *
     * @param white player ones socket
     * @param black player twos socket
     * @throws IOException if sockets are bad
     */
    public GameHandler(Socket white, Socket black) throws IOException {
        System.out.println("Joining matches");
        this.whitePlayer = new PlayerTwoNetworkHandler(white);
        this.blackPlayer = new PlayerOneNetworkHandler(black);
        System.out.println("Sockets assigned");
    }

    /**
     * Thread that handles game by sending images, answers, points and wins between clients
     */
    @Override
    public void run() {
        boolean running = true;
        while (running) {
            System.out.println("Game control");
            if (currentTurn == 0) {//player 1
                try {
                    blackPlayer.sendImage(whitePlayer.getImage());
                    whitePlayer.receiveQuestion();
                    String answer = blackPlayer.getAnswer();
                    if (answer.equalsIgnoreCase(whitePlayer.getQuestion())) {
                        blackPlayer.addPoint();
                        checkWinner();
                    }
                    blackPlayer.sendPoints(blackPlayer.getPoints(), whitePlayer.getPoints());
                    whitePlayer.sendPoints(whitePlayer.getPoints(), blackPlayer.getPoints());
                    blackPlayer.notifyTurn();
                    currentTurn = 1;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (currentTurn == 1) {// player 2
                try {
                    whitePlayer.sendImage(blackPlayer.getImage());
                    blackPlayer.receiveQuestion();
                    String answer = whitePlayer.getAnswer();
                    if (answer.equalsIgnoreCase(blackPlayer.getQuestion())) {
                        whitePlayer.addPoint();
                        checkWinner();
                    }
                    whitePlayer.sendPoints(whitePlayer.getPoints(), blackPlayer.getPoints());
                    blackPlayer.sendPoints(blackPlayer.getPoints(), whitePlayer.getPoints());
                    whitePlayer.notifyTurn();
                    currentTurn = 0;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Check win condition every time a point is added if a winner is determined it notifies players
     */
    private void checkWinner() {
        if (blackPlayer.getPoints() >= 10) {
            blackPlayer.sendWin();
            whitePlayer.sendLose();
        } else if (whitePlayer.getPoints() >= 10) {
            whitePlayer.sendWin();
            blackPlayer.sendLose();
        }
    }
}
