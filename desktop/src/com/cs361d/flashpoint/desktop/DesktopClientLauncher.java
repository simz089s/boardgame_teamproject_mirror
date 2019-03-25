package com.cs361d.flashpoint.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.cs361d.flashpoint.networking.Client;
import com.cs361d.flashpoint.networking.NetworkManager;
import com.cs361d.flashpoint.networking.Server;

import java.io.IOException;

public class DesktopClientLauncher
{
    public static void main(String[] arg) throws IOException {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
//        new LwjglApplication(new FlashPointGame(), config);

        Client.createClient();
        new LwjglApplication(Client.clientFPGame, config);
        System.out.println();

        // change window size
        config.width = 1225;
        config.height = 675;
    }
}
