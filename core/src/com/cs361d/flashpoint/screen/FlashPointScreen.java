package com.cs361d.flashpoint.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public abstract class FlashPointScreen implements Screen {

    double gameWidth = Gdx.graphics.getWidth();
    double gameHeight = Gdx.graphics.getHeight();

    static FlashPointGame game;

    static Skin skinUI = new Skin(Gdx.files.internal("data/uiskin.json"));

    Label debugLbl = new Label("", skinUI);

    static final String[][] ACCOUNTS = {
            {"Username", "Password"},
            {"Simon", "1234"},
            {"Elvric", "1234"},
            {"Jacques", "1234"},
            {"David", "1234"},
            {"Daniel", "1234"},
            {"Mat", "1234"}
    };

    static boolean searchDB(String usr, String pwd) {
        for (String[] account : ACCOUNTS) if (account[0].equals(usr) && account[1].equals(pwd)) return true;
        return false;
    }

    FlashPointScreen(Game pGame) {
        game = (FlashPointGame) pGame;
    }

    @Override
    public void dispose() {
        skinUI.dispose();
    }
}
