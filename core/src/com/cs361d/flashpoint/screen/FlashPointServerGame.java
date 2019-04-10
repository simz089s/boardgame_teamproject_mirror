package com.cs361d.flashpoint.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class FlashPointServerGame extends Game {

    Screen serverScreen;

    @Override
    public void create() {
        serverScreen = new ServerScreen(this);
        setServerScreen();
    }

    public void setServerScreen() {
        this.setScreen(serverScreen);
    }
}
