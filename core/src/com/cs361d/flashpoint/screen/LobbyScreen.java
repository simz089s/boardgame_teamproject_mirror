package com.cs361d.flashpoint.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.cs361d.flashpoint.networking.Client;
import com.cs361d.flashpoint.networking.ServerCommands;

import java.io.File;
import java.util.ArrayList;

public class LobbyScreen extends FlashPointScreen {

    SpriteBatch batch;
    static BoardDialog boardDialog;
    Texture txtrBG;
    Sprite spriteBG;

    // load games label
    Label loadGamesLabel;

    Label btnIndicationLabel;

    // load saved game list
    ScrollPane scrollPaneLoadGameList;
    ScrollPane.ScrollPaneStyle scrollStyle2;
    List<String> lstLoadGames;
    List.ListStyle listStyle2;
    String[] availableGames = {""};

    ImageButton btnLogout, btnJoin, btnLoad, btnCreateGame;

    Stage stage;

    private Music BGM = Gdx.audio.newMusic(Gdx.files.internal("playlist/void.mp3"));

    LobbyScreen(Game pGame) {
        super(pGame);
        BGM.setLooping(true);
    }

    @Override
    public void show() {

        BGM.play();

        stage = new Stage();
        batch = new SpriteBatch();
        boardDialog = new BoardDialog(stage);
        txtrBG = new Texture("lobby.png");
        spriteBG = new Sprite(txtrBG);
        //spriteBG.setScale(0.6f);
        spriteBG.setPosition(
                0, 0);

        createSavedGamesLabel();

        //create saved games list (TO LOAD)
        ArrayList<String> savedGames = listFilesOfSavedGames();
        String[] savedGamesArr = savedGames.toArray(new String[savedGames.size()]);
        createSavedGamesList(savedGamesArr);

        // exit button
        createLogoutButton();

        // create join button
        createJoinGameButton();

        // load game button
        createLoadGameButton();

        // create game button
        createCreateGameButton();

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


    // buttons

    // label

    private void createLogoutLabel() {
        btnIndicationLabel = new Label("LOGOUT", skinUI);
        btnIndicationLabel.setColor(Color.BLACK);

        btnIndicationLabel.setPosition(
                1010,
                (Gdx.graphics.getHeight() - 90));

        stage.addActor(btnIndicationLabel);
    }

    private void createLogoutButton() {
        Texture myTexture = new Texture(Gdx.files.internal("icons/logoutBtn.png"));
        TextureRegion myTextureRegion = new TextureRegion(myTexture);
        TextureRegionDrawable myTexRegionDrawable = new TextureRegionDrawable(myTextureRegion);

        btnLogout = new ImageButton(myTexRegionDrawable);

        btnLogout.setWidth(50);
        btnLogout.setHeight(50);

        final float x = Gdx.graphics.getWidth() - btnLogout.getWidth() - 95;
        final float y = Gdx.graphics.getHeight() - btnLogout.getHeight() * 2;

        btnLogout.setPosition(
                x,
                y);

        btnLogout.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
//                        NetworkManager.getInstance().sendCommand(Commands.DISCONNECT,"");
                        BGM.stop();
                        game.setScreen(game.loginScreen);
                    }
                });

        btnLogout.addListener(new ClickListener() {
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                createLogoutLabel();
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                btnIndicationLabel.remove();
            }
        });

        stage.addActor(btnLogout);
    }

    private void createJoinLabel() {
        btnIndicationLabel = new Label("JOIN GAME", skinUI);
        btnIndicationLabel.setColor(Color.BLACK);

        btnIndicationLabel.setPosition(
                1020,
                (Gdx.graphics.getHeight() - 130));

        stage.addActor(btnIndicationLabel);
    }

    private void createJoinGameButton() {
        Texture myTexture = new Texture(Gdx.files.internal("icons/joinGameBtn.png"));
        TextureRegion myTextureRegion = new TextureRegion(myTexture);
        TextureRegionDrawable myTexRegionDrawable = new TextureRegionDrawable(myTextureRegion);


        btnJoin = new ImageButton(myTexRegionDrawable);
        btnJoin.setWidth(50);
        btnJoin.setHeight(50);

        final float x = Gdx.graphics.getWidth() - btnJoin.getWidth() - 65;
        final float y = Gdx.graphics.getHeight() - btnJoin.getHeight() * 2 - 43;

        btnJoin.setPosition(
                x, y);

        btnJoin.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            BGM.stop();
            Client.getInstance().sendCommand(ServerCommands.JOIN, "");
          }
        });

        btnJoin.addListener(new ClickListener() {
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                createJoinLabel();
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                btnIndicationLabel.remove();
            }
        });

        stage.addActor(btnJoin);
    }

    private void createLoadLabel() {
        btnIndicationLabel = new Label("LOAD GAME", skinUI);
        btnIndicationLabel.setColor(Color.BLACK);

        btnIndicationLabel.setPosition(
                1120,
                (Gdx.graphics.getHeight() - 180));

        stage.addActor(btnIndicationLabel);
    }

    private void createLoadGameButton() {
        Texture myTexture = new Texture(Gdx.files.internal("icons/loadGameBtn.png"));
        TextureRegion myTextureRegion = new TextureRegion(myTexture);
        TextureRegionDrawable myTexRegionDrawable = new TextureRegionDrawable(myTextureRegion);

        btnLoad = new ImageButton(myTexRegionDrawable);

        btnLoad.setWidth(50);
        btnLoad.setHeight(50);

        final float x = Gdx.graphics.getWidth() - btnLoad.getWidth() - 8;
        final float y = Gdx.graphics.getHeight() - btnLoad.getHeight() * 2 - 50;

        btnLoad.setPosition(
                x, y);

        btnLoad.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (lstLoadGames.getSelected() != null ){
                            Client.getInstance().sendCommand(ServerCommands.LOAD_GAME,lstLoadGames.getSelected());
                        }
                    }
                });

        btnLoad.addListener(new ClickListener() {
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                createLoadLabel();
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                btnIndicationLabel.remove();
            }
        });

        stage.addActor(btnLoad);
    }

    private void createCreationLabel() {
        btnIndicationLabel = new Label("CREATE GAME", skinUI);
        btnIndicationLabel.setColor(Color.BLACK);

        btnIndicationLabel.setPosition(
                1020,
                (Gdx.graphics.getHeight() - 40));

        stage.addActor(btnIndicationLabel);
    }

    private void createCreateGameButton() {
        Texture myTexture = new Texture(Gdx.files.internal("icons/createGameBtn.png"));
        TextureRegion myTextureRegion = new TextureRegion(myTexture);
        TextureRegionDrawable myTexRegionDrawable = new TextureRegionDrawable(myTextureRegion);

        btnCreateGame = new ImageButton(myTexRegionDrawable);

        btnCreateGame.setWidth(100);
        btnCreateGame.setHeight(100);

        final float x = Gdx.graphics.getWidth() - btnCreateGame.getWidth();
        final float y = Gdx.graphics.getHeight() - btnCreateGame.getHeight();

        btnCreateGame.setPosition(
                x, y);

        btnCreateGame.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        BGM.stop();
                        game.setScreen(game.createGameScreen);
                    }
                });

        btnCreateGame.addListener(new ClickListener() {
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                createCreationLabel();
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                btnIndicationLabel.remove();
            }
        });

        stage.addActor(btnCreateGame);
    }

    // label

    private void createSavedGamesLabel() {
        loadGamesLabel = new Label("Saved games:", skinUI);
        loadGamesLabel.setFontScale(1.5f);
        loadGamesLabel.setColor(Color.BLACK);

        loadGamesLabel.setPosition(
                50,
                (Gdx.graphics.getHeight() - debugLbl.getHeight() - 50));

        stage.addActor(loadGamesLabel);
    }

    // saved games list

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
                50,
                Gdx.graphics.getHeight() - scrollPaneLoadGameList.getHeight() - 80);

        stage.addActor(scrollPaneLoadGameList);
    }



    // helper

    // get saved games from file system
    public ArrayList<String> listFilesOfSavedGames() {

        ArrayList<String> filesArr = new ArrayList<String>();
        File folder = new File("db");

        for (final File fileEntry : folder.listFiles()) {
            String filename = fileEntry.getName();
            if (fileEntry.isFile() && !filename.equals("map1.json") && !filename.equals("map2.json")) {
                int pos = filename.lastIndexOf(".");
                if (pos > 0) {
                    filename = filename.substring(0, pos);
                }
                filesArr.add(filename);
            }
        }

        return filesArr;
    }

    public static void resetLobbyScreen() {
        if (game.getScreen() == game.lobbyScreen) {
            game.setScreen(game.lobbyScreen);
        }
    }

    public static BoardDialog getDialog() {
        return boardDialog;
    }

}
