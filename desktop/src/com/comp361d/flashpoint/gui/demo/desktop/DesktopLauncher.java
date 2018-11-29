package com.comp361d.flashpoint.gui.demo.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.comp361d.flashpoint.gui.demo.GameScreens.FlashPointGame;

import com.comp361d.flashpoint.gui.demo.GameScreens.FlashPointGame;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = FlashPointGame.WIDTH;
        config.height = FlashPointGame.HEIGHT;
        config.title = FlashPointGame.TITLE;
        new LwjglApplication(new FlashPointGame(), config);
    }
}
