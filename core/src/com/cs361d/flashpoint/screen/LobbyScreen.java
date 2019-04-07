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
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.cs361d.flashpoint.manager.DBHandler;
import com.cs361d.flashpoint.manager.User;
import com.cs361d.flashpoint.networking.Client;
import com.cs361d.flashpoint.networking.ServerCommands;
import java.util.ArrayList;

public class LobbyScreen extends FlashPointScreen {

    SpriteBatch batch;
    static BoardDialog boardDialog;
    Texture txtrBG;
    Sprite spriteBG;

    // load games label
    Label savedGamesLabel, btnHintsLabel, loadedGameLabel, numPlayersLeftToJoinLabel ;

    // load saved game list
    static ScrollPane scrollPaneLoadGameList, scrollPaneGameInfoList;
    static ScrollPane.ScrollPaneStyle scrollStyle;
    static List<String> lstLoadGames, lstGameInfoPanel;
    static List.ListStyle listStyle;

    static String loadedGameName = "";
    static String numPlayersLeftToJoin = "";
    static String[] gameNamesArr = null;
    static ArrayList<ScrollPane> gameInfoPanelList = new ArrayList<ScrollPane>();

    ImageButton btnLogout, btnJoin, btnLoad, btnCreateGame;

    static Stage stage;

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

        createLoadedGameLabel();
        createNumPlayersLeftToJoinLabel();

        createSavedGamesLabel();
        createSavedGamesList();

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
        btnHintsLabel = new Label("LOGOUT", skinUI);
        btnHintsLabel.setColor(Color.BLACK);

        btnHintsLabel.setPosition(
                1010,
                (Gdx.graphics.getHeight() - 90));

