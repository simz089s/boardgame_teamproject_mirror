package com.cs361d.flashpoint.GameScreens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.cs361d.flashpoint.Entities.BoardElements.Tile;
import com.cs361d.flashpoint.controller.DBHandler;
import com.cs361d.flashpoint.controller.GameController;

import java.util.ArrayList;

public class BoardScreen extends FlashPointScreen {

    SpriteBatch batch;
    Texture txtrBG;
    Sprite spriteBG;

    Dialog dialog;

    TextButton btnExit;
    TextButton btnChat;


    // tiles properties
    final int NUMBER_OF_ROWS = 8;
    final int NUMBER_OF_COLS = 10;
    final int NUMBER_OF_TILES = NUMBER_OF_ROWS * NUMBER_OF_COLS;
    final int TILE_SIZE = 75;

    Image[][] tilesImg = new Image[NUMBER_OF_ROWS][NUMBER_OF_COLS];

    Tile[][] tiles = DBHandler.getTiles();

    Stage stage;



    // reference to game units images
    ArrayList<Image> gameUnits = new ArrayList <Image>();
    //Image[] gameUnits = new Image[1000]; // 1000 to be changed to actual max possible number of game units on board
    //int gameUnits_indexCount = 0;



    BoardScreen(Game pGame) {
        super(pGame);
    }

