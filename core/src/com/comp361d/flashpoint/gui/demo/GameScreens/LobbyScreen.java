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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;
import java.util.Arrays;

public class LobbyScreen extends FlashPointScreen {

    SpriteBatch batch;

    Texture txtrBG;
    Sprite spriteBG;

    // available games label
    Label availableGamesLabel;

    // load games label
    Label loadGamesLabel;

    // join available games list
    ScrollPane scrollPaneJoinGameList;
    ScrollPane.ScrollPaneStyle scrollStyle;
    List<String> lstJoinGames;
    List.ListStyle listStyle;

    // load saved game list
    ScrollPane scrollPaneLoadGameList;
    ScrollPane.ScrollPaneStyle scrollStyle2;
    List<String> lstLoadGames;
    List.ListStyle listStyle2;

    TextButton btnLogout;
    TextButton btnJoin;
    TextButton btnLoad;
    TextButton btnCreateGame;

    Stage stage;

    LobbyScreen(Game pGame) {
        super(pGame);
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        txtrBG = new Texture("lobby.png");
        spriteBG = new Sprite(txtrBG);
        //spriteBG.setScale(0.6f);
        spriteBG.setPosition(
                0, 0);

        // create labels
        createAvailGameLabel();
        createLoadGameLabel();

        // exit button
        createLogoutButton();

        // create join button
        createJoinGameButton();

        // load game button
        createLoadGameButton();

        // create game button
        createCreateGameButton();

        //create available games list (TO JOIN)
        String[] availableGames = {"Available game A", "Available game B"};
        createAvailableGamesList(availableGames);

        //create saved games list (TO LOAD)
        String[] savedGames = {"Saved game 1", "Saved game 2", "Saved game 3"};
        createSavedGamesList(savedGames);

        // button listeners
        btnLogout.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        game.setScreen(game.loginScreen);
                    }
                });

        btnJoin.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        game.setScreen(game.chatScreen);
                        btnJoin.setText("Joining...");
                        loadGamesLabel.setText(lstJoinGames.getSelected());
                    }
                });

        btnLoad.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        game.setScreen(game.chatScreen);
                    }
                });

        btnCreateGame.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        game.setScreen(game.createGameScreen);
                    }
                });

        stage = new Stage();
        stage.addActor(btnLogout);
        stage.addActor(scrollPaneJoinGameList);
        stage.addActor(scrollPaneLoadGameList);
        stage.addActor(btnJoin);
        stage.addActor(btnLoad);
        stage.addActor(btnCreateGame);
        stage.addActor(availableGamesLabel);
        stage.addActor(loadGamesLabel);

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
        stage.act();
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

    private void createAvailGameLabel() {

        availableGamesLabel = new Label("Available games:", skinUI);
        availableGamesLabel.setPosition(
                5,
                (Gdx.graphics.getHeight() - debugLbl.getHeight() - 40));
        availableGamesLabel.setColor(Color.BLACK);
    }

    private void createLoadGameLabel() {
        loadGamesLabel = new Label("Saved games:", skinUI);
        loadGamesLabel.setPosition(
                Gdx.graphics.getWidth() / 2,
                (Gdx.graphics.getHeight() - debugLbl.getHeight() - 40));
        loadGamesLabel.setColor(Color.BLACK);
    }

    private void createLogoutButton() {
        btnLogout = new TextButton("Logout", skinUI, "default");
        btnLogout.setWidth(100);
        btnLogout.setHeight(25);
        btnLogout.setPosition(
                (Gdx.graphics.getWidth() - btnLogout.getWidth() - 8),
                (Gdx.graphics.getHeight() - btnLogout.getHeight() - 8));
    }

    private void createJoinGameButton() {
        btnJoin = new TextButton("Join game", skinUI, "default");
        btnJoin.setWidth(100);
        btnJoin.setHeight(25);
        btnJoin.setPosition(
                Gdx.graphics.getWidth() / 4 - btnJoin.getWidth() / 2,
                Gdx.graphics.getHeight() / 5f - (btnJoin.getHeight() / 2));
    }

    private void createLoadGameButton() {
        btnLoad = new TextButton("Load game", skinUI, "default");
        btnLoad.setWidth(100);
        btnLoad.setHeight(25);
        btnLoad.setPosition(
                Gdx.graphics.getWidth() / 2 + Gdx.graphics.getWidth() / 4 - btnJoin.getWidth() / 2,
                Gdx.graphics.getHeight() / 5f - (btnLoad.getHeight() / 2));
    }

    private void createCreateGameButton() {
        btnCreateGame = new TextButton("Create game", skinUI, "default");
        btnCreateGame.setWidth(150);
        btnCreateGame.setHeight(25);
        btnCreateGame.setPosition(
                (Gdx.graphics.getWidth() - btnCreateGame.getWidth()) / 2,
                Gdx.graphics.getHeight() / 5f - (btnCreateGame.getHeight() / 2) - 40);
    }

    private void createAvailableGamesList(String[] messages) {
        // list style
        listStyle = new List.ListStyle();
        listStyle.font = Font.get(25); // font size
        listStyle.fontColorUnselected = Color.BLACK;
        listStyle.fontColorSelected = Color.BLACK;
        listStyle.selection = TextureLoader.getDrawable(50, 100, Color.SKY );

        lstJoinGames = new List<String>(listStyle);
        lstJoinGames.setItems(messages);

        // scrollPane style
        scrollStyle = new ScrollPane.ScrollPaneStyle();
        scrollStyle.vScrollKnob = TextureLoader.getDrawable(15, 15, Color.DARK_GRAY);
        scrollStyle.vScroll = TextureLoader.getDrawable(15, 15, Color.LIGHT_GRAY);

        scrollPaneJoinGameList = new ScrollPane(lstJoinGames, scrollStyle);
        scrollPaneJoinGameList.setOverscroll(false, false);
        scrollPaneJoinGameList.setFadeScrollBars(false);
        scrollPaneJoinGameList.setScrollingDisabled(true, false);
        scrollPaneJoinGameList.setTransform(true);
        scrollPaneJoinGameList.setScale(1.0f);
        scrollPaneJoinGameList.setWidth(Gdx.graphics.getWidth() / 2 - 15);
        scrollPaneJoinGameList.setHeight(Gdx.graphics.getHeight() - 100);
        //scrollMessage.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() + 100);
        scrollPaneJoinGameList.setPosition(
                10,
                Gdx.graphics.getHeight() - scrollPaneJoinGameList.getHeight() - 45);
    }

    private void createSavedGamesList(String[] messages) {
        // list style
        listStyle2 = new List.ListStyle();
        listStyle2.font = Font.get(25); // font size
        listStyle2.fontColorUnselected = Color.BLACK;
        listStyle2.fontColorSelected = Color.BLACK;
        listStyle2.selection = TextureLoader.getDrawable(50, 100, Color.YELLOW );

        lstLoadGames = new List<String>(listStyle2);
        lstLoadGames.setItems(messages);

        // scrollPane style
        scrollStyle2 = new ScrollPane.ScrollPaneStyle();
        scrollStyle2.vScrollKnob = TextureLoader.getDrawable(15, 15, Color.DARK_GRAY);
        scrollStyle2.vScroll = TextureLoader.getDrawable(15, 15, Color.LIGHT_GRAY);

        scrollPaneLoadGameList = new ScrollPane(lstLoadGames, scrollStyle2);
        scrollPaneLoadGameList.setOverscroll(false, false);
        //scrollPaneLoadGameList.setFadeScrollBars(false);
        scrollPaneLoadGameList.setScrollingDisabled(true, false);
        scrollPaneLoadGameList.setTransform(true);
        scrollPaneLoadGameList.setScale(1.0f);
        scrollPaneLoadGameList.setWidth(Gdx.graphics.getWidth() / 2 - 10);
        scrollPaneLoadGameList.setHeight(Gdx.graphics.getHeight() - 100);
        //scrollMessage.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() + 100);
        scrollPaneLoadGameList.setPosition(
                Gdx.graphics.getWidth() / 2,
                Gdx.graphics.getHeight() - scrollPaneLoadGameList.getHeight() - 45);
    }
}
