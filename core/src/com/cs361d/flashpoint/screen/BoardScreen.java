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
import com.cs361d.flashpoint.model.BoardElements.*;
import com.cs361d.flashpoint.networking.Commands;
import com.cs361d.flashpoint.networking.NetworkManager;

import java.util.ArrayList;

public class BoardScreen extends FlashPointScreen {

  String BOARD_TO_DISPLAY_FILE = "boards/tile_1.png";

  static final int NUMBER_OF_ROWS = BoardManager.ROWS;
  static final int NUMBER_OF_COLS = BoardManager.COLUMNS;

  static final int WALL_THICKNESS = 5;
  static final int TILE_SIZE = 75;
  static Image[][] tilesImg = new Image[NUMBER_OF_ROWS][NUMBER_OF_COLS];
  static ArrayList<Image> gameUnits = new ArrayList<Image>();

  // choose init pos clickable tiles
  final String[] CHOOSE_INIT_POS_TILES = {
          "0-0", "1-0", "2-0", "3-0", "4-0", "5-0", "6-0", "7-0", "0-1", "0-2", "0-3", "0-4", "0-5",
          "0-6", "0-7", "0-8", "0-9", "1-9", "2-9", "3-9", "4-9", "5-9", "6-9", "7-1", "7-2", "7-3",
          "7-4", "7-5", "7-6", "7-7", "7-8", "7-9",
  };

  // choose pos on knock down
  static boolean activateKnockDownChoosePos = false;
  static FireFighter knockedDownFirefigher;
  static ArrayList<Tile> clickableTilesOnKnockDownArr = new ArrayList<Tile>();

  // GUI elements
  SpriteBatch batch;
  Texture txtrBG;
  Sprite spriteBG;
  static Stage stage;

  private static Music BGM = Gdx.audio.newMusic(Gdx.files.internal("playlist/battle_normal01.mp3"));

  static BoardChooseRolePanel boardChooseRolePanel;
  static BoardMovesPanel boardMovesPanel;
  static BoardChatFragment boardChatFragment;
  static BoardCheatSFragment boardCheatSFragment;
  static BoardStatsFragment boardStatsFragment;

  static ImageButton btnExit;
  static Label gameInfoLabel;
  static ImageButton btnResume;
  ImageButton btnChat;
  ImageButton btnCheatS;
  ImageButton btnStats;
  ImageButton btnMusicSound;

  static Fragment currentFragment = Fragment.EMPTY;

  BoardScreen(Game pGame) {
    super(pGame);
    BGM.setLooping(true);
  }

