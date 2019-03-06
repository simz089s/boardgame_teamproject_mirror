package com.cs361d.flashpoint.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class FlashPointGame extends Game {

    public Screen loginScreen;
    public Screen lobbyScreen;
    public Screen createGameScreen;
    public Screen chatScreen;
    public Screen statsScreen;
    public Screen boardScreen;

    @Override
    public void create() {
        loginScreen = new LoginScreen(this);
        lobbyScreen = new LobbyScreen(this);
        createGameScreen = new CreateGameScreen(this);
        chatScreen = new ChatScreen(this);
        statsScreen = new StatsScreen(this);
        boardScreen = new BoardScreen(this);

        this.setScreen(lobbyScreen);
    }
}
