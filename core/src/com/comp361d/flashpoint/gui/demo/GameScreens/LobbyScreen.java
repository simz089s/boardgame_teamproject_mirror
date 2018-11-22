package com.comp361d.flashpoint.gui.demo.GameScreens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class LobbyScreen extends FlashPointScreen {

    SpriteBatch batch;

    Texture txtrBG;
    Sprite spriteBG;

    ScrollPane sp;
    List<String> lstGames;

    TextButton btnJoin;

    Stage stage;

    LobbyScreen(Game pGame) {
        super(pGame);
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        debugLbl.setText("LOBBY/CHAT SCREEN");
        debugLbl.setPosition(
                (Gdx.graphics.getWidth() - debugLbl.getWidth()) / 2f - 75f,
                (Gdx.graphics.getHeight() - debugLbl.getHeight()) / 2f);
        debugLbl.setColor(Color.ROYAL);

        txtrBG = new Texture("core/assets/fire_rescue.png");
        spriteBG = new Sprite(txtrBG);
        spriteBG.setScale(0.6f);
        spriteBG.setPosition(
                -(Gdx.graphics.getWidth() / 2f) - 125, -(Gdx.graphics.getHeight() / 2f) + 30);

        lstGames = new List<String>(skinUI);
        String[] games = {"Game A", "Game B", "Game C", "Game D", "Game E", "Game F", "..."};
        lstGames.setItems(games);
        sp = new ScrollPane(lstGames);
        sp.setSmoothScrolling(false);
        sp.setTransform(true);
        sp.setScale(0.5f);
        sp.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() + 100);
        sp.setPosition(
                Gdx.graphics.getWidth() / 2f - sp.getWidth() / 4,
                Gdx.graphics.getHeight() / 2f - sp.getHeight() / 4);

        btnJoin = new TextButton("Join game", skinUI, "default");
        btnJoin.setWidth(100);
        btnJoin.setHeight(25);
        btnJoin.setPosition(
                (Gdx.graphics.getWidth() - btnJoin.getWidth()) / 2,
                Gdx.graphics.getHeight() / 5f - (btnJoin.getHeight() / 2));
        btnJoin.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        btnJoin.setText("Joining...");
                        debugLbl.setText(lstGames.getSelected());
                    }
                });

        stage = new Stage();
        stage.addActor(sp);
        stage.addActor(btnJoin);
        stage.addActor(debugLbl);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.disableBlending();
        spriteBG.draw(batch);
        batch.enableBlending();
        batch.end();

        batch.begin();
        stage.draw();
        batch.end();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        // skinUI.dispose();
        this.dispose();
        batch.dispose();
        stage.dispose();
    }
}
