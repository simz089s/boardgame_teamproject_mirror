package com.comp361d.flashpoint.gui.demo.GameScreens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.comp361d.flashpoint.gui.demo.GameScreens.LobbyScreen;
import com.comp361d.flashpoint.gui.demo.GameScreens.LoginScreen;

public class FlashPointGame extends Game {

    public static final int WIDTH = 640;
    public static final int HEIGHT = 480;
    public static final String TITLE = "Flash Point";

    Screen loginScreen;
    Screen lobbyScreen;
    Screen chatScreen;
    Screen boardScreen;

    @Override
    public void create() {
        loginScreen = new LoginScreen(this);
        lobbyScreen = new LobbyScreen(this);
        chatScreen = new ChatScreen(this);
        boardScreen = new BoardScreen(this);

        this.setScreen(loginScreen);
    }
}
