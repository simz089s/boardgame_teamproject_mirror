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

  SpriteBatch batch;
  Texture txtrBG;
  Sprite spriteBG;

  Label APLeftTxt;

  ScrollPane scrollPaneMoveOptions;
  ScrollPane.ScrollPaneStyle scrollStyle;
  List<String> lstMoveOptions;
  List.ListStyle listStyleMoveOptions;

  ScrollPane scrollPaneMoveDirections;
  ScrollPane.ScrollPaneStyle scrollStyleDirections;
  List<String> lstMoveDirections;
  List.ListStyle listStyleDirections;

  Dialog dialog;

  TextButton btnExit;
  TextButton btnChat;
  TextButton btnStat;

  // tiles properties
  final int NUMBER_OF_ROWS = BoardManager.HEIGHT;
  final int NUMBER_OF_COLS = BoardManager.WIDTH;

  final int WALL_THICKNESS = 5;
  final int TILE_SIZE = 75;

  Image[][] tilesImg = new Image[NUMBER_OF_ROWS][NUMBER_OF_COLS];

  Tile[][] tiles = BoardManager.getInstance().getTiles();
  FireFighterTurnManager fireFighterTurnManager = FireFighterTurnManager.getInstance();
  Stage stage;

  final String[] OPTIONS_ARR = {
          "MOVE", "EXTINGUISH", "CHOP", "MOVE WITH VICTIM", "INTERACT WITH DOOR", "END TURN", "SAVE"
  };

  final String[] DIRECTIONS_ARR = {"UP", "DOWN", "LEFT", "RIGHT", "CANCEL"};

  // reference to game units images
  ArrayList<Image> gameUnits = new ArrayList<Image>();
  ArrayList<ScrollPane> directionsList = new ArrayList<ScrollPane>();

  BoardScreen(Game pGame) {
    super(pGame);
  }

  @Override
  public void show() {

    BoardManager myBoardManager = DBHandler.getBoardFromDB();

    for (int i = 0; i < 4; i++) {
      myBoardManager.endTurnFireSpread(5,5);
    }

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
    for (int i = 0; i < NUMBER_OF_ROWS; i++) {
      for (int j = 0; j < NUMBER_OF_COLS; j++) {
        if (j == 0) {
          curYPos = Gdx.graphics.getHeight() - (i + 1) * TILE_SIZE;
        }

        String tileFileName1 = "boards/tile.png"; // basic tile image
        //String tileFileName2 = "boards/board1_tiles/row-" + (i + 1) + "-col-" + (j + 1) + ".jpg"; // tiles with furniture image

        tilesImg[i][j] = new Image(new Texture(tileFileName1));
        tilesImg[i][j].setHeight(TILE_SIZE);
        tilesImg[i][j].setWidth(TILE_SIZE);
        tilesImg[i][j].setPosition(j * TILE_SIZE + leftPadding + (j + 1) * WALL_THICKNESS, curYPos - topPadding - (i + 1) * WALL_THICKNESS);

        stage.addActor(tilesImg[i][j]);
        drawGameUnitOnTile(tilesImg[i][j], i, j);
      }
    }

    createExitButton();
    createChatButton();
    createStatButton();
    createAPLeftLabel();
    createMovesList();

    lstMoveOptions.addListener(new InputListener() {
      @Override
      public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {

        int indexSelected = lstMoveOptions.getSelectedIndex();

        if (OPTIONS_ARR[indexSelected].equals("MOVE")) {
          clearDirectionsScrollPane();
          createDirectionList(OPTIONS_ARR[indexSelected]);
          stage.addActor(scrollPaneMoveDirections);

        } else if (OPTIONS_ARR[indexSelected].equals("EXTINGUISH")) {
          clearDirectionsScrollPane();
          createDirectionList(OPTIONS_ARR[indexSelected]);
          stage.addActor(scrollPaneMoveDirections);

        } else if (OPTIONS_ARR[indexSelected].equals("CHOP")) {
          clearDirectionsScrollPane();
          createDirectionList(OPTIONS_ARR[indexSelected]);
          stage.addActor(scrollPaneMoveDirections);

        } else if (OPTIONS_ARR[indexSelected].equals("MOVE WITH VICTIM")) {
          clearDirectionsScrollPane();
          createDirectionList(OPTIONS_ARR[indexSelected]);
          stage.addActor(scrollPaneMoveDirections);

        } else if (OPTIONS_ARR[indexSelected].equals("INTERACT WITH DOOR")) {
          clearDirectionsScrollPane();
          createDirectionList(OPTIONS_ARR[indexSelected]);
          stage.addActor(scrollPaneMoveDirections);

        } else if (OPTIONS_ARR[indexSelected].equals("END TURN")) {
          clearAllGameUnits();
          fireFighterTurnManager.endTurn();
          redrawGameUnitsOnTile();
        }  else if (OPTIONS_ARR[indexSelected].equals("SAVE")) {


        }  else {
          debugLbl.setText("failed action");
        }

        return true;
      }
    });

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

    btnStat.addListener(
            new ClickListener() {
              @Override
              public void clicked(InputEvent event, float x, float y) {
                game.setScreen(game.statsScreen);
              }
            });

    stage.addActor(scrollPaneMoveOptions);
    stage.addActor(btnExit);
    stage.addActor(btnChat);
    stage.addActor(btnStat);
    stage.addActor(APLeftTxt);
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


  public void redrawGameUnitsOnTile() {
    for (int i = 0; i < tiles.length; i++) {
      for (int j = 0; j < tiles[i].length; j++) {
        drawGameUnitOnTile(tilesImg[i][j], i, j);
      }
    }
  }

  private void drawObstacles(Image myTile, Obstacle obs, Direction d) {
    Image gameUnit;
    if (obs.isNull() ) {
      return;

    } else if (obs.isDoor()) {
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

  private void drawGameUnitOnTile(Image myTile, int i, int j) {

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

    if (!tiles[i][j].getFirefighters().isEmpty()) { // placed at top left corner of tile

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
    // Point of Interest Rendering
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

    // Smoke And Fire Rendering
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

  private void createStatButton() {
    btnStat = new TextButton("Stat", skinUI, "default");
    btnStat.setWidth(100);
    btnStat.setHeight(25);
    btnStat.setPosition(
            (Gdx.graphics.getWidth() - btnExit.getWidth() - 8),
            (Gdx.graphics.getHeight() - btnExit.getHeight() - 22 - btnChat.getHeight() - btnStat.getHeight()));
  }

  private void createAPLeftLabel() {
    int numAP = fireFighterTurnManager.getCurrentFireFighter().getActionPointsLeft();
    APLeftTxt = new Label("AP LEFT: " + numAP, skinUI);
    APLeftTxt.setPosition(
            850,
            Gdx.graphics.getHeight() - 100);
    APLeftTxt.setColor(Color.BLACK);
  }

  private void createMovesList() {
    // list style
    listStyleMoveOptions = new List.ListStyle();
    listStyleMoveOptions.font = Font.get(25); // font size
    listStyleMoveOptions.fontColorUnselected = Color.BLACK;
    listStyleMoveOptions.fontColorSelected = Color.BLACK;
    listStyleMoveOptions.selection = TextureLoader.getDrawable(50, 100, Color.SKY );

    lstMoveOptions = new List<String>(listStyleMoveOptions);
    lstMoveOptions.setItems(OPTIONS_ARR);

    // scrollPane style
    scrollStyle = new ScrollPane.ScrollPaneStyle();
    scrollStyle.vScrollKnob = TextureLoader.getDrawable(15, 15, Color.DARK_GRAY);
    scrollStyle.vScroll = TextureLoader.getDrawable(15, 15, Color.LIGHT_GRAY);

    scrollPaneMoveOptions = new ScrollPane(lstMoveOptions, scrollStyle);
    scrollPaneMoveOptions.setOverscroll(false, false);
    scrollPaneMoveOptions.setFadeScrollBars(false);
    scrollPaneMoveOptions.setScrollingDisabled(true, false);
    scrollPaneMoveOptions.setTransform(true);
    scrollPaneMoveOptions.setScale(1.0f);
    scrollPaneMoveOptions.setWidth(300);
    scrollPaneMoveOptions.setHeight(200);
    //scrollMessage.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() + 100);
    scrollPaneMoveOptions.setPosition(
            850,
            Gdx.graphics.getHeight() - scrollPaneMoveOptions.getHeight() - 150);
  }

  private void createDirectionList(String moveSelected){

    // list style
    listStyleDirections = new List.ListStyle();
    listStyleDirections.font = Font.get(25); // font size
    listStyleDirections.fontColorUnselected = Color.BLACK;
    listStyleDirections.fontColorSelected = Color.BLACK;
    listStyleDirections.selection = TextureLoader.getDrawable(50, 100, Color.YELLOW);

    lstMoveDirections = new List<String>(listStyleDirections);
    lstMoveDirections.setItems(DIRECTIONS_ARR);

    // scrollPane style
    scrollStyleDirections = new ScrollPane.ScrollPaneStyle();
    scrollStyleDirections.vScrollKnob = TextureLoader.getDrawable(15, 15, Color.DARK_GRAY);
    scrollStyleDirections.vScroll = TextureLoader.getDrawable(15, 15, Color.LIGHT_GRAY);

    scrollPaneMoveDirections = new ScrollPane(lstMoveDirections, scrollStyleDirections);
    scrollPaneMoveDirections.setOverscroll(false, false);
    scrollPaneMoveDirections.setFadeScrollBars(false);
    scrollPaneMoveDirections.setScrollingDisabled(true, false);
    scrollPaneMoveDirections.setTransform(true);
    scrollPaneMoveDirections.setScale(1.0f);
    scrollPaneMoveDirections.setWidth(300);
    scrollPaneMoveDirections.setHeight(200);
    scrollPaneMoveDirections.setPosition(
            850,
            Gdx.graphics.getHeight() - scrollPaneMoveDirections.getHeight() - 400);


    final String move = moveSelected;

    lstMoveDirections.addListener(new InputListener() {
      @Override
      public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {

        int indexSelected = lstMoveDirections.getSelectedIndex();

        if (DIRECTIONS_ARR[indexSelected].equals("UP")) {
          if (move.equals("MOVE")){
            clearAllGameUnits();
            fireFighterTurnManager.move(Direction.TOP);
            redrawGameUnitsOnTile();
          } else if (move.equals("EXTINGUISH")){
            clearAllGameUnits();
            fireFighterTurnManager.extinguishFire(Direction.TOP);
            redrawGameUnitsOnTile();
          } else if (move.equals("CHOP")){
            clearAllGameUnits();
            fireFighterTurnManager.chopWall(Direction.TOP);
            redrawGameUnitsOnTile();
          } else if (move.equals("MOVE WITH VICTIM")){
            clearAllGameUnits();
            fireFighterTurnManager.moveWithVictim(Direction.TOP);
            redrawGameUnitsOnTile();
          } else if (move.equals("INTERACT WITH DOOR")){
            clearAllGameUnits();
            fireFighterTurnManager.interactWithDoor(Direction.TOP);
            redrawGameUnitsOnTile();
          }

        } else if (DIRECTIONS_ARR[indexSelected].equals("DOWN")) {
          if (move.equals("MOVE")){
            clearAllGameUnits();
            fireFighterTurnManager.move(Direction.BOTTOM);
            redrawGameUnitsOnTile();
          } else if (move.equals("EXTINGUISH")){
            clearAllGameUnits();
            fireFighterTurnManager.extinguishFire(Direction.BOTTOM);
            redrawGameUnitsOnTile();
          } else if (move.equals("CHOP")){
            clearAllGameUnits();
            fireFighterTurnManager.chopWall(Direction.BOTTOM);
            redrawGameUnitsOnTile();
          } else if (move.equals("MOVE WITH VICTIM")){
            clearAllGameUnits();
            fireFighterTurnManager.moveWithVictim(Direction.BOTTOM);
            redrawGameUnitsOnTile();
          } else if (move.equals("INTERACT WITH DOOR")){
            clearAllGameUnits();
            fireFighterTurnManager.interactWithDoor(Direction.BOTTOM);
            redrawGameUnitsOnTile();
          }
        } else if (DIRECTIONS_ARR[indexSelected].equals("LEFT")) {
          if (move.equals("MOVE")){
            clearAllGameUnits();
            fireFighterTurnManager.move(Direction.LEFT);
            redrawGameUnitsOnTile();
          } else if (move.equals("EXTINGUISH")){
            clearAllGameUnits();
            fireFighterTurnManager.extinguishFire(Direction.LEFT);
            redrawGameUnitsOnTile();
          } else if (move.equals("CHOP")){
            clearAllGameUnits();
            fireFighterTurnManager.chopWall(Direction.LEFT);
            redrawGameUnitsOnTile();
          } else if (move.equals("MOVE WITH VICTIM")){
            clearAllGameUnits();
            fireFighterTurnManager.moveWithVictim(Direction.LEFT);
            redrawGameUnitsOnTile();
          } else if (move.equals("INTERACT WITH DOOR")){
            clearAllGameUnits();
            fireFighterTurnManager.interactWithDoor(Direction.LEFT);
            redrawGameUnitsOnTile();
          }

        } else if (DIRECTIONS_ARR[indexSelected].equals("RIGHT")) {
          if (move.equals("MOVE")){
            clearAllGameUnits();
            fireFighterTurnManager.move(Direction.RIGHT);
            redrawGameUnitsOnTile();
          } else if (move.equals("EXTINGUISH")){
            clearAllGameUnits();
            fireFighterTurnManager.extinguishFire(Direction.RIGHT);
            redrawGameUnitsOnTile();

          } else if (move.equals("CHOP")){
            clearAllGameUnits();
            fireFighterTurnManager.chopWall(Direction.RIGHT);
            redrawGameUnitsOnTile();
          } else if (move.equals("MOVE WITH VICTIM")){
            clearAllGameUnits();
            fireFighterTurnManager.moveWithVictim(Direction.RIGHT);
            redrawGameUnitsOnTile();
          } else if (move.equals("INTERACT WITH DOOR")){
            clearAllGameUnits();
            fireFighterTurnManager.interactWithDoor(Direction.RIGHT);
            redrawGameUnitsOnTile();
          }

        } else if (DIRECTIONS_ARR[indexSelected].equals("CANCEL")){

        }

        APLeftTxt.setText("AP LEFT: " + fireFighterTurnManager.getCurrentFireFighter().getActionPointsLeft());

        scrollPaneMoveDirections.remove();

        return true;
      }
    });

    directionsList.add(scrollPaneMoveDirections);
  }

  private void clearAllGameUnits() {
    // System.out.println(gameUnits); // for test
    for (int i = 0; i < gameUnits.size(); i++) {
      gameUnits.get(i).remove();
    }
    gameUnits.clear();
  }

  private void clearDirectionsScrollPane(){
    for (int i = 0; i < directionsList.size(); i++) {
      directionsList.get(i).remove();
    }
    directionsList.clear();
  }

  //  private void createDialog(String message){
//    dialog =
//            new Dialog("Choice", skinUI, "dialog") {
//              public void result(Object obj) {}
//            };
//    dialog.add(createDialogContent(message));
//    dialog.show(stage);
//  }
//
//  public Table createDialogContent(String message) {
//
//    final String[] dialogOptArr = {"CANCEL"};
//
//    Table table = new Table(skinUI);
//    table.add(new Label(message, skinUI));
//    table.row();
//
//    final List<String> lstOptions = new List<String>(skinUI);
//    lstOptions.setItems(dialogOptArr);
//    ScrollPane optionsMenu = new ScrollPane(lstOptions);
//
//    // when clicking on an item of the list
//    lstOptions.addListener(
//            new InputListener() {
//              @Override
//              public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
//
//                if (dialogOptArr[lstOptions.getSelectedIndex()].equals("CANCEL")) {
//                  dialog.cancel();
//                }
//                return true;
//              }
//            });
//
//    table.add(optionsMenu);
//    table.row();
//
//    return table;
//  }

}
