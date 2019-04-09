package com.cs361d.flashpoint.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.cs361d.flashpoint.networking.Client;
import com.cs361d.flashpoint.networking.NetworkManager;
import com.cs361d.flashpoint.networking.Server;

import java.util.Scanner;

public class DesktopServerLauncher {
    public static void main(String[] arg) {
        // Check if you should connect as a server or a client
        String publicIP = NetworkManager.getMyPublicIP();
        if (publicIP.equals(NetworkManager.DEFAULT_SERVER_IP)) {
            Server.createServer();
        } else {
            System.err.println("Wrong IP. Using public IP from whatismyip.com : " + publicIP);
            Client.serverIP = publicIP;
            System.err.println("Please tell your clients to update the server IP.");
            Server.createServer();
        }

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        new LwjglApplication(Server.serverFPGame, config);
        System.out.println();

        // change window size
        config.width = 613;
        config.height = 338;
    }
}
