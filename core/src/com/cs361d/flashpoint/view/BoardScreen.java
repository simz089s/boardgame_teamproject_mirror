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
import com.cs361d.flashpoint.manager.DBHandler;
import com.cs361d.flashpoint.manager.FireFighterTurnManager;
import com.cs361d.flashpoint.manager.BoardManager;
import com.cs361d.flashpoint.model.BoardElements.*;

import java.util.ArrayList;

public class BoardScreen extends FlashPointScreen {

  String BOARD_TO_DISPLAY_FILE = "boards/tile.png";

  static final int NUMBER_OF_ROWS = BoardManager.HEIGHT;
  static final int NUMBER_OF_COLS = BoardManager.WIDTH;

  static final int WALL_THICKNESS = 5;
  static final int TILE_SIZE = 75;

  static Image[][] tilesImg = new Image[NUMBER_OF_ROWS][NUMBER_OF_COLS];
  static ArrayList<Image> gameUnits = new ArrayList<Image>();


  // managers initialization
  static Tile[][] tiles = BoardManager.getInstance().getTiles();
  static FireFighterTurnManager fireFighterTurnManager = FireFighterTurnManager.getInstance();


  SpriteBatch batch;
  Texture txtrBG;
  Sprite spriteBG;
  static Stage stage;

  BoardChooseInitPosPanel boardChooseInitPosPanel;
  BoardMovesPanel boardMovesPanel;
  BoardChatFragment boardChatFragment;

  TextButton btnExit;
  TextButton btnChat;
  TextButton btnResumeGame;
  static Label gameInfoLabel;
  static Dialog dialog;

  BoardScreen(Game pGame) {
    super(pGame);
  }

  @Override
  public void show() {

    //DBHandler.createBoardDBFamilyVersion(); // generate start board

    final BoardManager myBoardManager = DBHandler.getBoardFromDB();

    stage = new Stage();
    batch = new SpriteBatch();

    txtrBG = new Texture("empty.png");
    spriteBG = new Sprite(txtrBG);
    spriteBG.setScale(0.6f);
    spriteBG.setPosition(
            -(Gdx.graphics.getWidth() / 2f) - 125, -(Gdx.graphics.getHeight() / 2f) + 30);

    boardChatFragment = new BoardChatFragment(stage);

    float curYPos = Gdx.graphics.getHeight();

    int leftPadding = 20;
    int topPadding = 20;

    // draw the tiles
    for (int i = 0; i < NUMBER_OF_ROWS; i++) {
      for (int j = 0; j < NUMBER_OF_COLS; j++) {
        if (j == 0) {
          curYPos = Gdx.graphics.getHeight() - (i + 1) * TILE_SIZE;
        }

        //BOARD_TO_DISPLAY_FILE = "boards/board1_tiles/row-" + (i + 1) + "-col-" + (j + 1) + ".jpg"; // tiles with furniture image

        tilesImg[i][j] = new Image(new Texture(BOARD_TO_DISPLAY_FILE));
        tilesImg[i][j].setHeight(TILE_SIZE);
        tilesImg[i][j].setWidth(TILE_SIZE);
        tilesImg[i][j].setPosition(j * TILE_SIZE + leftPadding + (j + 1) * WALL_THICKNESS, curYPos - topPadding - (i + 1) * WALL_THICKNESS);

        stage.addActor(tilesImg[i][j]);
        drawGameUnitsOnTile(tilesImg[i][j], i, j);
      }
    }

    createExitButton();
    createChatButton();
    createGameInfoLabel();

    // Moves panel
    boardMovesPanel = new BoardMovesPanel(stage);
    boardMovesPanel.createMovesAndDirectionsPanel();

    stage.addActor(btnExit);
    stage.addActor(gameInfoLabel);

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
    super.dispose();
    batch.dispose();
    stage.dispose();
  }

