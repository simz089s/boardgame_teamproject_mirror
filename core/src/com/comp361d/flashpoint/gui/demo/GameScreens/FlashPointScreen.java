package com.comp361d.flashpoint.gui.demo.GameScreens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public abstract class FlashPointScreen implements Screen {

    double gameWidth = Gdx.graphics.getWidth();
    double gameHeight = Gdx.graphics.getHeight();

    FlashPointGame game;

    Skin skinUI = new Skin(Gdx.files.internal("core/assets/data/uiskin.json"));

    Label debugLbl = new Label("", skinUI);

    static final String[][] ACCOUNTS = {
            {"Username", "Password"},
            {"Simon", "notunderrated"},
            {"Elvric", "BGod"},
            {"Jacques", "corp"},
            {"David", "notdaniel"},
            {"Daniel", "notdavid"},
            {"Mat", "hematics"}
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
