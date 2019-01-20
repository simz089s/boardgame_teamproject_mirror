package com.cs361d.flashpoint.view;

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
import com.cs361d.flashpoint.model.Board;
import com.cs361d.flashpoint.model.BoardElements.*;
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

  Tile[][] tiles = Board.getInstance().getTiles();

  Stage stage;

  // reference to game units images
  ArrayList<Image> gameUnits = new ArrayList<Image>();

  BoardScreen(Game pGame) {
    super(pGame);
  }

  @Override
  public void show() {
    Board myBoard = Board.getInstance();
//    myBoard.addDoor(0,0, Direction.TOP, 1, false);
    myBoard.addDoor(0,0, Direction.BOTTOM, 1, true);
    myBoard.addDoor(0,0, Direction.LEFT, 1, true);
    myBoard.addFireStatus(1,1, FireStatus.FIRE);
    myBoard.addFireStatus(2,1, FireStatus.SMOKE);
    myBoard.addDoor(0,0, Direction.RIGHT, 1, true);
    debugLbl.setPosition(10, 10);
    debugLbl.setColor(Color.PURPLE);

    stage = new Stage();

    batch = new SpriteBatch();

    txtrBG = new Texture("empty.png");
    spriteBG = new Sprite(txtrBG);
    spriteBG.setScale(0.6f);
    spriteBG.setPosition(
        -(Gdx.graphics.getWidth() / 2f) - 125, -(Gdx.graphics.getHeight() / 2f) + 30);

    float curYPos = Gdx.graphics.getHeight();

    int leftPadding = 20;
    int topPadding = 20;
    // draw the tiles
    for (int i = 0; i < Board.HEIGHT; i++) {
      for (int j = 0; j < Board.WIDTH; j++) {
        if (j == 0) {
          curYPos = Gdx.graphics.getHeight() - (i + 1) * TILE_SIZE;
        }

        String tileFileName1 = "boards/tile.png"; // basic tile image
        //                String tileFileName2 = "boards/board1_tiles/row-" + (i + 1) + "-col-" + (j
        // + 1) + ".jpg"; // tiles with furniture image

        tilesImg[i][j] = new Image(new Texture(tileFileName1));
        tilesImg[i][j].setHeight(TILE_SIZE);
        tilesImg[i][j].setWidth(TILE_SIZE);
        tilesImg[i][j].setPosition(j * TILE_SIZE + leftPadding, curYPos - topPadding);

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
            // game.setScreen(game.chatScreen);
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
    super.dispose();
    batch.dispose();
    stage.dispose();
  }

  // create the scroll pane (list of options) to be put in the dialog when clicking a tile
  public Table createDialogContentTable(int i, int j) {

    final String[] optArr = {
      "Move up", "Move down", "Move left", "Move right", "Extinguish", "Chop", "Save", "Cancel"
    }; // temporary
    // final String[] optArr = GameController.getAllAvailableActions();

    final int tmp_i = i;
    final int tmp_j = j;

    Table table = new Table(skinUI);
    table.add(new Label("Available actions", skinUI));
    table.row();

    final List<String> lstOptions = new List<String>(skinUI);
    lstOptions.setItems(optArr);
    ScrollPane optionsMenu = new ScrollPane(lstOptions);

    // when clicking on an item of the list
    lstOptions.addListener(
        new InputListener() {
          @Override
          public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

            if (optArr[lstOptions.getSelectedIndex()].equals("Move up")) {
              // perform action
              clearAllGameUnits();
              // give it current board state (tiles) and current position (i, j); returns updated
              // board state (tiles)
              GameController.moveUp(tiles, tmp_i, tmp_j);
              redrawGameUnitsOnTile();

            } else if (optArr[lstOptions.getSelectedIndex()].equals("Move down")) {
              clearAllGameUnits();
              GameController.moveDown(tiles, tmp_i, tmp_j);
              redrawGameUnitsOnTile();

            } else if (optArr[lstOptions.getSelectedIndex()].equals("Move left")) {
              clearAllGameUnits();
              GameController.moveLeft(tiles, tmp_i, tmp_j);
              redrawGameUnitsOnTile();

            } else if (optArr[lstOptions.getSelectedIndex()].equals("Move right")) {
              clearAllGameUnits();
              GameController.moveRight(tiles, tmp_i, tmp_j);
              redrawGameUnitsOnTile();

            } else if (optArr[lstOptions.getSelectedIndex()].equals("Extinguish")) {
              clearAllGameUnits();
              GameController.extinguishFireToTile(tiles, tmp_i, tmp_j);
              redrawGameUnitsOnTile();
            } else if (optArr[lstOptions.getSelectedIndex()].equals("Chop")) {

            } else if (optArr[lstOptions.getSelectedIndex()].equals("Save")) {
              //              DBHandler.saveTilesToDB(tiles);
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

  public void redrawGameUnitsOnTile() {
    for (int i = 0; i < tiles.length; i++) {
      for (int j = 0; j < tiles[i].length; j++) {
        drawGameUnitOnTile(tilesImg[i][j], i, j);
      }
    }
  }

  private void drawObstacles(Image myTile, Obstacle obs, Direction d) {
    Image gameUnit = null;
    if (obs.isNull()) {
      return;
    } else if (obs.isDoor()) {
      if (obs.isOpen()) {
        gameUnit = new Image(new Texture("game_units/walls/Open_Door.png"));
      } else {
        gameUnit = new Image(new Texture("game_units/walls/Closed_Door.png"));
      }
      gameUnit.setHeight(20);
      gameUnit.setWidth(20);

      switch (d) {
        case TOP:
          gameUnit.setPosition(
              myTile.getX() + myTile.getWidth() / 2 - gameUnit.getHeight() / 2,
              myTile.getY() + myTile.getHeight() - gameUnit.getHeight() / 2);

          break;

        case BOTTOM:
          gameUnit.setPosition(
              myTile.getX() + myTile.getWidth() / 2 - gameUnit.getHeight() / 2,
              myTile.getY() - gameUnit.getHeight() / 2);
          break;

        case LEFT:
          gameUnit.setPosition(
              myTile.getX() - gameUnit.getWidth() / 2,
              myTile.getY() + myTile.getHeight() / 2 - gameUnit.getHeight() / 2);

          break;

        case RIGHT:
          gameUnit.setPosition(
              myTile.getX() + myTile.getWidth() - gameUnit.getWidth() / 2,
              myTile.getY() + myTile.getHeight() / 2 - gameUnit.getHeight() / 2);
          break;
        default:
          throw new IllegalArgumentException("That argument does not exist");
      }

    } else {
      switch (d) {
        case TOP:
          if (obs.getHealth() == 2) {
            gameUnit = new Image(new Texture("game_units/walls/h2.png"));
          } else if (obs.getHealth() == 1) {
            gameUnit = new Image(new Texture("game_units/walls/h1.png"));
          } else {
            gameUnit = new Image(new Texture("game_units/walls/h0.png"));
          }
          gameUnit.setHeight(5);
          gameUnit.setWidth(75);
          gameUnit.setPosition(myTile.getX(), myTile.getY() + myTile.getHeight());
          break;

        case BOTTOM:
          if (obs.getHealth() == 2) {
            gameUnit = new Image(new Texture("game_units/walls/h2.png"));
          } else if (obs.getHealth() == 1) {
            gameUnit = new Image(new Texture("game_units/walls/h1.png"));
          } else {
            gameUnit = new Image(new Texture("game_units/walls/h0.png"));
          }
          gameUnit.setHeight(5);
          gameUnit.setWidth(75);
          gameUnit.setPosition(myTile.getX(), myTile.getY());
          break;

        case LEFT:
          if (obs.getHealth() == 2) {
            gameUnit = new Image(new Texture("game_units/walls/v2.png"));
          } else if (obs.getHealth() == 1) {
            gameUnit = new Image(new Texture("game_units/walls/v1.png"));
          } else {
            gameUnit = new Image(new Texture("game_units/walls/v0.png"));
          }
          gameUnit.setHeight(75);
          gameUnit.setWidth(5);
          gameUnit.setPosition(
              myTile.getX() - gameUnit.getWidth() / 2,
              myTile.getY() + myTile.getHeight() / 2 - gameUnit.getHeight() / 2);

          break;

        case RIGHT:
          if (obs.getHealth() == 2) {
            gameUnit = new Image(new Texture("game_units/walls/v2.png"));
          } else if (obs.getHealth() == 1) {
            gameUnit = new Image(new Texture("game_units/walls/v1.png"));
          } else {
            gameUnit = new Image(new Texture("game_units/walls/v0.png"));
          }
          gameUnit.setHeight(75);
          gameUnit.setWidth(5);
          gameUnit.setPosition(
              myTile.getX() + myTile.getWidth(),
              myTile.getY() + myTile.getHeight() / 2 - gameUnit.getHeight() / 2);
          break;
        default:
          throw new IllegalArgumentException("That argument does not exist");
      }
    }
    gameUnits.add(gameUnit);
    stage.addActor(gameUnit);
  }

  private void drawGameUnitOnTile(Image myTile, int i, int j) {

    final int tmp_index_i = i;
    final int tmp_index_j = j;
    Obstacle top = tiles[i][j].getObstacle(Direction.TOP);
    Obstacle left = tiles[i][j].getObstacle(Direction.LEFT);

    drawObstacles(myTile, top, Direction.TOP);
    drawObstacles(myTile, left, Direction.LEFT);
    if (j == Board.WIDTH - 1) {
      Obstacle right = tiles[i][j].getObstacle(Direction.RIGHT);
      drawObstacles(myTile, right, Direction.RIGHT);
    }

    if (i == Board.HEIGHT - 1) {
      Obstacle bottom = tiles[i][j].getObstacle(Direction.BOTTOM);
      drawObstacles(myTile, bottom, Direction.BOTTOM);
    }

    // damage counter
    if (!tiles[i][j].getFirefighters().isEmpty()) { // placed at top left corner of tile

      Image gameUnit = new Image(new Texture("game_units/firefighters/blue.png"));
      gameUnit.addListener(
          new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
              dialog =
                  new Dialog("Choice", skinUI, "dialog") {
                    public void result(Object obj) {}
                  };

              dialog.add(createDialogContentTable(tmp_index_i, tmp_index_j));

              dialog.show(stage);
            }
          });

      if (tiles[i][j].getFirefighters().get(0).getColor().equals("red")) {
        gameUnit = new Image(new Texture("game_units/firefighters/red.png"));
      } else if (tiles[i][j].getFirefighters().get(0).getColor().equals("blue")) {
        gameUnit = new Image(new Texture("game_units/firefighters/blue.png"));
      } else if (tiles[i][j].getFirefighters().get(0).getColor().equals("white")) {
        gameUnit = new Image(new Texture("game_units/firefighters/white.png"));
      } else if (tiles[i][j].getFirefighters().get(0).getColor().equals("orange")) {
        gameUnit = new Image(new Texture("game_units/firefighters/orange.png"));
      } else if (tiles[i][j].getFirefighters().get(0).getColor().equals("yellow")) {
        gameUnit = new Image(new Texture("game_units/firefighters/yellow.png"));
      } else if (tiles[i][j].getFirefighters().get(0).getColor().equals("green")) {
        gameUnit = new Image(new Texture("game_units/firefighters/green.png"));
      }

      gameUnit.setHeight(30);
      gameUnit.setWidth(30);
      gameUnit.setPosition(myTile.getX(), myTile.getY() + myTile.getHeight() / 2);

      gameUnits.add(gameUnit);
      stage.addActor(gameUnit);
    }
    Image gameUnit;
    if (tiles[i][j].containsPointOfInterest()) {
      gameUnit = new Image(new Texture("game_units/POI_Rear.png"));
      if (tiles[i][j].getVictim().isRevealed()) {
        gameUnit = new Image(new Texture("game_units/Victim_1.png"));
      }
      gameUnit.setHeight(30);
      gameUnit.setWidth(30);
      gameUnit.setPosition(
          myTile.getX() + myTile.getHeight() / 2, myTile.getY() + myTile.getHeight() / 2);
      gameUnits.add(gameUnit);
      stage.addActor(gameUnit);
    }

    if (tiles[i][j].hasSmoke()) { // placed at bottom left corner of tile
      gameUnit = new Image(new Texture("game_units/Smoke.png"));
      gameUnit.setHeight(30);
      gameUnit.setWidth(30);
      gameUnit.setPosition(myTile.getX(), myTile.getY());

      gameUnits.add(gameUnit);
      stage.addActor(gameUnit);
    } else if (tiles[i][j].hasFire()) { // placed at bottom left corner of tile
      gameUnit = new Image(new Texture("game_units/Fire.png"));
      gameUnit.setHeight(30);
      gameUnit.setWidth(30);
      gameUnit.setPosition(myTile.getX(), myTile.getY());

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

  private void clearAllGameUnits() {
    // System.out.println(gameUnits);
    for (int i = 0; i < gameUnits.size(); i++) {
      gameUnits.get(i).remove();
    }
    gameUnits.clear();
  }

  private void addFireFighterListener(FireFighter f, Image i) {}
}
