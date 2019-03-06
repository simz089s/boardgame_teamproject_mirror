package com.cs361d.flashpoint.view;

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
        chatScreen = new ChatScreen(this);
        statsScreen = new StatsScreen(this);
        boardScreen = new BoardScreen(this);

        this.setScreen(lobbyScreen);
    }

    public Screen getLoginScreen() { return loginScreen; }

    public Screen getLobbyScreen() { return lobbyScreen; }

    public Screen getCreateGameScreen() { return createGameScreen; }

    public Screen getChatScreen() { return chatScreen; }

    public Screen getStatsScreen() { return statsScreen; }

    public Screen getBoardScreen() { return boardScreen; }


}
