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
    static ScrollPane scrollPaneLoadGameList;
    static ScrollPane scrollPaneGameInfoList;
    static ScrollPane.ScrollPaneStyle scrollStyle;
    static List<String> lstLoadGames;
    static List<String> lstGameInfoPanel;
    static List.ListStyle listStyle;

    static String[] array = null;
    static ArrayList<ScrollPane> gameInfoPanelList = new ArrayList<ScrollPane>();
    static ArrayList<Image> gameDiffImgList = new ArrayList<Image>();

    ImageButton btnLogout, btnJoin, btnLoad, btnCreateGame;

    static Image gameDifficultyImg;

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
            Client.getInstance().sendCommand(ServerCommands.JOIN, User.getInstance().getName());
          }
        });

        btnJoin.addListener(new ClickListener() {
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (btnIndicationLabel != null) btnIndicationLabel.remove();
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
                        if (lstLoadGames.getSelected() != null ) {
                            BGM.stop();
                            Client.getInstance().sendCommand(ServerCommands.LOAD_GAME,lstLoadGames.getSelected());
                        }
                    }
                });

        btnLoad.addListener(new ClickListener() {
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (btnIndicationLabel != null) btnIndicationLabel.remove();
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
                60,
                (Gdx.graphics.getHeight() - debugLbl.getHeight() - 50));

        stage.addActor(loadGamesLabel);
    }

    // saved games list

    public static void setSavedGames(ArrayList<String> games) {
        array = games.toArray(new String[games.size()]);
    }

    public static void createSavedGamesList() {
        // list style
        listStyle = new List.ListStyle();
        listStyle.font = Font.get(25); // font size
        listStyle.fontColorUnselected = Color.BLACK;
        listStyle.fontColorSelected = Color.BLACK;
        listStyle.selection = TextureLoader.getDrawable(50, 100, Color.YELLOW );


        lstLoadGames = new List<String>(listStyle);
        lstLoadGames.setItems(array);

        // scrollPane style
        scrollStyle = new ScrollPane.ScrollPaneStyle();
        scrollStyle.vScrollKnob = TextureLoader.getDrawable(15, 15, Color.DARK_GRAY);
        scrollStyle.vScroll = TextureLoader.getDrawable(15, 15, Color.LIGHT_GRAY);

        scrollPaneLoadGameList = new ScrollPane(lstLoadGames, scrollStyle);
        scrollPaneLoadGameList.setOverscroll(false, false);
        scrollPaneLoadGameList.setScrollingDisabled(true, false);
        scrollPaneLoadGameList.setTransform(true);
        scrollPaneLoadGameList.setScale(1.0f);
        scrollPaneLoadGameList.setWidth(380);
        scrollPaneLoadGameList.setHeight(Gdx.graphics.getHeight() - 250);

        float x = 60;
        float y = Gdx.graphics.getHeight() - scrollPaneLoadGameList.getHeight() - 80;

        scrollPaneLoadGameList.setPosition(
                x, y);

        lstLoadGames.addListener(
                new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        //removeGameInfoElements();
                        //createGameInfoPanel(lstLoadGames.getSelected());
                        //createGameDifficultyImg(lstLoadGames.getSelected());
                        return true;
                    }
                });

        stage.addActor(scrollPaneLoadGameList);
    }

    private static void createGameInfoPanel(String gameName) {
        // list style
        listStyle = new List.ListStyle();
        listStyle.font = Font.get(25); // font size
        listStyle.fontColorUnselected = Color.BLACK;
        listStyle.fontColorSelected = Color.BLACK;
        listStyle.selection = TextureLoader.getDrawable(50, 100, Color.CLEAR );

        lstGameInfoPanel = new List<String>(listStyle);
        lstGameInfoPanel.setItems(DBHandler.getInfoForLobbyGameOnSelect(gameName));

        // scrollPane style
        scrollStyle = new ScrollPane.ScrollPaneStyle();
        scrollStyle.vScrollKnob = TextureLoader.getDrawable(15, 15, Color.DARK_GRAY);
        scrollStyle.vScroll = TextureLoader.getDrawable(15, 15, Color.LIGHT_GRAY);

        scrollPaneGameInfoList = new ScrollPane(lstGameInfoPanel, scrollStyle);
        scrollPaneGameInfoList.setOverscroll(false, false);
        //scrollPaneLoadGameList.setFadeScrollBars(false);
        scrollPaneGameInfoList.setScrollingDisabled(true, false);
        scrollPaneGameInfoList.setTransform(true);
        scrollPaneGameInfoList.setScale(1.0f);
        scrollPaneGameInfoList.setWidth(Gdx.graphics.getWidth() / 2 - 10);
        scrollPaneGameInfoList.setHeight(Gdx.graphics.getHeight() - 100);

        float x = Gdx.graphics.getWidth() / 2;
        float y = Gdx.graphics.getHeight() - scrollPaneLoadGameList.getHeight() - 20;

        scrollPaneGameInfoList.setPosition(
                x, y);

        gameInfoPanelList.add(scrollPaneGameInfoList);
        stage.addActor(scrollPaneGameInfoList);
    }

    private static void createGameDifficultyImg(String filename) {

        boolean isGameAdv = DBHandler.isGameAdvForLobbyImg(filename);

        String imagefileName = isGameAdv ? "advLobby.png" : "famLobby.png";
        gameDifficultyImg = new Image(new Texture(imagefileName));
        gameDifficultyImg.setHeight(232);
        gameDifficultyImg.setWidth(407);
        gameDifficultyImg.setPosition(
                50,
                150);

        gameDiffImgList.add(gameDifficultyImg);
        stage.addActor(gameDifficultyImg);
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

        for (int i = 0; i < gameDiffImgList.size(); i++) {
            gameDiffImgList.get(i).remove();
        }

        gameDiffImgList.clear();
    }

}
