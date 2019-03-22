package com.cs361d.flashpoint.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.cs361d.flashpoint.networking.Client;
import com.cs361d.flashpoint.networking.NetworkManager;
import com.cs361d.flashpoint.networking.Server;

public class DesktopClientLauncher
{
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
//        new LwjglApplication(new FlashPointGame(), config);

        NetworkManager myNetwork = NetworkManager.getInstance(); // Create a single network

        Client client = new Client(NetworkManager.DEFAULT_SERVER_IP, NetworkManager.DEFAULT_SERVER_PORT);
        myNetwork.addNewClient(client);
        new LwjglApplication(client.clientFPGame, config);
        // change window size
        config.width = 1225;
        config.height = 675;
    }
}