    @Override
    public void show() {

        debugLbl.setPosition(10, 10);
        debugLbl.setColor(Color.PURPLE);

        stage = new Stage();

        batch = new SpriteBatch();

        txtrBG = new Texture("empty.png");
        spriteBG = new Sprite(txtrBG);
        spriteBG.setScale(0.6f);
        spriteBG.setPosition(
                -(Gdx.graphics.getWidth() / 2f) - 125,
                -(Gdx.graphics.getHeight() / 2f) + 30);

        float curYPos = Gdx.graphics.getHeight();

        int leftPadding = 20;
        int topPadding = 20;

        // draw the tiles
        for (int i = 0; i < NUMBER_OF_ROWS; i++){
            for (int j = 0; j < NUMBER_OF_COLS; j++) {

                if (j == 0) {
                    curYPos = Gdx.graphics.getHeight() - (i + 1) * TILE_SIZE;
                }

                //String tileFileName1 = "tiles/tile.png"; // basic tile image
                String tileFileName2 = "boards/board1_tiles/row-" + (i + 1) + "-col-" + (j + 1) + ".jpg"; // tiles with furniture image

                tilesImg[i][j] = new Image(new Texture(tileFileName2));
                tilesImg[i][j].setHeight(TILE_SIZE);
                tilesImg[i][j].setWidth(TILE_SIZE);
                tilesImg[i][j].setPosition(
                        j * TILE_SIZE + leftPadding,
                        curYPos - topPadding);

                stage.addActor(tilesImg[i][j]);

                drawGameUnitOnTile(tilesImg[i][j], i, j);
            }
        }


        createExitButton();
        createChatButton();

        // button listeners
        btnExit.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        game.setScreen(game.lobbyScreen);
                    }
                });

        btnChat.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        //game.setScreen(game.chatScreen);
                    }
                });

        stage.addActor(btnExit);
        stage.addActor(btnChat);
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

    // create the scroll pane (list of options) to be put in the dialog when clicking a tile
    public Table createDialogContentTable(int i, int j){

        final String[] optArr = {"Move up", "Move down", "Move left", "Move right", "Extinguish", "Chop", "Save", "Cancel"}; // temporary
        //final String[] optArr = GameController.getAllAvailableActions();

        final int tmp_i = i;
        final int tmp_j = j;

        Table table = new Table(skinUI);
        table.add(new Label("Available actions", skinUI));
        table.row();

        final List<String> lstOptions = new List<String>(skinUI);
        lstOptions.setItems(optArr);
        ScrollPane optionsMenu = new ScrollPane(lstOptions);


        // when clicking on an item of the list
        lstOptions.addListener(new InputListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {

                if (optArr[lstOptions.getSelectedIndex()].equals("Move up")) {
                    // perform action
                    clearAllGameUnits();
                    // give it current board state (tiles) and current position (i, j); returns updated board state (tiles)
                    tiles = GameController.moveUp(tiles, tmp_i, tmp_j);
                    redrawGameUnitsOnTile();

                } else if (optArr[lstOptions.getSelectedIndex()].equals("Move down")) {
                    clearAllGameUnits();
                    tiles = GameController.moveDown(tiles, tmp_i, tmp_j);
                    redrawGameUnitsOnTile();

                } else if (optArr[lstOptions.getSelectedIndex()].equals("Move left")) {
                    clearAllGameUnits();
                    tiles = GameController.moveLeft(tiles, tmp_i, tmp_j);
                    redrawGameUnitsOnTile();

                } else if (optArr[lstOptions.getSelectedIndex()].equals("Move right")) {
                    clearAllGameUnits();
                    tiles = GameController.moveRight(tiles, tmp_i, tmp_j);
                    redrawGameUnitsOnTile();

                } else if (optArr[lstOptions.getSelectedIndex()].equals("Extinguish")) {
                    clearAllGameUnits();
                    tiles = GameController.extinguishFireToTile(tiles, tmp_i, tmp_j);
                    redrawGameUnitsOnTile();
                } else if (optArr[lstOptions.getSelectedIndex()].equals("Chop")) {

                } else if (optArr[lstOptions.getSelectedIndex()].equals("Save")) {
                    DBHandler.saveGame(tiles);
                } else if (optArr[lstOptions.getSelectedIndex()].equals("Cancel")) {

                } else {
                    debugLbl.setText("failed action");
                }

                dialog.remove();

                return true;

            }
        });

        table.add(optionsMenu);
        table.row();

        return table;

    }

    public void redrawGameUnitsOnTile(){
        for (int i = 0; i < tiles.length; i ++){
            for (int j= 0; j < tiles[i].length; j ++){
                drawGameUnitOnTile(tilesImg[i][j], i, j);
            }
        }
    }

    private void drawGameUnitOnTile(Image myTile, int i, int j){

        final int tmp_index_i = i;
        final int tmp_index_j = j;


        // damage counter
        if (tiles[i][j].getTop_wall() >= 2){ // one damage
            Image gameUnit = new Image(new Texture("game_units/Damage_Counter.png"));
            gameUnit.setHeight(20);
            gameUnit.setWidth(20);
            gameUnit.setPosition(
                    myTile.getX() + myTile.getWidth() / 2 - gameUnit.getHeight() / 2 - 15,
                    myTile.getY() + myTile.getHeight() - gameUnit.getHeight() / 2);

            gameUnits.add(gameUnit);
            stage.addActor(gameUnit);

            if (tiles[i][j].getTop_wall() == 3){ // two damage
                Image gameUnit2 = new Image(new Texture("game_units/Damage_Counter.png"));
                gameUnit2.setHeight(20);
                gameUnit2.setWidth(20);
                gameUnit2.setPosition(
                        myTile.getX() + myTile.getWidth() / 2 - gameUnit2.getHeight() / 2 + 15,
                        myTile.getY() + myTile.getHeight() - gameUnit2.getHeight() / 2);

                gameUnits.add(gameUnit2);
                stage.addActor(gameUnit2);
            }
        }


        if (tiles[i][j].getBottom_wall() >= 2){ // one damage
            Image gameUnit = new Image(new Texture("game_units/Damage_Counter.png"));
            gameUnit.setHeight(20);
            gameUnit.setWidth(20);
            gameUnit.setPosition(
                    myTile.getX() + myTile.getWidth() / 2 - gameUnit.getHeight() / 2 - 15,
                    myTile.getY() - gameUnit.getHeight() / 2);

            gameUnits.add(gameUnit);
            stage.addActor(gameUnit);

            if (tiles[i][j].getBottom_wall() == 3){ // two damage
                Image gameUnit2 = new Image(new Texture("game_units/Damage_Counter.png"));
                gameUnit2.setHeight(20);
                gameUnit2.setWidth(20);
                gameUnit2.setPosition(
                        myTile.getX() + myTile.getWidth() / 2 - gameUnit2.getHeight() / 2 + 15,
                        myTile.getY() - gameUnit2.getHeight() / 2);

                gameUnits.add(gameUnit2);
                stage.addActor(gameUnit2);
            }
        }


        if (tiles[i][j].getLeft_wall() >= 2){ // one damage
            Image gameUnit = new Image(new Texture("game_units/Damage_Counter.png"));
            gameUnit.setHeight(20);
            gameUnit.setWidth(20);
            gameUnit.setPosition(
                    myTile.getX() - gameUnit.getWidth() / 2,
                    myTile.getY() + myTile.getHeight() / 2 - gameUnit.getHeight() / 2 - 15);

            gameUnits.add(gameUnit);
            stage.addActor(gameUnit);

            if (tiles[i][j].getLeft_wall() == 3){ // two damage
                Image gameUnit2 = new Image(new Texture("game_units/Damage_Counter.png"));
                gameUnit2.setHeight(20);
                gameUnit2.setWidth(20);
                gameUnit2.setPosition(
                        myTile.getX() - gameUnit2.getWidth() / 2,
                        myTile.getY() + myTile.getHeight() / 2 - gameUnit2.getHeight() / 2 + 15);

                gameUnits.add(gameUnit2);
                stage.addActor(gameUnit2);
            }
        }


        if (tiles[i][j].getRight_wall() >= 2){ // one damage
            Image gameUnit = new Image(new Texture("game_units/Damage_Counter.png"));
            gameUnit.setHeight(20);
            gameUnit.setWidth(20);
            gameUnit.setPosition(
                    myTile.getX() + myTile.getWidth() - gameUnit.getWidth() / 2 + 15,
                    myTile.getY() + myTile.getHeight() / 2 - gameUnit.getHeight() / 2);

            gameUnits.add(gameUnit);
            stage.addActor(gameUnit);

            if (tiles[i][j].getRight_wall() == 3){ // two damage
                Image gameUnit2 = new Image(new Texture("game_units/Damage_Counter.png"));
                gameUnit2.setHeight(20);
                gameUnit2.setWidth(20);
                gameUnit2.setPosition(
                        myTile.getX() + myTile.getWidth() - gameUnit2.getWidth() / 2 - 15,
                        myTile.getY() + myTile.getHeight() / 2 - gameUnit2.getHeight() / 2);

                gameUnits.add(gameUnit2);
                stage.addActor(gameUnit2);
            }
        }


        if(!tiles[i][j].getHas_firefighter().equals("none")){ // placed at top left corner of tile
            Image gameUnit = new Image(new Texture("game_units/Firefighter.png"));
            gameUnit.setHeight(30);
            gameUnit.setWidth(30);
            gameUnit.setPosition(
                    myTile.getX() ,
                    myTile.getY() + myTile.getHeight() / 2);

            gameUnit.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            dialog = new Dialog("Choice", skinUI, "dialog") {
                                public void result(Object obj) {
                                }
                            };

                            dialog.add(createDialogContentTable(tmp_index_i, tmp_index_j));

                            dialog.show(stage);
                        }
                    });

            gameUnits.add(gameUnit);
            stage.addActor(gameUnit);
        }

        if (tiles[i][j].isHas_victim()){ // placed at top right corner of tile
            Image gameUnit = new Image(new Texture("game_units/Victim_1.png"));
            gameUnit.setHeight(30);
            gameUnit.setWidth(30);
            gameUnit.setPosition(
                    myTile.getX() + myTile.getHeight() / 2,
                    myTile.getY() + myTile.getHeight() / 2);

            gameUnits.add(gameUnit);
            stage.addActor(gameUnit);
        }

        if (tiles[i][j].isHas_false_alarm()){ // placed at top right corner of tile
            Image gameUnit = new Image(new Texture("game_units/POI_False_Alarm.png"));
            gameUnit.setHeight(30);
            gameUnit.setWidth(30);
            gameUnit.setPosition(
                    myTile.getX() + myTile.getHeight() / 2,
                    myTile.getY() + myTile.getHeight() / 2);

            gameUnits.add(gameUnit);
            stage.addActor(gameUnit);
        }

        if (tiles[i][j].isHas_smoke()){ // placed at bottom left corner of tile
            Image gameUnit = new Image(new Texture("game_units/Smoke.png"));
            gameUnit.setHeight(30);
            gameUnit.setWidth(30);
            gameUnit.setPosition(
                    myTile.getX(),
                    myTile.getY());

            gameUnits.add(gameUnit);
            stage.addActor(gameUnit);
        }

        if (tiles[i][j].isHas_fire()){ // placed at bottom left corner of tile
            Image gameUnit = new Image(new Texture("game_units/Fire.png"));
            gameUnit.setHeight(30);
            gameUnit.setWidth(30);
            gameUnit.setPosition(
                    myTile.getX(),
                    myTile.getY());

            gameUnits.add(gameUnit);
            stage.addActor(gameUnit);
        }

        if (tiles[i][j].isHas_explosion()){ // placed at bottom left corner of tile
            Image gameUnit = new Image(new Texture("game_units/Explosion.png"));
            gameUnit.setHeight(30);
            gameUnit.setWidth(30);
            gameUnit.setPosition(
                    myTile.getX(),
                    myTile.getY());

            gameUnits.add(gameUnit);
            stage.addActor(gameUnit);
        }
    }

    private void createExitButton() {
        btnExit = new TextButton("Exit", skinUI, "default");
        btnExit.setWidth(100);
        btnExit.setHeight(25);
        btnExit.setPosition(
                (Gdx.graphics.getWidth() - btnExit.getWidth() - 8),
                (Gdx.graphics.getHeight() - btnExit.getHeight() - 8));
    }

    private void createChatButton() {
        btnChat = new TextButton("Chat", skinUI, "default");
        btnChat.setWidth(100);
        btnChat.setHeight(25);
        btnChat.setPosition(
                (Gdx.graphics.getWidth() - btnExit.getWidth() - 8),
                (Gdx.graphics.getHeight() - btnExit.getHeight() - 15 - btnChat.getHeight()));
    }

    private void clearAllGameUnits(){
        //System.out.println(gameUnits);
        for (int i = 0; i < gameUnits.size(); i++){
            gameUnits.get(i).remove();
        }
        gameUnits.clear();
    }
}
