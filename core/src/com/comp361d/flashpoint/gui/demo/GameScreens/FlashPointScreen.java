package com.comp361d.flashpoint.gui.demo.GameScreens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public abstract class FlashPointScreen implements Screen {

    FlashPointGame game;

    Skin skinUI = new Skin(Gdx.files.internal("core/assets/data/uiskin.json"));

    FlashPointScreen(Game pGame) {
        game = (FlashPointGame) pGame;
    }

    @Override
    public void dispose() {
        skinUI.dispose();
    }
}