        stage.addActor(btnHintsLabel);
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
                btnHintsLabel.remove();
            }
        });

        stage.addActor(btnLogout);
    }

    private void createJoinLabel() {
        btnHintsLabel = new Label("JOIN GAME", skinUI);
        btnHintsLabel.setColor(Color.BLACK);

        btnHintsLabel.setPosition(
                1030,
                (Gdx.graphics.getHeight() - 150));

        stage.addActor(btnHintsLabel);
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
            Client.getInstance().sendCommand(ServerCommands.JOIN, User.getInstance().getName());
          }
        });

        btnJoin.addListener(new ClickListener() {
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (btnHintsLabel != null) btnHintsLabel.remove();
                createJoinLabel();
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                btnHintsLabel.remove();
            }
        });

        stage.addActor(btnJoin);
    }

    private void createLoadLabel() {
        btnHintsLabel = new Label("LOAD GAME", skinUI);
        btnHintsLabel.setColor(Color.BLACK);

        btnHintsLabel.setPosition(
                1120,
                (Gdx.graphics.getHeight() - 180));

        stage.addActor(btnHintsLabel);
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
                        if (lstLoadGames.getSelected() != null ) {
                            BGM.stop();
                            Client.getInstance().sendCommand(ServerCommands.LOAD_GAME,lstLoadGames.getSelected());
                        }
                    }
                });

        btnLoad.addListener(new ClickListener() {
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (btnHintsLabel != null) btnHintsLabel.remove();
                createLoadLabel();
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                btnHintsLabel.remove();
            }
        });

        stage.addActor(btnLoad);
    }

    private void createCreationLabel() {
        btnHintsLabel = new Label("CREATE GAME", skinUI);
        btnHintsLabel.setColor(Color.BLACK);

        btnHintsLabel.setPosition(
                1020,
                (Gdx.graphics.getHeight() - 40));

        stage.addActor(btnHintsLabel);
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
                btnHintsLabel.remove();
            }
        });

        stage.addActor(btnCreateGame);
    }

    // loaded game

    public static void setLoadedGameName(String game) {
        loadedGameName = game;
    }

    private void createLoadedGameLabel() {
        loadedGameLabel = new Label("Current active game:  " + loadedGameName, skinUI);
        loadedGameLabel.setFontScale(1.5f);
        loadedGameLabel.setColor(Color.FIREBRICK);

        loadedGameLabel.setPosition(
                20, 280);

        stage.addActor(loadedGameLabel);
    }

    public static void setNumPlayersLeftToJoin(int numPlayers) {
        numPlayersLeftToJoin = numPlayers == -1 ? "" : "" + numPlayers;
    }

    private void createNumPlayersLeftToJoinLabel() {
        numPlayersLeftToJoinLabel = new Label("Number of players left to join:  " + numPlayersLeftToJoin, skinUI);
        numPlayersLeftToJoinLabel.setFontScale(1.5f);
        numPlayersLeftToJoinLabel.setColor(Color.FIREBRICK);

        numPlayersLeftToJoinLabel.setPosition(
                20, 230);

        stage.addActor(numPlayersLeftToJoinLabel);
    }

    private void createSavedGamesLabel() {
        savedGamesLabel = new Label("Saved games:", skinUI);
        savedGamesLabel.setFontScale(1.5f);
        savedGamesLabel.setColor(Color.WHITE);

        savedGamesLabel.setPosition(
                20,
                (Gdx.graphics.getHeight() - debugLbl.getHeight() - 50));

        stage.addActor(savedGamesLabel);
    }

    // saved games list

    public static void setSavedGames(ArrayList<String> games) {
        gameNamesArr = games.toArray(new String[games.size()]);
    }

    public static void createSavedGamesList() {
        // list style
        listStyle = new List.ListStyle();
        listStyle.font = Font.get(25); // font size
        listStyle.fontColorUnselected = Color.WHITE;
        listStyle.fontColorSelected = Color.WHITE;
        listStyle.selection = TextureLoader.getDrawable(50, 100, Color.LIGHT_GRAY );


        lstLoadGames = new List<String>(listStyle);
        lstLoadGames.setItems(gameNamesArr);

        // scrollPane style
        scrollStyle = new ScrollPane.ScrollPaneStyle();
        scrollStyle.vScrollKnob = TextureLoader.getDrawable(15, 15, Color.DARK_GRAY);
        scrollStyle.vScroll = TextureLoader.getDrawable(15, 15, Color.LIGHT_GRAY);

        scrollPaneLoadGameList = new ScrollPane(lstLoadGames, scrollStyle);
        scrollPaneLoadGameList.setOverscroll(false, false);
        scrollPaneLoadGameList.setFadeScrollBars(false);
        scrollPaneLoadGameList.setScrollingDisabled(true, false);
        scrollPaneLoadGameList.setTransform(true);
        scrollPaneLoadGameList.setScale(1.0f);
        scrollPaneLoadGameList.setWidth(300);
        scrollPaneLoadGameList.setHeight(333);

        float x = 200;
        float y = Gdx.graphics.getHeight() - scrollPaneLoadGameList.getHeight() - 20;

        scrollPaneLoadGameList.setPosition(
                x, y);

        lstLoadGames.addListener(
                new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        removeGameInfoElements();
                        createGameInfoPanel(lstLoadGames.getSelected());
                        return true;
                    }
                });

        stage.addActor(scrollPaneLoadGameList);
    }

    private static void createGameInfoPanel(String gameName) {
        // list style
        listStyle = new List.ListStyle();
        listStyle.font = Font.get(22); // font size
        listStyle.fontColorUnselected = Color.WHITE;
        listStyle.fontColorSelected = Color.WHITE;
        listStyle.selection = TextureLoader.getDrawable(50, 100, Color.CLEAR );

        lstGameInfoPanel = new List<String>(listStyle);
        lstGameInfoPanel.setItems(DBHandler.getInfoForLobbyGameOnSelect(gameName));

        // scrollPane style
        scrollStyle = new ScrollPane.ScrollPaneStyle();
        scrollStyle.vScrollKnob = TextureLoader.getDrawable(15, 15, Color.DARK_GRAY);
        scrollStyle.vScroll = TextureLoader.getDrawable(15, 15, Color.LIGHT_GRAY);

        scrollPaneGameInfoList = new ScrollPane(lstGameInfoPanel, scrollStyle);
        scrollPaneGameInfoList.setOverscroll(false, false);
        scrollPaneGameInfoList.setScrollingDisabled(true, false);
        scrollPaneGameInfoList.setTransform(true);
        scrollPaneGameInfoList.setScale(1.0f);
        scrollPaneGameInfoList.setWidth(Gdx.graphics.getWidth() / 2 - 10);
        scrollPaneGameInfoList.setHeight(300);

        float x = Gdx.graphics.getWidth() / 2 + 60;
        float y = 100;

        scrollPaneGameInfoList.setPosition(
                x, y);

        gameInfoPanelList.add(scrollPaneGameInfoList);
        stage.addActor(scrollPaneGameInfoList);
    }



    // helper



    public static void resetLobbyScreen() {
        if (game.getScreen() == game.lobbyScreen) {
            game.setScreen(game.lobbyScreen);
        }
    }

    public static BoardDialog getDialog() {
        return boardDialog;
    }

    private static void removeGameInfoElements() {
        for (int i = 0; i < gameInfoPanelList.size(); i++) {
            gameInfoPanelList.get(i).remove();
        }

        gameInfoPanelList.clear();
    }

}
