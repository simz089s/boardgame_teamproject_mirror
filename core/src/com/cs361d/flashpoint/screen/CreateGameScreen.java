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
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.cs361d.flashpoint.manager.*;
import com.cs361d.flashpoint.networking.Client;
import com.cs361d.flashpoint.networking.ServerCommands;
import org.json.simple.JSONObject;

public class CreateGameScreen extends FlashPointScreen {

    final private int PADDING_LEFT = 75;

    SpriteBatch batch;

    Texture txtrBG;
    Sprite spriteBG;

    TextField gameNameField;

    ImageButton btnExit;
    TextButton btnStartGame;

    Label label;

    List<String> lstNumPlayers, lstDifficulty, lstGameBoard;
    ScrollPane numPlayersMenu, difficultyMenu, gameBoardMenu;

    Image gameBoardImg;

    static Stage stage;

    private Music audioMusic = Gdx.audio.newMusic(Gdx.files.internal("playlist/tech.mp3"));

    CreateGameScreen(Game pGame) {
        super(pGame);
        audioMusic.setLooping(true);
    }

    @Override
    public void show() {

        stage = new Stage();

        audioMusic.play();

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

        Texture myTexture = new Texture(Gdx.files.internal("icons/backBtn.png"));
        TextureRegion myTextureRegion = new TextureRegion(myTexture);
        TextureRegionDrawable myTexRegionDrawable = new TextureRegionDrawable(myTextureRegion);

        btnExit = new ImageButton(myTexRegionDrawable);
        btnExit.setWidth(35);
        btnExit.setHeight(35);
        btnExit.setPosition(
                20,
                Gdx.graphics.getHeight() - btnExit.getHeight() - 20);

        btnExit.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        audioMusic.stop();
                        game.setScreen(game.lobbyScreen);
                    }
                });

        stage.addActor(btnExit);
    }

    // game name

    private void createGameNameLabel() {
        label = new Label("Game name:", skinUI);
        label.setPosition(
                70 + PADDING_LEFT,
                Gdx.graphics.getHeight() - 70);
        label.setColor(Color.BLACK);
        stage.addActor(label);
    }

    private void createGameNameTextField() {
        gameNameField = new TextField("", skinUI, "default");
        gameNameField.setMessageText("Enter it here");
        gameNameField.setWidth(200);
        gameNameField.setHeight(25);
        gameNameField.setMaxLength(15);
        gameNameField.setPosition(
                80 + label.getWidth() + PADDING_LEFT,
                Gdx.graphics.getHeight() - 70);

        stage.addActor(gameNameField);
    }

    // number of players

    private void createNumPlayersLabel() {
        label = new Label("Number of players:", skinUI);
        label.setPosition(
                70 + PADDING_LEFT,
                Gdx.graphics.getHeight() - gameNameField.getHeight() - 100);
        label.setColor(Color.BLACK);
        stage.addActor(label);
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
                        - label.getHeight() - numPlayersMenu.getHeight() - 80);

        stage.addActor(numPlayersMenu);
    }

    // difficulty

    private void createDifficultyLabel() {
        label = new Label("Game difficulty:", skinUI);
        label.setPosition(
                Gdx.graphics.getWidth() / 2 - 250 + PADDING_LEFT,
                Gdx.graphics.getHeight() - gameNameField.getHeight() - 100);
        label.setColor(Color.BLACK);
        stage.addActor(label);
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
                        - label.getHeight() - numPlayersMenu.getHeight() - 80);

        stage.addActor(difficultyMenu);
    }

    // game board (choose)

    private void createGameBoardLabel() {
        label = new Label("Game board:", skinUI);
        label.setPosition(
                70 + PADDING_LEFT,
                Gdx.graphics.getHeight() - gameNameField.getHeight() - numPlayersMenu.getHeight() - 140);
        label.setColor(Color.BLACK);
        stage.addActor(label);
    }

    private void createGameBoardList() {
        lstGameBoard = new List<String>(skinUI);
        String[] gameBoards = {"MAP 1", "MAP 2", "RANDOM"};
        lstGameBoard.setItems(gameBoards);
        gameBoardMenu = new ScrollPane(lstGameBoard);

        gameBoardMenu.setPosition(
                70 + PADDING_LEFT,
                Gdx.graphics.getHeight() - gameNameField.getHeight() - numPlayersMenu.getHeight()
                    - label.getHeight() - gameBoardMenu.getHeight() - 120);

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

        stage.addActor(gameBoardMenu);
    }

    private void createGameBoardImg() {

        gameBoardImg = new Image(new Texture("board1.png"));
        gameBoardImg.setHeight(360);
        gameBoardImg.setWidth(480);
        gameBoardImg.setPosition(
                Gdx.graphics.getWidth() / 2 + PADDING_LEFT,
                200);

        stage.addActor(gameBoardImg);
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
                            String name = gameNameField.getText();
                            MapKind mk = MapKind.MAP1;

                            if (mapSelected.equals("MAP 2")) {
                                mk = MapKind.MAP2;
                            }
                            // TODO
                            //                            else if (mapSelected.equals("RANDOM")){
                            //                                mk = MapKind.RANDOM;
                            //                            }

                            JSONObject obj = new JSONObject();
                            obj.put("name",name);
                            obj.put("numPlayers",numPlayers);
                            obj.put("mapKind",mk.toString());
                            obj.put("Difficulty",diffSelected);
                            audioMusic.stop();
                            Client.getInstance().sendCommand(ServerCommands.CREATE_GAME,obj.toJSONString());

                        } else {
                            BoardDialog boardDialog = new BoardDialog(stage);
                            boardDialog.drawDialog("Warning", "Invalid or empty game name!");
                        }
                    }
                });

        stage.addActor(btnStartGame);
    }

}
