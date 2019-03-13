package com.cs361d.flashpoint.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class FlashPointGame extends Game {

    Screen loginScreen;
    Screen lobbyScreen;
    Screen createGameScreen;
    Screen chatScreen;
    Screen statsScreen;
    Screen boardScreen;

    @Override
    public void create() {
        loginScreen = new LoginScreen(this);
        lobbyScreen = new LobbyScreen(this);
        createGameScreen = new CreateGameScreen(this);
        statsScreen = new StatsScreen(this);
        boardScreen = new BoardScreen(this);

        this.setScreen(loginScreen);
    }

    public void setLobbyScreen() { this.setScreen(lobbyScreen); }

}
