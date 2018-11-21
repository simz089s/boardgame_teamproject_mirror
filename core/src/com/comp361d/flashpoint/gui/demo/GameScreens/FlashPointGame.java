package com.comp361d.flashpoint.gui.demo.GameScreens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.comp361d.flashpoint.gui.demo.GameScreens.LobbyScreen;
import com.comp361d.flashpoint.gui.demo.GameScreens.LoginScreen;

public class FlashPointGame extends Game {

    Screen loginScreen;
    Screen lobbyScreen;

    @Override
    public void create() {
        loginScreen = new LoginScreen(this);
        lobbyScreen = new LobbyScreen(this);

        this.setScreen(loginScreen);
    }
}