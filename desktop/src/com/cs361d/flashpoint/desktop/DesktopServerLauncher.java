package com.cs361d.flashpoint.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.cs361d.flashpoint.networking.Client;
import com.cs361d.flashpoint.networking.NetworkManager;
import com.cs361d.flashpoint.networking.Server;

public class DesktopServerLauncher
{
    public static void main(String[] arg) {
        //Check if you should connect as a server or a client
        if(NetworkManager.getMyPublicIP().equals(NetworkManager.DEFAULT_SERVER_IP)){
            Server.createServer();
        }
        // change window size
//        config.width = 1225;
//        config.height = 675;
    }
}
