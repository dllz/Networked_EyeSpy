package server.networking;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Daniel on 2016/11/02.
 * This makes it a lot easier to keep track of tcp connections
 */
public class PlayerOneNetworkHandler extends GameNetworkHandler {
    /**
     * @param ConnectionToClient
     * @throws IOException
     * @see GameNetworkHandler
     */
    public PlayerOneNetworkHandler(Socket ConnectionToClient) throws IOException {
        super(ConnectionToClient);
    }
}
