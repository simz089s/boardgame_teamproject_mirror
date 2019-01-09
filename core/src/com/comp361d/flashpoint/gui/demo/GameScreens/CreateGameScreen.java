package com.comp361d.flashpoint.gui.demo.GameScreens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class CreateGameScreen extends FlashPointScreen {

    SpriteBatch batch;

    Texture txtrBG;
    Sprite spriteBG;

    TextField gameNameField;

    TextButton btnLogout;
    TextButton btnStartGame;

    Label gameNameLabel;
    Label numPlayersLabel;
    Label difficultyLabel;
    Label gameBoardLabel;

    List<String> lstNumPlayers;
    ScrollPane numPlayersMenu;

    List<String> lstDifficulty;
    ScrollPane difficultyMenu;

    List<String> lstGameBoard;
    ScrollPane gameBoardMenu;

    Image gameBoardImg;

    Stage stage;

    CreateGameScreen(Game pGame) {
        super(pGame);
    }

    @Override
    public void show() {

        debugLbl.setPosition(10, 10);
        debugLbl.setColor(Color.PURPLE);
        debugLbl.setText("debug");


        batch = new SpriteBatch();

        txtrBG = new Texture("create.png");
        spriteBG = new Sprite(txtrBG);
        spriteBG.setScale(0.6f);
        spriteBG.setPosition(
                -(Gdx.graphics.getWidth() / 2f) - 125, -(Gdx.graphics.getHeight() / 2f) + 30);

        // exit button
        createLogoutButton();

        // create label + text field for: game name
        createGameNameLabel();
        createGameNameTextField();

        // create label + menu list for: num of players
        createNumPlayersLabel();
        createNumPlayersList();

        // create label + menu list for: difficulty
        createDifficultyLabel();
        createDifficultyList();

        // create label + menu list for: game board choice
        createGameBoardLabel();
        createGameBoardList();
        createGameBoardImg();

        // create game button
        createStartGameButton();

        // button listeners
        btnLogout.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        game.setScreen(game.loginScreen);
                    }
                });

        btnStartGame.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (!gameNameField.getText().isEmpty()) {
                            debugLbl.setText(gameNameField.getText() + ", " + lstNumPlayers.getSelected() + ", "
                                    + lstDifficulty.getSelected() + ", " + lstGameBoard.getSelected());
                        } else {
                            debugLbl.setText("Invalid: enter a game name.");
                        }
                    }
                });

        lstGameBoard.addListener(new InputListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {

                String boardNum = "" + (lstGameBoard.getSelectedIndex() + 1);

                gameBoardImg = new Image(new Texture("board" + boardNum + ".png"));
                gameBoardImg.setHeight(180);
                gameBoardImg.setWidth(240);
                gameBoardImg.setPosition(
                        Gdx.graphics.getWidth() / 2,
                        50);
                stage.addActor(gameBoardImg);
                //debugLbl.setText(lstGameBoard.getSelected());
                return true;
            }
        });

        stage = new Stage();
        stage.addActor(btnLogout);

        stage.addActor(gameNameLabel);
        stage.addActor(gameNameField);

        stage.addActor(numPlayersLabel);
        stage.addActor(numPlayersMenu);

        stage.addActor(difficultyLabel);
        stage.addActor(difficultyMenu);

        stage.addActor(gameBoardLabel);
        stage.addActor(gameBoardMenu);
        stage.addActor(gameBoardImg);

        stage.addActor(btnStartGame);

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

    private void createLogoutButton() {
        btnLogout = new TextButton("LOGOUT", skinUI, "default");
        btnLogout.setWidth(100);
        btnLogout.setHeight(25);
        btnLogout.setPosition(
                (Gdx.graphics.getWidth() - btnLogout.getWidth() - 8),
                (Gdx.graphics.getHeight() - btnLogout.getHeight() - 8));
    }

    private void createGameNameLabel() {
        gameNameLabel = new Label("Game name:", skinUI);
        gameNameLabel.setPosition(
                70,
                Gdx.graphics.getHeight() - 70);
        gameNameLabel.setColor(Color.BLACK);
    }

    private void createGameNameTextField() {
        gameNameField = new TextField("", skinUI, "default");
        gameNameField.setMessageText("Enter it here");
        gameNameField.setWidth(200);
        gameNameField.setHeight(25);
        gameNameField.setMaxLength(20);
        gameNameField.setPosition(
                80 + gameNameLabel.getWidth(),
                Gdx.graphics.getHeight() - 70);
    }

    // number of players

    private void createNumPlayersLabel() {
        numPlayersLabel = new Label("Number of players:", skinUI);
        numPlayersLabel.setPosition(
                70,
                Gdx.graphics.getHeight() - gameNameField.getHeight() - 100);
        numPlayersLabel.setColor(Color.BLACK);
    }

    private void createNumPlayersList() {
        lstNumPlayers = new List<String>(skinUI);
        String[] numPlayers = {"3","4","5","6"};
        lstNumPlayers.setItems(numPlayers);
        numPlayersMenu = new ScrollPane(lstNumPlayers);

        numPlayersMenu.setWidth(100);
        numPlayersMenu.setPosition(
                70,
                Gdx.graphics.getHeight() - gameNameField.getHeight()
                        - numPlayersLabel.getHeight() - numPlayersMenu.getHeight() - 80);
    }

    // difficulty

    private void createDifficultyLabel() {
        difficultyLabel = new Label("Game difficulty:", skinUI);
        difficultyLabel.setPosition(
                Gdx.graphics.getWidth() / 2 + 30,
                Gdx.graphics.getHeight() - gameNameField.getHeight() - 90);
        difficultyLabel.setColor(Color.BLACK);
    }

    private void createDifficultyList() {
        lstDifficulty = new List<String>(skinUI);
        String[] gameDifficulty = {"Family","Recruit","Veteran","Heroic"};
        lstDifficulty.setItems(gameDifficulty);
        difficultyMenu = new ScrollPane(lstDifficulty);

        difficultyMenu.setPosition(
                Gdx.graphics.getWidth() / 2 + 30,
                Gdx.graphics.getHeight() - gameNameField.getHeight()
                        - difficultyLabel.getHeight() - numPlayersMenu.getHeight() - 70);
    }

    // game board (choose)

    private void createGameBoardLabel() {
        gameBoardLabel = new Label("Game board:", skinUI);
        gameBoardLabel.setPosition(
                70,
                Gdx.graphics.getHeight() - gameNameField.getHeight() - numPlayersMenu.getHeight() - 100);
        gameBoardLabel.setColor(Color.BLACK);
    }

    private void createGameBoardList() {
        lstGameBoard = new List<String>(skinUI);
        String[] gameBoards = {"Game board 1", "Game board 2", "Game board 3"};
        lstGameBoard.setItems(gameBoards);
        gameBoardMenu = new ScrollPane(lstGameBoard);

        gameBoardMenu.setPosition(
                70,
                Gdx.graphics.getHeight() - gameNameField.getHeight() - numPlayersMenu.getHeight()
                    - gameBoardLabel.getHeight() - gameBoardMenu.getHeight() - 80);
    }

    private void createGameBoardImg() {

        gameBoardImg = new Image(new Texture("board1.png"));
        gameBoardImg.setHeight(180);
        gameBoardImg.setWidth(240);
        gameBoardImg.setPosition(
                Gdx.graphics.getWidth() / 2,
                50);
    }

    // start game after creating game
    private void createStartGameButton() {
        btnStartGame = new TextButton("Start game", skinUI, "default");
        btnStartGame.setWidth(150);
        btnStartGame.setHeight(25);
        btnStartGame.setPosition(
                (Gdx.graphics.getWidth() - btnStartGame.getWidth()) / 2,
                10);
    }

}