  // draw walls and doors
  private static void drawObstacles(Image myTile, Obstacle obs, Direction d) {
    Image gameUnit;
    if (obs.isNull() ) {
      return;

    } else if (obs.isDoor()) { // door
      if (obs.isDestroyed()) {
        gameUnit = new Image(new Texture("game_units/walls/Destroyed_Door.png"));
      }
      else if (obs.isOpen()) {
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

    } else { // wall
      switch (d) {
        case TOP:
          if (obs.getHealth() == 2) {
            gameUnit = new Image(new Texture("game_units/walls/h2.png"));
          } else if (obs.getHealth() == 1) {
            gameUnit = new Image(new Texture("game_units/walls/h1.png"));
          } else {
            gameUnit = new Image(new Texture("game_units/walls/h0.png"));
          }
          gameUnit.setHeight(WALL_THICKNESS);
          gameUnit.setWidth(TILE_SIZE);
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
          gameUnit.setHeight(WALL_THICKNESS);
          gameUnit.setWidth(TILE_SIZE);
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
          gameUnit.setHeight(TILE_SIZE);
          gameUnit.setWidth(WALL_THICKNESS);
          gameUnit.setPosition(
                  myTile.getX() - WALL_THICKNESS,
                  myTile.getY());

          break;

        case RIGHT:
          if (obs.getHealth() == 2) {
            gameUnit = new Image(new Texture("game_units/walls/v2.png"));
          } else if (obs.getHealth() == 1) {
            gameUnit = new Image(new Texture("game_units/walls/v1.png"));
          } else {
            gameUnit = new Image(new Texture("game_units/walls/v0.png"));
          }
          gameUnit.setHeight(TILE_SIZE);
          gameUnit.setWidth(WALL_THICKNESS);
          gameUnit.setPosition(
                  myTile.getX() + myTile.getWidth(),
                  myTile.getY());
          break;
        default:
          throw new IllegalArgumentException("That argument does not exist");
      }
    }
    gameUnits.add(gameUnit);
    stage.addActor(gameUnit);
  }

  // draw all game units: obstacles, firefighter (top left), victim (top right), fire_status = smoke, fire (bottom left)
  private static void drawGameUnitsOnTile(Image myTile, int i, int j) {

    Obstacle top = tiles[i][j].getObstacle(Direction.TOP);
    Obstacle left = tiles[i][j].getObstacle(Direction.LEFT);

    drawObstacles(myTile, top, Direction.TOP);
    drawObstacles(myTile, left, Direction.LEFT);

    if (j == BoardManager.WIDTH - 1) {
      Obstacle right = tiles[i][j].getObstacle(Direction.RIGHT);
      drawObstacles(myTile, right, Direction.RIGHT);
    }

    if (i == BoardManager.HEIGHT - 1) {
      Obstacle bottom = tiles[i][j].getObstacle(Direction.BOTTOM);
      drawObstacles(myTile, bottom, Direction.BOTTOM);
    }

    // Firefighters
    if (!tiles[i][j].getFirefighters().isEmpty()) {

      Image gameUnit;
      for (FireFighter f : tiles[i][j].getFirefighters()) {
        switch (f.getColor()) {
          case BLUE:
            gameUnit = new Image(new Texture("game_units/firefighters/blue.png"));
            break;
          case RED:
            gameUnit = new Image(new Texture("game_units/firefighters/red.png"));
            break;
          case GREEN:
            gameUnit = new Image(new Texture("game_units/firefighters/green.png"));
            break;
          case WHITE:
            gameUnit = new Image(new Texture("game_units/firefighters/white.png"));
            break;
          case ORANGE:
            gameUnit = new Image(new Texture("game_units/firefighters/orange.png"));
            break;
          case YELLOW:
            gameUnit = new Image(new Texture("game_units/firefighters/yellow.png"));
            break;
          default:
            throw new IllegalArgumentException(
                    "FireFighter color " + f.getColor() + " does not exists");
        }
        gameUnit.setHeight(30);
        gameUnit.setWidth(30);
        gameUnit.setPosition(myTile.getX(), myTile.getY() + myTile.getHeight() / 2);

        gameUnits.add(gameUnit);
        stage.addActor(gameUnit);
      }
    }
    // POI
    Image gameUnit;
    if (tiles[i][j].hasPointOfInterest()) {
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

    // Fire status (smoke, fire)
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

  public static void redrawGameUnitsOnTile() {
    for (int i = 0; i < tiles.length; i++) {
      for (int j = 0; j < tiles[i].length; j++) {
        drawGameUnitsOnTile(tilesImg[i][j], i, j);
      }
    }
  }

  public static void clearAllGameUnits() {
    // System.out.println(gameUnits); // for test
    for (int i = 0; i < gameUnits.size(); i++) {
      gameUnits.get(i).remove();
    }
    gameUnits.clear();
  }

  private void createGameInfoLabel() {
    int numAP = fireFighterTurnManager.getCurrentFireFighter().getActionPointsLeft();
    FireFighterColor color = fireFighterTurnManager.getCurrentFireFighter().getColor();
    gameInfoLabel = new Label("Current turn: " + color + "\nAP left: " + numAP, skinUI);
    gameInfoLabel.setPosition(
            850,
            Gdx.graphics.getHeight() - 100);
    gameInfoLabel.setColor(Color.BLACK);
  }

  public static void updateGameInfoLabel(){
    int APLeft = fireFighterTurnManager.getCurrentFireFighter().getActionPointsLeft();
    FireFighterColor color = fireFighterTurnManager.getCurrentFireFighter().getColor();
    gameInfoLabel.setText("Current turn: " + color + "\nAP left: " + APLeft);
  }

  public static void createDialog(String title, String message){
    dialog =
            new Dialog(title, skinUI, "dialog") {
              public void result(Object obj) {}
            };
    dialog.add(createDialogContent(message));
    dialog.show(stage);
  }

  public static Table createDialogContent(String message) {

    final String[] dialogOptArr = {"OK"};

    Table table = new Table(skinUI);
    table.add(new Label(message, skinUI));
    table.row();

    final List<String> lstOptions = new List<String>(skinUI);
    lstOptions.setItems(dialogOptArr);
    ScrollPane optionsMenu = new ScrollPane(lstOptions);

    // when clicking on an item of the list
    lstOptions.addListener(
            new InputListener() {
              @Override
              public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                if (lstOptions.getSelected().equals("OK")) {
                  dialog.remove();
                }
                return true;
              }
            });

    table.add(optionsMenu);
    table.row();

    return table;
  }

  // general buttons

  private void createExitButton() {
    btnExit = new TextButton("Exit", skinUI, "default");
    btnExit.setWidth(100);
    btnExit.setHeight(25);
    btnExit.setPosition(
            (Gdx.graphics.getWidth() - btnExit.getWidth() - 8),
            (Gdx.graphics.getHeight() - btnExit.getHeight() - 8));

    btnExit.addListener(
            new ClickListener() {
              @Override
              public void clicked(InputEvent event, float x, float y) {
                game.setScreen(game.lobbyScreen);
              }
            });
  }

  private void createChatButton() {
    btnChat = new TextButton("Chat", skinUI, "default");
    btnChat.setWidth(100);
    btnChat.setHeight(25);

    final float xPosBtnChat = Gdx.graphics.getWidth() - btnExit.getWidth() - 8;
    final float yPosBtnChat = Gdx.graphics.getHeight() - btnExit.getHeight() - 15 - btnChat.getHeight();

    btnChat.setPosition(xPosBtnChat, yPosBtnChat);

    btnChat.addListener(
            new ClickListener() {
              @Override
              public void clicked(InputEvent event, float x, float y) {
                boardMovesPanel.removeMovesAndDirectionsPanel();
                boardChatFragment.createChatFragment();
                createResumeGameButton(xPosBtnChat, yPosBtnChat);
                btnChat.remove();
              }
            });

    stage.addActor(btnChat);
  }

  private void createResumeGameButton(float x, float y) {
    btnResumeGame = new TextButton("Resume", skinUI, "default");
    btnResumeGame.setWidth(100);
    btnResumeGame.setHeight(25);
    btnResumeGame.setPosition(x, y);

    btnResumeGame.addListener(
            new ClickListener() {
              @Override
              public void clicked(InputEvent event, float x, float y) {
                if (boardChatFragment.scrollPaneMsg != null) {
                  boardChatFragment.removeChatFragment();
                }

                createChatButton();
                boardMovesPanel.createMovesAndDirectionsPanel();
                btnResumeGame.remove();
              }
            });

    stage.addActor(btnResumeGame);
  }

}