  @Override
  public void show() {

    // DBHandler.createBoard(MapKind.MAP2); // generate initial map

    BGM.play();

    stage = new Stage();
    batch = new SpriteBatch();

    txtrBG = new Texture("empty.png");
    spriteBG = new Sprite(txtrBG);
    spriteBG.setPosition(0, 0);

    boardChooseRolePanel = new BoardChooseRolePanel(stage);
    boardMovesPanel = new BoardMovesPanel(stage);
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

        // BOARD_TO_DISPLAY_FILE = "boards/board1_tiles/row-" + (i + 1) + "-col-" + (j + 1) +
        // ".jpg"; // tiles with furniture image

        tilesImg[i][j] = new Image(new Texture(BOARD_TO_DISPLAY_FILE));
        tilesImg[i][j].setHeight(TILE_SIZE);
        tilesImg[i][j].setWidth(TILE_SIZE);
        tilesImg[i][j].setPosition(
                j * TILE_SIZE + leftPadding + (j + 1) * WALL_THICKNESS,
                curYPos - topPadding - (i + 1) * WALL_THICKNESS);

        final int i_pos = i;
        final int j_pos = j;

        tilesImg[i][j].addListener(
                new ClickListener() {
                  @Override
                  public void clicked(InputEvent event, float x, float y) {
                    if (!FireFighterTurnManager.getInstance().allAssigned()
                            && DBHandler.isPresentInArr(CHOOSE_INIT_POS_TILES, i_pos + "-" + j_pos)
                            ) {
                      clearAllGameUnits();
                      Tile[][] tiles = BoardManager.getInstance().getTiles();

                      try {
                        FireFighterTurnManager.getInstance().chooseInitialPosition(tiles[i_pos][j_pos]);
                      } catch (IllegalAccessException e) {
                        e.printStackTrace();
                      }
                      try {
                        Thread.sleep(100);
                      } catch (InterruptedException e) {
                        e.printStackTrace();
                      }
                      removeAllFilterOnTile();
                      drawGameUnitsOnTile();
                      updateGameInfoLabel();

                      if (!FireFighterTurnManager.getInstance().allAssigned()) {
                        addFilterOnTileForChooseInitPos();
                      } else {
                        createAllGameButtons();

                        // moves panel
                        removeAllPrevFragments();
                        boardMovesPanel.createMovesAndDirectionsPanel();

                        createEngineTilesColor();
                      }
                    }

                    // choose tile on knock down
                    if (activateKnockDownChoosePos && isClickableTileOnKnockDown(i_pos, j_pos)) {
                      clearAllGameUnits();

                      BoardManager.getInstance()
                              .chooseForKnockedDown(
                                      BoardManager.getInstance().getTiles()[i_pos][j_pos],
                                      knockedDownFirefigher);

                      activateKnockDownChoosePos = false;

                      removeAllFilterOnTile();
                      drawGameUnitsOnTile();
                      updateGameInfoLabel();
                    }
                  }
                });

        stage.addActor(tilesImg[i][j]);
      }
    }

    drawGameUnitsOnTile();

    //createSoundButton();
    createGameInfoLabel();

    // Choose init pos
    if (!FireFighterTurnManager.getInstance().allAssigned()) {
      if(User.getInstance().isMyTurn()) {
        createDialog("Ready, set, go!", "Choose your initial position on the board (green tiles).");
        addFilterOnTileForChooseInitPos();
        boardChooseRolePanel.createChooseRolePanel();
      }
    } else {
      createAllGameButtons();

      // moves panel
      removeAllPrevFragments();
      boardMovesPanel.createMovesAndDirectionsPanel();

      createEngineTilesColor();
    }

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
    if (obs.isNull()) {
      return;

    } else if (obs.isDoor()) { // door

      if (obs.isDestroyed()) {
        gameUnit = new Image(new Texture("game_units/walls/Destroyed_Door.png"));
      } else if (obs.isOpen()) {
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
          gameUnit.setPosition(myTile.getX() - WALL_THICKNESS, myTile.getY());
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
          gameUnit.setPosition(myTile.getX() + myTile.getWidth(), myTile.getY());
          break;

        default:
          throw new IllegalArgumentException("That argument does not exist");
      }
    }
    gameUnits.add(gameUnit);
    stage.addActor(gameUnit);
  }

  // draw all game units: obstacles, firefighter (top left), victim (top right), fire_status =
  // smoke, fire (bottom left)
  private static void drawGameUnitsOnTile(Image myTile, int i, int j) {

    Tile[][] tiles = BoardManager.getInstance().getTiles();
    Obstacle top = tiles[i][j].getObstacle(Direction.TOP);
    Obstacle left = tiles[i][j].getObstacle(Direction.LEFT);

    drawObstacles(myTile, top, Direction.TOP);
    drawObstacles(myTile, left, Direction.LEFT);

    if (j == BoardManager.COLUMNS - 1) {
      Obstacle right = tiles[i][j].getObstacle(Direction.RIGHT);
      drawObstacles(myTile, right, Direction.RIGHT);
    }

    if (i == BoardManager.ROWS - 1) {
      Obstacle bottom = tiles[i][j].getObstacle(Direction.BOTTOM);
      drawObstacles(myTile, bottom, Direction.BOTTOM);
    }

    Image gameUnit;

    // Vehicles
    //TODO: boolean to check tiles[i][j].hasVehicle()
    if (true) {
      gameUnit = null;
      if(true){ // TODO: tiles[i][j].hasAmbulance()
        if(getVehicleOrientationOnTile(i, j, CarrierStatus.HASAMBULANCE, Orientation.HORIZONTAL)) { // horizontally positioned
          gameUnit = new Image(new Texture("game_units/vehicles/h_ambulance.png"));
          gameUnit.setHeight(60);
          gameUnit.setWidth(100);
          gameUnit.setPosition(myTile.getX() + 25, myTile.getY() + 8);
        } else if (getVehicleOrientationOnTile(i, j, CarrierStatus.HASAMBULANCE, Orientation.VERTICAL)){ // vertically positioned
          gameUnit = new Image(new Texture("game_units/vehicles/v_ambulance.png"));
          gameUnit.setHeight(100);
          gameUnit.setWidth(60);
          gameUnit.setPosition(myTile.getX() + 8, myTile.getY() + 25);
        }
      }

      if (true){ // TODO: tiles[i][j].hasEngine()
        if(getVehicleOrientationOnTile(i, j, CarrierStatus.HASFIRETRUCK, Orientation.HORIZONTAL)) { // horizontally positioned
          gameUnit = new Image(new Texture("game_units/vehicles/h_engine.png"));
          gameUnit.setHeight(60);
          gameUnit.setWidth(100);
          gameUnit.setPosition(myTile.getX() + 25, myTile.getY() + 8);
        } else if (getVehicleOrientationOnTile(i, j, CarrierStatus.HASFIRETRUCK, Orientation.VERTICAL)){ // vertically positioned
          gameUnit = new Image(new Texture("game_units/vehicles/v_engine.png"));
          gameUnit.setHeight(100);
          gameUnit.setWidth(60);
          gameUnit.setPosition(myTile.getX() + 8, myTile.getY() + 25);
        }
      }

      if (gameUnit != null){
        gameUnits.add(gameUnit);
        stage.addActor(gameUnit);
      }
    }

    // Firefighters
    if (!tiles[i][j].getFirefighters().isEmpty()) {
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
    if (tiles[i][j].hasPointOfInterest()) {
      gameUnit = new Image(new Texture("game_units/POI_Rear.png"));
      if (tiles[i][j].getVictim().isRevealed()) {
        gameUnit = new Image(new Texture("game_units/Victim.png"));
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

    // Hazmat
    //TODO: boolean to check tiles[i][j].hasHazmat()
    if (false) { // placed at bottom right corner of tile
      gameUnit = new Image(new Texture("game_units/Hazmat.png"));
      gameUnit.setHeight(30);
      gameUnit.setWidth(30);
      gameUnit.setPosition(myTile.getX() + 45, myTile.getY());

      gameUnits.add(gameUnit);
      stage.addActor(gameUnit);
    }

    // Hot spot
    //TODO: boolean to check tiles[i][j].hasHotSpot()
    if (false) { // placed at bottom right corner of tile
      gameUnit = new Image(new Texture("game_units/Hot_Spot.png"));
      gameUnit.setHeight(15);
      gameUnit.setWidth(15);
      gameUnit.setPosition(myTile.getX() + 30, myTile.getY() + 28);

      gameUnits.add(gameUnit);
      stage.addActor(gameUnit);
    }
  }

  public static void drawGameUnitsOnTile() {
    Tile[][] tiles = BoardManager.getInstance().getTiles();
    for (int i = 0; i < tiles.length; i++) {
      for (int j = 0; j < tiles[i].length; j++) {
        drawGameUnitsOnTile(tilesImg[i][j], i, j);
      }
    }
  }



  // game info label



  private void createGameInfoLabel() {

    if (FireFighterTurnManager.getInstance().getCurrentFireFighter() == null) {
      return;
    }
    int numAP = FireFighterTurnManager.getInstance().getCurrentFireFighter().getActionPointsLeft();
    FireFighterColor color =
            FireFighterTurnManager.getInstance().getCurrentFireFighter().getColor();

    String specialty = "\n";
    if(true){ //TODO: boolean to check if experienced game mode
      specialty = "\n"; //TODO: User.getInstance().getSpecialty()
    }

    gameInfoLabel =
            new Label(

                      User.getInstance().getName().toUpperCase()
                              + specialty
                            //+ "\nYour Color: "
                            //+ User.getInstance().getColor()
                            + "\nCurrent turn: "
                            + color
                            + "\nAP left: "
                            + numAP,
                    skinUI);
    gameInfoLabel.setFontScale(1.2f);
    gameInfoLabel.setColor(Color.BLACK);
    gameInfoLabel.setPosition(850, Gdx.graphics.getHeight() - 130);

    stage.addActor(gameInfoLabel);
  }

  public static void updateGameInfoLabel() {
    FireFighterTurnManager ft = FireFighterTurnManager.getInstance();
    if (ft.getInstance().getCurrentFireFighter() == null) {
      return;
    }
    int APLeft = FireFighterTurnManager.getInstance().getCurrentFireFighter().getActionPointsLeft();
    FireFighterColor color =
            FireFighterTurnManager.getInstance().getCurrentFireFighter().getColor();

    String specialty = "\n";
    if(true){ //TODO: boolean to check if experienced game mode
      specialty = "\n"; //TODO: User.getInstance().getSpecialty()
    }

    gameInfoLabel.setText(
                    User.getInstance().getName().toUpperCase()
                            + specialty
                    //+ "\nPlaying Color: "
                    //+ User.getInstance().getColor()
                    + "\nCurrent turn: "
                    + color
                    + "\nAP left: "
                    + APLeft);

    if (APLeft == 0) {
      gameInfoLabel.setColor(Color.RED);
    }
  }



  // dialogs



  public static void createDialog(String title, String message) {
    Dialog dialog =
            new Dialog(title, skinUI, "dialog") {
              public void result(Object obj) {
                remove();
              }
            };

    dialog.text(message);
    dialog.button("OK", true);
    dialog.show(stage);
  }

  public static void createEndGameDialog(String title, String message) {
    BoardManager.getInstance().setGameEnded();
    Dialog dialog =
            new Dialog(title, skinUI, "dialog") {
              public void result(Object obj) {
                if ((Boolean) obj) {
                  BGM.stop();
                  game.setScreen(game.lobbyScreen);
                }
              }
            };

    dialog.text(message);
    dialog.button("OK", true);
    dialog.show(stage);
  }



  // right menu buttons



  private void createAllGameButtons() {
    createExitButton();
    createCheatSButton();
    createStatsButton();
    createChatButton();
    createResumeButton();
  }

  private void createExitButton() {
    Texture myTexture = new Texture(Gdx.files.internal("icons/exitBtn.png"));
    TextureRegion myTextureRegion = new TextureRegion(myTexture);
    TextureRegionDrawable myTexRegionDrawable = new TextureRegionDrawable(myTextureRegion);

    btnExit = new ImageButton(myTexRegionDrawable);

    btnExit.setWidth(50);
    btnExit.setHeight(50);

    final float xPosBtnExit = Gdx.graphics.getWidth() - btnExit.getWidth() - 100;
    final float yPosBtnExit = Gdx.graphics.getHeight() - btnExit.getHeight() - 8;

    btnExit.setPosition(xPosBtnExit, yPosBtnExit);

    btnExit.addListener(
            new ClickListener() {
              @Override
              public void clicked(InputEvent event, float x, float y) {
                Dialog dialog =
                        new Dialog("Warning", skinUI, "dialog") {
                          public void result(Object obj) {
                            if ((Boolean) obj) {
                              NetworkManager.getInstance().sendCommand(Commands.DISCONNECT, "");
                              BGM.stop();
                              game.setScreen(game.lobbyScreen);
                            }
                          }
                        };
                NetworkManager.getInstance().sendCommand(Commands.EXITGAME, "");
              }
            });

    stage.addActor(btnExit);
  }

  private void createCheatSButton() {
    Texture myTexture = new Texture(Gdx.files.internal("icons/cheatSheetBtn.png"));
    TextureRegion myTextureRegion = new TextureRegion(myTexture);
    TextureRegionDrawable myTexRegionDrawable = new TextureRegionDrawable(myTextureRegion);

    btnCheatS = new ImageButton(myTexRegionDrawable);

    btnCheatS.setWidth(50);
    btnCheatS.setHeight(50);

    final float xPosBtnCheatS = Gdx.graphics.getWidth() - btnExit.getWidth() - 110;
    final float yPosBtnCheatS = Gdx.graphics.getHeight() - btnExit.getHeight() * 2 - 10;

    btnCheatS.setPosition(xPosBtnCheatS, yPosBtnCheatS);

    btnCheatS.addListener(
            new ClickListener() {
              @Override
              public void clicked(InputEvent event, float x, float y) {
                setSideFragment(Fragment.CHEATSHEET);
              }
            });

    stage.addActor(btnCheatS);
  }

  private void createStatsButton() {
    Texture myTexture = new Texture(Gdx.files.internal("icons/statBtn.png"));
    TextureRegion myTextureRegion = new TextureRegion(myTexture);
    TextureRegionDrawable myTexRegionDrawable = new TextureRegionDrawable(myTextureRegion);

    btnStats = new ImageButton(myTexRegionDrawable);

    btnStats.setWidth(50);
    btnStats.setHeight(50);

    final float xPosBtnStats = Gdx.graphics.getWidth() - btnExit.getWidth() - 65;
    final float yPosBtnStats = Gdx.graphics.getHeight() - btnExit.getHeight() * 2 - 43;

    btnStats.setPosition(xPosBtnStats, yPosBtnStats);

    btnStats.addListener(
            new ClickListener() {
              @Override
              public void clicked(InputEvent event, float x, float y) {
                setSideFragment(Fragment.STATS);
              }
            });

    stage.addActor(btnStats);
  }

  private void createChatButton() {

    Texture myTexture = new Texture(Gdx.files.internal("icons/chatBtn.png"));
    TextureRegion myTextureRegion = new TextureRegion(myTexture);
    TextureRegionDrawable myTexRegionDrawable = new TextureRegionDrawable(myTextureRegion);

    btnChat = new ImageButton(myTexRegionDrawable);

    btnChat.setWidth(50);
    btnChat.setHeight(50);


    final float xPosBtnChat = Gdx.graphics.getWidth() - btnExit.getWidth() - 8;
    final float yPosBtnChat = Gdx.graphics.getHeight() - btnExit.getHeight() * 2 - 50;

    btnChat.setPosition(xPosBtnChat, yPosBtnChat);

    btnChat.addListener(
            new ClickListener() {
              @Override
              public void clicked(InputEvent event, float x, float y) {
                NetworkManager.getInstance().sendCommand(Commands.GET_CHAT_MESSAGES,"");
              }
            });

    stage.addActor(btnChat);
  }

  // show MovesAndDirectionsPanel on resume
  private static void createResumeButton() {

    Texture myTexture = new Texture(Gdx.files.internal("icons/resumeBtn_" + User.getInstance().getColor() + ".png"));

    TextureRegion myTextureRegion = new TextureRegion(myTexture);
    TextureRegionDrawable myTexRegionDrawable = new TextureRegionDrawable(myTextureRegion);

    btnResume = new ImageButton(myTexRegionDrawable);
    btnResume.setWidth(100);
    btnResume.setHeight(100);

    final float x = Gdx.graphics.getWidth() - btnResume.getWidth();
    final float y = Gdx.graphics.getHeight() - btnResume.getHeight();

    btnResume.setPosition(x, y);

    btnResume.addListener(
            new ClickListener() {
              @Override
              public void clicked(InputEvent event, float x, float y) {
                removeAllPrevFragments();
                boardMovesPanel.createMovesAndDirectionsPanel();
              }
            });

    if (User.getInstance().isMyTurn()) {
      stage.addActor(btnResume);
    }
  }

  private void createSoundButton() {

    Texture myTexture = new Texture(Gdx.files.internal("icons/sound.png"));
    TextureRegion myTextureRegion = new TextureRegion(myTexture);
    TextureRegionDrawable myTexRegionDrawable = new TextureRegionDrawable(myTextureRegion);

    btnMusicSound = new ImageButton(myTexRegionDrawable);
    btnMusicSound.setWidth(30);
    btnMusicSound.setHeight(30);

    final float x = Gdx.graphics.getWidth() - btnExit.getWidth() - 75;
    final float y = Gdx.graphics.getHeight() - btnExit.getHeight() * 2 - 15;

    btnMusicSound.setPosition(x, y);

    btnMusicSound.addListener(
            new ClickListener() {
              @Override
              public void clicked(InputEvent event, float x, float y) {
                if (BGM.isPlaying()) {
                  BGM.pause();
                } else {
                  BGM.play();
                }
              }
            });

    stage.addActor(btnMusicSound);
  }



  // add filters



  private void addFilterOnTileForChooseInitPos() {
    for (int i = 0; i < CHOOSE_INIT_POS_TILES.length; i++) {
      int i_pos = Integer.parseInt(CHOOSE_INIT_POS_TILES[i].split("-")[0]);
      int j_pos = Integer.parseInt(CHOOSE_INIT_POS_TILES[i].split("-")[1]);
      tilesImg[i_pos][j_pos].setColor(Color.GREEN);
    }
  }

  public static void addFilterOnKnockDownChoosePos(FireFighter f, ArrayList<Tile> clickableTiles) {

    clickableTilesOnKnockDownArr = clickableTiles;
    knockedDownFirefigher = f;

    for (int i = 0; i < clickableTiles.size(); i++) {
      tilesImg[clickableTiles.get(i).getI()][clickableTiles.get(i).getJ()].setColor(Color.GREEN);
      activateKnockDownChoosePos = true;
    }
  }



  // remove GUI elements



  public static void clearAllGameUnits() {
    for (int i = 0; i < gameUnits.size(); i++) {
      gameUnits.get(i).remove();
    }
    gameUnits.clear();
  }

  private static void removeAllPrevFragments() {
    boardMovesPanel.removeMovesAndDirectionsPanel();
    boardChooseRolePanel.removeChooseInitPosPanel();
    boardChatFragment.removeChatFragment();
    boardCheatSFragment.removeCheatSFragment();
    boardStatsFragment.removeStatsFragment();
  }

  private void removeAllFilterOnTile() {
    for (int i = 0; i < tilesImg.length; i++) {
      for (int j = 0; j < tilesImg[i].length; j++) {
        tilesImg[i][j].setColor(Color.WHITE);
      }
    }
  }



  // helper methods



  private boolean isClickableTileOnKnockDown(int i_input, int j_input) {
    for (int i = 0; i < clickableTilesOnKnockDownArr.size(); i++) {
      int i_pos = clickableTilesOnKnockDownArr.get(i).getI();
      int j_pos = clickableTilesOnKnockDownArr.get(i).getJ();

      if (i_input == i_pos && j_input == j_pos) {
        return true;
      }
    }

    return false;
  }

  private void createEngineTilesColor() {
    // Engines (Ambulance, firetruck)
    Tile[][] tiles = BoardManager.getInstance().getTiles();
    for (int i = 0; i < tiles.length; i++) {
      for (int j = 0; j < tiles[i].length; j++) {
        if (tiles[i][j].canContainAmbulance()) {
          tilesImg[i][j].setColor(Color.SKY);
        } else if (tiles[i][j].canContainFireTruck()) {
          tilesImg[i][j].setColor(Color.ORANGE);
        }
      }
    }
  }

  private static boolean getVehicleOrientationOnTile(int i, int j, CarrierStatus carrierStatus, Orientation orientation) {
    if(true){ //TODO; get the map kind (if mk == 1)
      if(carrierStatus == CarrierStatus.HASAMBULANCE){
        if(orientation == Orientation.VERTICAL){
          return (i == 4 && j == 0) || (i == 4 && j == 9);
        } else if(orientation == Orientation.HORIZONTAL){
          return (i == 0 && j == 5) || (i == 7 && j == 3);
        }
      } else if (carrierStatus == CarrierStatus.HASFIRETRUCK){
        if(orientation == Orientation.VERTICAL){
          return (i == 2 && j == 0) || (i == 6 && j == 9);
        } else if(orientation == Orientation.HORIZONTAL){
          return (i == 0 && j == 7) || (i == 7 && j == 1);
        }
      }
    } else if(false){ //TODO; get the map kind (if mk == 2)
      if(carrierStatus == CarrierStatus.HASAMBULANCE){
        if(orientation == Orientation.VERTICAL){
          return (i == 3 && j == 0) || (i == 5 && j == 9);
        } else if(orientation == Orientation.HORIZONTAL){
          return (i == 0 && j == 5) || (i == 7 && j == 3);
        }
      } else if (carrierStatus == CarrierStatus.HASFIRETRUCK){
        if(orientation == Orientation.VERTICAL){
          return (i == 5 && j == 0) || (i == 3 && j == 9);
        } else if(orientation == Orientation.HORIZONTAL){
          return (i == 0 && j == 3) || (i == 7 && j == 5);
        }
      }
    }

    return false;
  }

  public static void redrawBoard() {
    if (game.getScreen() == game.boardScreen) {
      setBoardScreen();
    }
  }

  public static boolean isOnBoardScreen() {
    return game.getScreen() == game.boardScreen;
  }

  public static void setLobbyPage() {
    BGM.stop();
    if (game.getScreen() == game.boardScreen) {
      game.setScreen(game.lobbyScreen);
      }
  }

  public static void setBoardScreen() {
    BGM.stop();
    game.setScreen(game.boardScreen);
  }

  public static void setSideFragment(Fragment fragment){

    removeAllPrevFragments();

    if (fragment == Fragment.CHEATSHEET){
      boardCheatSFragment.createCheatSFragment();
    } else if (fragment == Fragment.STATS){
      boardStatsFragment.createStatsFragment();
    } else if (fragment == Fragment.CHAT){
      boardChatFragment.createChatFragment();
    }

    currentFragment = fragment;
  }

  public static boolean isChatFragment() {
    return currentFragment == Fragment.CHAT;
  }

  public static boolean isCheatSheetFragment() {
    return currentFragment == Fragment.CHEATSHEET;
  }

  public static boolean isStatsFragment() {
    return currentFragment == Fragment.STATS;
  }
}