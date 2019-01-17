package com.cs361d.flashpoint.GameScreens;

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

public class StatsScreen extends FlashPointScreen {

    SpriteBatch batch;

    Texture txtrBG;
    Sprite spriteBG;

    // available games label
    Label statsLabel;

    // join available games list
    ScrollPane scrollPaneJoinGameList;
    ScrollPane.ScrollPaneStyle scrollStyle;
    List<String> lstJoinGames;
    List.ListStyle listStyle;

    TextButton btnExit;
    TextButton btnBackToGame;

    Stage stage;

    StatsScreen(Game pGame) {
        super(pGame);
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        txtrBG = new Texture("stats.png");
        spriteBG = new Sprite(txtrBG);
        //spriteBG.setScale(0.6f);
        spriteBG.setPosition(
                0, 0);

        // create labels
        createStatsTitleLabel();

        // exit button
        createExitButton();

        // create game button
        createBackToGameButton();

        //create available games list (TO JOIN)
        String[] gamesStatsArr = {"\t\t[Team stats]", "Walls left: X/20", "Victims saved: Y/7", "Victims lost: Z/5", "",
                "[My info]", "Accumulated AP: 1", "Special AP: 0", "Specialist: Fire Captain", "Firefighter color: red", "# victims saved: 2", "",
                "[Player 2 info]", "Accumulated AP: 2", "Special AP: 1", "Specialist: Rescue specialist", "Firefighter color: yellow", "# victims saved: 0", "",
                "[Player 3 info]", "Accumulated AP: 0", "Special AP: 1", "Specialist: Paramedic", "Firefighter color: blue", "# victims saved: 0"
        };

        createGameStatsList(gamesStatsArr);

        // button listeners
        btnExit.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        game.setScreen(game.loginScreen);
                    }
                });


        btnBackToGame.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        game.setScreen(game.chatScreen);
                    }
                });

        stage = new Stage();
        stage.addActor(btnExit);
        stage.addActor(scrollPaneJoinGameList);
        stage.addActor(btnBackToGame);
        stage.addActor(statsLabel);

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
        stage.act(); // scroll bar activation
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

    private void createStatsTitleLabel() {

        statsLabel = new Label("STATISTICS:", skinUI);
        statsLabel.setPosition(
                5,
                (Gdx.graphics.getHeight() - debugLbl.getHeight() - 40));
        statsLabel.setColor(Color.LIGHT_GRAY);
    }

    private void createExitButton() {
        btnExit = new TextButton("Exit", skinUI, "default");
        btnExit.setWidth(100);
        btnExit.setHeight(25);
        btnExit.setPosition(
                (Gdx.graphics.getWidth() - btnExit.getWidth() - 8),
                (Gdx.graphics.getHeight() - btnExit.getHeight() - 8));
    }

    private void createBackToGameButton() {
        btnBackToGame = new TextButton("Back to game", skinUI, "default");
        btnBackToGame.setWidth(150);
        btnBackToGame.setHeight(25);
        btnBackToGame.setPosition(
                (Gdx.graphics.getWidth() - btnBackToGame.getWidth()) / 2,
                Gdx.graphics.getHeight() / 5f - (btnBackToGame.getHeight() / 2) - 40);
    }

    private void createGameStatsList(String[] messages) {
        // list style
        listStyle = new List.ListStyle();
        listStyle.font = Font.get(25); // font size
        listStyle.fontColorUnselected = Color.WHITE;
        listStyle.fontColorSelected = Color.WHITE;
        listStyle.selection = TextureLoader.getDrawable(100, 100, Color.CLEAR );

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
        scrollPaneJoinGameList.setWidth(Gdx.graphics.getWidth() - 20);
        scrollPaneJoinGameList.setHeight(Gdx.graphics.getHeight() - 120);
        //scrollMessage.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() + 100);
        scrollPaneJoinGameList.setPosition(
                10,
                Gdx.graphics.getHeight() - scrollPaneJoinGameList.getHeight() - 45);
    }
}
