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

    Image[][] tiles = new Image[NUMBER_OF_ROWS][NUMBER_OF_COLS];

    Stage stage;

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

        // drawing the tiles
        for (int i = 0; i < NUMBER_OF_ROWS; i++){
            for (int j = 0; j < NUMBER_OF_COLS; j++) {

                if (j == 0) {
                    curYPos = Gdx.graphics.getHeight() - (i + 1) * TILE_SIZE;
                }

                //String tileFileName1 = "tiles/tile.png"; // basic tile image
                String tileFileName2 = "tiles/row-" + (i + 1) + "-col-" + (j + 1) + ".jpg"; // tiles with furniture image

                tiles[i][j] = new Image(new Texture(tileFileName2));
                tiles[i][j].setHeight(TILE_SIZE);
                tiles[i][j].setWidth(TILE_SIZE);
                tiles[i][j].setPosition(
                        j * TILE_SIZE + leftPadding,
                        curYPos - topPadding);

                final int tmp_index_i = i;
                final int tmp_index_j = j;

                tiles[i][j].addListener(
                        new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {

                                dialog = new Dialog("Choice", skinUI, "dialog") {
                                    public void result(Object obj) {
                                    }
                                };

                                String[] optionsArr = {"firefighter", "victim", "fire", "smoke", "explosion", "damaged wall top", "damaged wall bottom", "damaged wall left", "damaged wall right"};
                                String text = "Which game unit do you want to add? ";
                                dialog.add(createDialogContentTable(tiles[tmp_index_i][tmp_index_j], text, optionsArr));

                                dialog.show(stage);
                            }
                        });

                stage.addActor(tiles[i][j]);
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
                        game.setScreen(game.chatScreen);
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
    public Table createDialogContentTable(Image tile, String titleTxt, String[] optionsArr){

        final Image myTile = tile;

        Table table = new Table(skinUI);
        table.add(new Label(titleTxt, skinUI));
        table.row();

        final List<String> lstOptions = new List<String>(skinUI);
        lstOptions.setItems(optionsArr);
        ScrollPane optionsMenu = new ScrollPane(lstOptions);


        // when clicking on an item of the list
        lstOptions.addListener(new InputListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {

                Image gameUnit = null;

                switch (lstOptions.getSelectedIndex()) {
                    case 0: gameUnit = new Image(new Texture("game_units/Firefighter.png"));
                        break;
                    case 1:  gameUnit = new Image(new Texture("game_units/Victim_1.png"));
                        break;
                    case 2:  gameUnit = new Image(new Texture("game_units/Fire.png"));
                        break;
                    case 3:  gameUnit = new Image(new Texture("game_units/Smoke.png"));
                        break;
                    case 4:  gameUnit = new Image(new Texture("game_units/Explosion.png"));
                        break;
                    case 5:
                        gameUnit = new Image(new Texture("game_units/Damage_Counter.png"));
                        gameUnit.setHeight(20);
                        gameUnit.setWidth(20);
                        gameUnit.setPosition(
                                myTile.getX() + myTile.getWidth() / 2 - gameUnit.getHeight() / 2,
                                myTile.getY() + myTile.getHeight() - gameUnit.getHeight() / 2);
                        break;
                    case 6:
                        gameUnit = new Image(new Texture("game_units/Damage_Counter.png"));
                        gameUnit.setHeight(20);
                        gameUnit.setWidth(20);
                        gameUnit.setPosition(
                                myTile.getX() + myTile.getWidth() / 2 - gameUnit.getHeight() / 2,
                                myTile.getY() - gameUnit.getHeight() / 2);
                        break;
                    case 7:
                        gameUnit = new Image(new Texture("game_units/Damage_Counter.png"));
                        gameUnit.setHeight(20);
                        gameUnit.setWidth(20);
                        gameUnit.setPosition(
                                myTile.getX() - gameUnit.getWidth() / 2,
                                myTile.getY() + myTile.getHeight() / 2 - gameUnit.getHeight() / 2);
                        break;
                    case 8:
                        gameUnit = new Image(new Texture("game_units/Damage_Counter.png"));
                        gameUnit.setHeight(20);
                        gameUnit.setWidth(20);
                        gameUnit.setPosition(
                                myTile.getX() + myTile.getWidth() - gameUnit.getWidth() / 2,
                                myTile.getY() + myTile.getHeight() / 2 - gameUnit.getHeight() / 2);
                        break;
                    default: debugLbl.setText("failed");
                        break;
                }


                if (lstOptions.getSelectedIndex() < 5){  // tile markers
                    gameUnit.setHeight(50);
                    gameUnit.setWidth(50);
                    gameUnit.setPosition(
                            myTile.getX() + myTile.getWidth() / 5,
                            myTile.getY() + myTile.getHeight() / 5);
                }

                dialog.remove();

                if (gameUnit != null) {
                    stage.addActor(gameUnit);
                }
                return true;

            }
        });

        table.add(optionsMenu);
        table.row();

        return table;

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
}
