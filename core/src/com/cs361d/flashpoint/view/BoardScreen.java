package com.cs361d.flashpoint.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
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

  // choose init pos clickable tiles
  boolean activateChooseInitPos = false;

  final String[] CHOOSE_INIT_POS_TILES = {"0-0", "1-0", "2-0", "3-0", "4-0", "5-0", "6-0", "7-0",
          "0-1","0-2","0-3","0-4","0-5","0-6","0-7","0-8","0-9",
          "1-9", "2-9", "3-9", "4-9", "5-9", "6-9",
          "7-1","7-2","7-3","7-4","7-5","7-6","7-7","7-8","7-9",
  };



  // GUI elements
  SpriteBatch batch;
  Texture txtrBG;
  Sprite spriteBG;
  static Stage stage;

  BoardMovesPanel boardMovesPanel;
  BoardChatFragment boardChatFragment;
  BoardCheatSFragment boardCheatSFragment;
  BoardStatsFragment boardStatsFragment;

  TextButton btnChooseInitPosTest;
  TextButton btnExit;
  TextButton btnChat;
  ImageButton btnResume;
  TextButton btnCheatS;
  TextButton btnStats;
  static Label gameInfoLabel;
  static Dialog dialog;

  BoardScreen(Game pGame) {
    super(pGame);
  }

  @Override
  public void show() {

    //DBHandler.createBoardDBFamilyVersion(); // generate start board
    CreateNewGameManager.createNewGame(3, MapKind.ORIGINAL1, Difficulty.FAMILLY);

    stage = new Stage();
    batch = new SpriteBatch();

    txtrBG = new Texture("empty.png");
    spriteBG = new Sprite(txtrBG);
    spriteBG.setScale(0.6f);
    spriteBG.setPosition(
            -(Gdx.graphics.getWidth() / 2f) - 125, -(Gdx.graphics.getHeight() / 2f) + 30);

    boardChatFragment = new BoardChatFragment(stage);
    boardCheatSFragment = new BoardCheatSFragment(stage);
    boardStatsFragment = new BoardStatsFragment(stage);

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

        final int i_pos = i;
        final int j_pos = j;

        tilesImg[i][j].addListener(
                new ClickListener() {
                  @Override
                  public void clicked(InputEvent event, float x, float y) {
                     if (!FireFighterTurnManager.getInstance().allAssigned() && DBHandler.isPresentInArr(CHOOSE_INIT_POS_TILES, i_pos + "-" + j_pos)){
                      clearAllGameUnits();
                      Tile[][] tiles = BoardManager.getInstance().getTiles();

                      try {
                        FireFighterTurnManager.getInstance().chooseInitialPosition(tiles[i_pos][j_pos]);
                      } catch (IllegalAccessException e) {
                        e.printStackTrace();
                      }

                      activateChooseInitPos = false;
                      removeAllFilterOnTile();
                      redrawGameUnitsOnTile();
                      updateGameInfoLabel();

                      if (!FireFighterTurnManager.getInstance().allAssigned()) {
                        addFilterOnTileForChooseInitPos();
                      } else {
                        createResumeButton();
                        boardMovesPanel.createMovesAndDirectionsPanel();
                      }

                    }
                  }
                });

        stage.addActor(tilesImg[i][j]);
        drawGameUnitsOnTile(tilesImg[i][j], i, j);
      }
    }

    createGameInfoLabel();


    createExitButton();
    createCheatSButton();
    createStatsButton();
    createChatButton();
    createResumeButton();

    // Moves panel
    boardMovesPanel = new BoardMovesPanel(stage);
    boardMovesPanel.createMovesAndDirectionsPanel();

    if (!FireFighterTurnManager.getInstance().allAssigned()){
      addFilterOnTileForChooseInitPos();
      removeAllPrevFragments();
      btnResume.remove();
    }

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

    Tile[][] tiles = BoardManager.getInstance().getTiles();
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
            gameUnit.setPosition(myTile.getX() - 7, myTile.getY() + myTile.getHeight() / 2);
            break;
          case RED:
            gameUnit = new Image(new Texture("game_units/firefighters/red.png"));
            gameUnit.setPosition(myTile.getX() - 4, myTile.getY() + myTile.getHeight() / 2);
            break;
          case GREEN:
            gameUnit = new Image(new Texture("game_units/firefighters/green.png"));
            gameUnit.setPosition(myTile.getX() - 1, myTile.getY() + myTile.getHeight() / 2);
            break;
          case WHITE:
            gameUnit = new Image(new Texture("game_units/firefighters/white.png"));
            gameUnit.setPosition(myTile.getX() + 2, myTile.getY() + myTile.getHeight() / 2);
            break;
          case ORANGE:
            gameUnit = new Image(new Texture("game_units/firefighters/orange.png"));
            gameUnit.setPosition(myTile.getX() + 5, myTile.getY() + myTile.getHeight() / 2);
            break;
          case YELLOW:
            gameUnit = new Image(new Texture("game_units/firefighters/yellow.png"));
            gameUnit.setPosition(myTile.getX() + 8, myTile.getY() + myTile.getHeight() / 2);
            break;
          default:
            throw new IllegalArgumentException(
                    "FireFighter color " + f.getColor() + " does not exists");
        }
        gameUnit.setHeight(30);
        gameUnit.setWidth(30);

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
              myTile.getX() + myTile.getHeight() / 2 + 7, myTile.getY() + myTile.getHeight() / 2 + 7);
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
    Tile[][] tiles = BoardManager.getInstance().getTiles();
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

    int numAP = FireFighterTurnManager.getInstance().getCurrentFireFighter().getActionPointsLeft();
    FireFighterColor color = FireFighterTurnManager.getInstance().getCurrentFireFighter().getColor();
    gameInfoLabel = new Label("Current turn: " + color + "\nAP left: " + numAP, skinUI);
    gameInfoLabel.setPosition(
            850,
            Gdx.graphics.getHeight() - 100);
    gameInfoLabel.setColor(Color.BLACK);
  }

  public static void updateGameInfoLabel(){
    int APLeft = FireFighterTurnManager.getInstance().getCurrentFireFighter().getActionPointsLeft();
    FireFighterColor color = FireFighterTurnManager.getInstance().getCurrentFireFighter().getColor();
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

  // right menu buttons

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
                Dialog dialog = new Dialog("Warning", skinUI, "dialog") {
                  public void result(Object obj) {
                    if ((Boolean) obj){
                      Gdx.app.exit();
                      //game.setScreen(game.lobbyScreen);
                    }
                  }
                };

                dialog.text("Are you sure you want to quit?");
                dialog.button("Yes", true); //sends "true" as the result
                dialog.button("No", false); //sends "false" as the result
                dialog.show(stage);
              }
            });
  }

  private void createCheatSButton() {
    btnCheatS = new TextButton("Cheat sheet", skinUI, "default");
    btnCheatS.setWidth(100);
    btnCheatS.setHeight(25);

    final float xPosBtnCheatS = Gdx.graphics.getWidth() - btnExit.getWidth() - 8;
    final float yPosBtnCheatS = Gdx.graphics.getHeight() - btnExit.getHeight() * 2 - 15;

    btnCheatS.setPosition(xPosBtnCheatS, yPosBtnCheatS);

    btnCheatS.addListener(
            new ClickListener() {
              @Override
              public void clicked(InputEvent event, float x, float y) {
                removeAllPrevFragments();
                boardCheatSFragment.createCheatSFragment();
              }
            });

    stage.addActor(btnCheatS);
  }

  private void createStatsButton() {
    btnStats = new TextButton("Stats", skinUI, "default");
    btnStats.setWidth(100);
    btnStats.setHeight(25);

    final float xPosBtnStats = Gdx.graphics.getWidth() - btnExit.getWidth() - 8;
    final float yPosBtnStats = Gdx.graphics.getHeight() - btnExit.getHeight() * 2 - 48;

    btnStats.setPosition(xPosBtnStats, yPosBtnStats);

    btnStats.addListener(
            new ClickListener() {
              @Override
              public void clicked(InputEvent event, float x, float y) {
                removeAllPrevFragments();
                boardStatsFragment.createStatsFragment();
              }
            });

    stage.addActor(btnStats);
  }

  private void createChatButton() {
    btnChat = new TextButton("Chat", skinUI, "default");
    btnChat.setWidth(100);
    btnChat.setHeight(25);

    final float xPosBtnChat = Gdx.graphics.getWidth() - btnExit.getWidth() - 8;
    final float yPosBtnChat = Gdx.graphics.getHeight() - btnExit.getHeight() * 3 - 55;

    btnChat.setPosition(xPosBtnChat, yPosBtnChat);

    btnChat.addListener(
            new ClickListener() {
              @Override
              public void clicked(InputEvent event, float x, float y) {
                removeAllPrevFragments();
                boardChatFragment.createChatFragment();
              }
            });

    stage.addActor(btnChat);
  }

  // show MovesAndDirectionsPanel on Resume
  private void createResumeButton(){

    Texture myTexture = new Texture(Gdx.files.internal("myResumeBtn.png"));
    TextureRegion myTextureRegion = new TextureRegion(myTexture);
    TextureRegionDrawable myTexRegionDrawable = new TextureRegionDrawable(myTextureRegion);

    btnResume = new ImageButton(myTexRegionDrawable);
    btnResume.setWidth(50);
    btnResume.setHeight(50);

    final float x = Gdx.graphics.getWidth() - btnExit.getWidth() - btnResume.getWidth() - 50;
    final float y = Gdx.graphics.getHeight() - btnExit.getHeight() * 2 - 45;

    btnResume.setPosition(x, y);

    btnResume.addListener(
            new ClickListener() {
              @Override
              public void clicked(InputEvent event, float x, float y) {
                removeAllPrevFragments();
                boardMovesPanel.createMovesAndDirectionsPanel();
              }
            });

    stage.addActor(btnResume);
  }

  private void removeAllPrevFragments(){
      boardMovesPanel.removeMovesAndDirectionsPanel();
      boardChatFragment.removeChatFragment();
      boardCheatSFragment.removeCheatSFragment();
      boardStatsFragment.removeStatsFragment();
  }

  private void addFilterOnTileForChooseInitPos(){
    for (int i = 0; i < CHOOSE_INIT_POS_TILES.length; i++){
      int i_pos = Integer.parseInt(CHOOSE_INIT_POS_TILES[i].split("-")[0]);
      int j_pos = Integer.parseInt(CHOOSE_INIT_POS_TILES[i].split("-")[1]);
      tilesImg[i_pos][j_pos].setColor(Color.GREEN);
    }
  }

  private void addFilterOnAfterKnockdownChoosePos(){

  }

  private void removeAllFilterOnTile(){
    for (int i = 0; i < tilesImg.length; i++){
      for (int j = 0; j < tilesImg[i].length; j++) {
        tilesImg[i][j].setColor(Color.WHITE);
      }
    }
  }

}
