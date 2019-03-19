package com.cs361d.flashpoint.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.cs361d.flashpoint.manager.*;
import com.cs361d.flashpoint.networking.Commands;
import com.cs361d.flashpoint.networking.NetworkManager;

public class CreateGameScreen extends FlashPointScreen {

    final private int PADDING_LEFT = 75;

    SpriteBatch batch;

    Texture txtrBG;
    Sprite spriteBG;

    TextField gameNameField;

    TextButton btnExit;
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

    static Stage stage;

    private Music BGM = Gdx.audio.newMusic(Gdx.files.internal("playlist/tech.mp3"));

    CreateGameScreen(Game pGame) {
        super(pGame);
        BGM.setLooping(true);
    }

    @Override
    public void show() {

        BGM.play();

        batch = new SpriteBatch();

        txtrBG = new Texture("create.png");
        spriteBG = new Sprite(txtrBG);
        //spriteBG.setScale(0.6f);
        spriteBG.setPosition(
                0, 0);

        // exit button
        createExitButton();

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
        btnExit.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        BGM.stop();
                        game.setScreen(game.lobbyScreen);
                    }
                });

    btnStartGame.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {

            boolean isInvalidName =
                gameNameField.getText().equals("map1") || gameNameField.getText().equals("map2");

            if (!gameNameField.getText().isEmpty() && !isInvalidName) {

              int numPlayers = Integer.parseInt(lstNumPlayers.getSelected());

              String mapSelected = lstGameBoard.getSelected();
              String diffSelected = lstDifficulty.getSelected();

              MapKind mk = MapKind.MAP1;

              if (mapSelected.equals("MAP 2")) {
                mk = MapKind.MAP2;
              }
              // TODO
              //                            else if (mapSelected.equals("RANDOM")){
              //                                mk = MapKind.RANDOM;
              //                            }

              CreateNewGameManager.createNewGame(
                  gameNameField.getText(), numPlayers, mk, Difficulty.fromString(diffSelected));
              NetworkManager.getInstance()
                  .sendCommand(Commands.SEND_NEWLY_CREATED_BOARD, DBHandler.getBoardAsString());
                game.setScreen(game.boardScreen);
                BGM.stop();

            } else {
              createDialog("Warning", "Invalid or empty game name!");
            }
          }
        });

        lstGameBoard.addListener(new InputListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {

                String boardNum = "" + (lstGameBoard.getSelectedIndex() + 1);

                gameBoardImg = new Image(new Texture("board" + boardNum + ".png"));
                gameBoardImg.setHeight(360);
                gameBoardImg.setWidth(480);
                gameBoardImg.setPosition(
                        Gdx.graphics.getWidth() / 2 + PADDING_LEFT,
                        200);

                stage.addActor(gameBoardImg);
                return true;
            }
        });

        stage = new Stage();
        stage.addActor(btnExit);

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



    private void createExitButton() {
        btnExit = new TextButton("Exit", skinUI, "default");
        btnExit.setWidth(100);
        btnExit.setHeight(25);
        btnExit.setPosition(
                (Gdx.graphics.getWidth() - btnExit.getWidth() - 8),
                (Gdx.graphics.getHeight() - btnExit.getHeight() - 8));
    }



    // game name



    private void createGameNameLabel() {
        gameNameLabel = new Label("Game name:", skinUI);
        gameNameLabel.setPosition(
                70 + PADDING_LEFT,
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
                80 + gameNameLabel.getWidth() + PADDING_LEFT,
                Gdx.graphics.getHeight() - 70);
    }



    // number of players



    private void createNumPlayersLabel() {
        numPlayersLabel = new Label("Number of players:", skinUI);
        numPlayersLabel.setPosition(
                70 + PADDING_LEFT,
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
                70 + PADDING_LEFT,
                Gdx.graphics.getHeight() - gameNameField.getHeight()
                        - numPlayersLabel.getHeight() - numPlayersMenu.getHeight() - 80);
    }



    // difficulty



    private void createDifficultyLabel() {
        difficultyLabel = new Label("Game difficulty:", skinUI);
        difficultyLabel.setPosition(
                Gdx.graphics.getWidth() / 2 - 250 + PADDING_LEFT,
                Gdx.graphics.getHeight() - gameNameField.getHeight() - 100);
        difficultyLabel.setColor(Color.BLACK);
    }

    private void createDifficultyList() {
        lstDifficulty = new List<String>(skinUI);
        String[] gameDifficulty = new String[Difficulty.values().length];
        int i = 0;
        for (Difficulty diff : Difficulty.values()) {
            gameDifficulty[i] = diff.getText();
            i++;
        }
        lstDifficulty.setItems(gameDifficulty);
        difficultyMenu = new ScrollPane(lstDifficulty);

        difficultyMenu.setPosition(
                Gdx.graphics.getWidth() / 2 - 250 + PADDING_LEFT,
                Gdx.graphics.getHeight() - gameNameField.getHeight()
                        - difficultyLabel.getHeight() - numPlayersMenu.getHeight() - 80);
    }



    // game board (choose)



    private void createGameBoardLabel() {
        gameBoardLabel = new Label("Game board:", skinUI);
        gameBoardLabel.setPosition(
                70 + PADDING_LEFT,
                Gdx.graphics.getHeight() - gameNameField.getHeight() - numPlayersMenu.getHeight() - 140);
        gameBoardLabel.setColor(Color.BLACK);
    }

    private void createGameBoardList() {
        lstGameBoard = new List<String>(skinUI);
        String[] gameBoards = {"MAP 1", "MAP 2", "RANDOM"};
        lstGameBoard.setItems(gameBoards);
        gameBoardMenu = new ScrollPane(lstGameBoard);

        gameBoardMenu.setPosition(
                70 + PADDING_LEFT,
                Gdx.graphics.getHeight() - gameNameField.getHeight() - numPlayersMenu.getHeight()
                    - gameBoardLabel.getHeight() - gameBoardMenu.getHeight() - 120);
    }

    private void createGameBoardImg() {

        gameBoardImg = new Image(new Texture("board1.png"));
        gameBoardImg.setHeight(360);
        gameBoardImg.setWidth(480);
        gameBoardImg.setPosition(
                Gdx.graphics.getWidth() / 2 + PADDING_LEFT,
                200);
    }



    // start game after creating game



    private void createStartGameButton() {
        btnStartGame = new TextButton("Start game", skinUI, "default");
        btnStartGame.setWidth(150);
        btnStartGame.setHeight(50);
        btnStartGame.setColor(Color.FIREBRICK);
        btnStartGame.setPosition(
                (Gdx.graphics.getWidth() - btnStartGame.getWidth()) / 2,
                75);
    }



    // dialog (warning message)



    public static void createDialog(String title, String message){
        Dialog dialog = new Dialog(title, skinUI, "dialog") {
            public void result(Object obj) {
                remove();
            }
        };

        dialog.text(message);
        dialog.button("OK", true);
        dialog.show(stage);
    }

}
