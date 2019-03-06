package com.cs361d.flashpoint.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.cs361d.flashpoint.Networking.Client;
import com.cs361d.flashpoint.Networking.Server;
import com.cs361d.flashpoint.view.FlashPointGame;
import java.net.InetAddress;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        new LwjglApplication(new FlashPointGame(), config);


        if(Server.getMyIPAddress().equals(Server.SERVER_IP)){
            //start server
//            Server server = new Server();

        }
        else
        {
//            Client client = new Client();
        }
        // change window size
        config.width = 1225;
        config.height = 675;
    }
}
