package com.cs361d.flashpoint.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.cs361d.flashpoint.networking.Client;
import com.cs361d.flashpoint.networking.NetworkManager;
import com.cs361d.flashpoint.networking.Server;

public class DesktopServerLauncher
{
    public static void main(String[] arg) {
//        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
//        new LwjglApplication(new FlashPointGame(), config);

        NetworkManager myNetwork = NetworkManager.getInstance(); // Create a single network
        Server server = null;

        //Check if you should connect as a server or a client
        if(myNetwork.getMyPublicIP().equals(myNetwork.DEFAULT_SERVER_IP)){
            server = Server.createServer(NetworkManager.DEFAULT_SERVER_PORT);
            myNetwork.addServer(server);
        }
        // change window size
//        config.width = 1225;
//        config.height = 675;
    }
}
