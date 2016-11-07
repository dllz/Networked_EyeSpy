package server.server;

import server.processing.GameHandler;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Daniel on 2016/11/02.
 */
public class GameSetUp {
    static ArrayList<Player> client = new ArrayList<>();
    ;

    /**
     * Adds player to the waiting array
     *
     * @param s       the client socket
     * @param yName   the clients username
     * @param oppName the opponents username
     * @return true if added to array and false if failed or matched
     */
    public static boolean addPlayer(Socket s, String yName, String oppName) {
        boolean found = false;
        for (int i = 0; i < client.size(); i++) {
            System.out.println("Searching for matching game");
            if (client.get(i).getOppName().equals(yName)) {
                try {
                    System.out.println("Moving game to new thread");
                    Runnable game = new GameHandler(client.get(i).getSocket(), s);//match found, send to game room
                    game.run();
                    client.remove(i);
                    System.out.println("Players Matched");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!found) { //match not found, add to arry
            client.add(new Player(s, yName, oppName));
            System.out.println("Player added");
            return true;
        } else {
            return false;
        }
    }

    /**
     * Delete player from the array if they disconnect
     *
     * @param connect the socket of the player who needs to be removed
     */
    public static void removePlayer(Socket connect) {
        for (int i = 0; i < client.size(); i++) {

            if (client.get(i).getSocket().equals(connect)) {
                client.remove(i);
                System.out.println("Disconnected Player removed");
            }
        }
    }
}
