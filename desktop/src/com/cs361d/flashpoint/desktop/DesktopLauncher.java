package com.cs361d.flashpoint.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.cs361d.flashpoint.networking.Client;
import com.cs361d.flashpoint.networking.NetworkManager;
import com.cs361d.flashpoint.networking.Server;
import com.cs361d.flashpoint.view.FlashPointGame;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
//        new LwjglApplication(new FlashPointGame(), config);

        NetworkManager myNetwork = NetworkManager.getInstance(); // Create a single network

    //Check if you should connect as a server or a client
        if(myNetwork.getMyPublicIP().equals(myNetwork.SERVER_IP)){
            //start server with its Game instance
            Server server = new Server();
            myNetwork.addServer(server);
            new LwjglApplication(server.serverFPGame, config);
        }
        else {
            Client client = new Client();
            myNetwork.addNewClient(client);
            new LwjglApplication(client.clientFPGame, config);
        }
        // change window size
        config.width = 1225;
        config.height = 675;
    }
}
