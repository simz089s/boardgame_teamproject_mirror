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
import com.cs361d.flashpoint.model.FireFighterSpecialities.FireFighterAdvanceSpecialities;
import com.cs361d.flashpoint.networking.Client;
import com.cs361d.flashpoint.networking.ServerCommands;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BoardScreen extends FlashPointScreen {

  String BOARD_TO_DISPLAY_FILE = "boards/tile_1.png";

  static final int NUMBER_OF_ROWS = BoardManager.ROWS;
  static final int NUMBER_OF_COLS = BoardManager.COLUMNS;

  static final int WALL_THICKNESS = 5;
  static final int TILE_SIZE = 75;
  static Image[][] tilesImg = new Image[NUMBER_OF_ROWS][NUMBER_OF_COLS];
  static ArrayList<Image> gameUnits = new ArrayList<Image>();

  // choose init pos clickable tiles
  static final String[] CHOOSE_INIT_POS_TILES = {
    "0-0", "1-0", "2-0", "3-0", "4-0", "5-0", "6-0", "7-0", "0-1", "0-2", "0-3", "0-4", "0-5",
    "0-6", "0-7", "0-8", "0-9", "1-9", "2-9", "3-9", "4-9", "5-9", "6-9", "7-1", "7-2", "7-3",
    "7-4", "7-5", "7-6", "7-7", "7-8", "7-9",
  };

  // choose pos on knock down
  static boolean activateKnockDownChoosePos = false;
  static boolean activateFlipPOIChoosePos = false;
  static FireFighter knockedDownFirefigher;
  static ArrayList<Tile> clickableTilesOnKnockDownArr = new ArrayList<Tile>();

  // GUI elements
  SpriteBatch batch;
  Texture txtrBG;
  Sprite spriteBG;
  static Stage stage;

  private static Music audioMusic =
      Gdx.audio.newMusic(Gdx.files.internal("playlist/battle_normal01.mp3"));

  static BoardDialog boardDialog;

  static BoardChooseSpecialtyPanel boardChooseRolePanel;
  static BoardGameInfoLabel boardGameInfoLabel;
  static BoardMovesPanel boardMovesPanel;
  static BoardChatFragment boardChatFragment;
  static BoardCheatSFragment boardCheatSFragment;
  static BoardStatsFragment boardStatsFragment;

  static ImageButton btnExit;
  static ImageButton btnResume;
  ImageButton btnChat;
  ImageButton btnCheatS;
  ImageButton btnStats;

  static Fragment currentFragment = Fragment.EMPTY;

  BoardScreen(Game pGame) {
    super(pGame);
    audioMusic.setLooping(true);
  }

  @Override
  public void show() {

    // DBHandler.createBoard(MapKind.MAP2); // generate initial map

    audioMusic.play();

    stage = new Stage();
    batch = new SpriteBatch();

    txtrBG = new Texture("empty.png");
    spriteBG = new Sprite(txtrBG);
    spriteBG.setPosition(0, 0);

    boardDialog = new BoardDialog(stage);

    boardChooseRolePanel = new BoardChooseSpecialtyPanel(stage);
    boardGameInfoLabel = new BoardGameInfoLabel(stage);
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

                // some boolean checks for advanced version
                boolean isAdvancedVersion = BoardManager.getInstance().isAdvanced();
                boolean hasNoSpecialty =
                    isAdvancedVersion
                        && !(FireFighterTurnManagerAdvance.getInstance().currentHasSpeciality());
                boolean isAmbulanceNotSet =
                    isAdvancedVersion && !(BoardManagerAdvanced.getInstance()).hasAmbulancePlaced();
                boolean isEngineNotSet =
                    isAdvancedVersion && !(BoardManagerAdvanced.getInstance()).hasFireTruckPlaced();

                // set init vehicles position
                if (isAmbulanceNotSet
                    && DBHandler.isPresentInArr(
                        getAmbulanceClickableTiles(), i_pos + "-" + j_pos)) {

                  JSONObject obj = new JSONObject();
                  obj.put("i", i_pos);
                  obj.put("j", j_pos);
                  Client.getInstance()
                          .sendCommand(ServerCommands.SET_AMBULANCE, obj.toJSONString());

                } else if (isEngineNotSet
                    && DBHandler.isPresentInArr(getEngineClickableTiles(), i_pos + "-" + j_pos)) {

                  if (isAmbulanceNotSet) {
                    return;
                  }

                  JSONObject obj = new JSONObject();
                  obj.put("i", i_pos);
                  obj.put("j", j_pos);
                  Client.getInstance()
                          .sendCommand(ServerCommands.SET_FIRETRUCK, obj.toJSONString());
                }

                // choose init position
                if (!FireFighterTurnManager.getInstance().currentHasTile()
                    && DBHandler.isPresentInArr(CHOOSE_INIT_POS_TILES, i_pos + "-" + j_pos) && User.getInstance().isMyTurn()) { // TODO && User.getInstance().isMyTurn()

                  if (isAmbulanceNotSet || isEngineNotSet) {
                    return;
                  }

                  JSONObject obj = new JSONObject();
                  obj.put("i", i_pos);
                  obj.put("j", j_pos);
                  Client.getInstance()
                      .sendCommand(ServerCommands.CHOOSE_INITIAL_POSITION, obj.toJSONString());
                }

                // choose tile on knock down
                if (activateKnockDownChoosePos && isClickableTileOnKnockDown(i_pos, j_pos)) {
                  BoardManager.getInstance()
                      .chooseForKnockedDown(
                          BoardManager.getInstance().getTiles()[i_pos][j_pos],
                          knockedDownFirefigher);

                  activateKnockDownChoosePos = false;

                  removeAllFilterOnTile();
                  clearAllGameUnits();
                  drawGameUnitsOnTile();
                  boardGameInfoLabel.drawGameInfoLabel();
                }

                if (activateFlipPOIChoosePos && isClickableTileOnFlipPOI(i_pos, j_pos)) {
                  JSONObject obj = new JSONObject();
                  obj.put("i", i_pos);
                  obj.put("j", j_pos);
                  removeAllFilterOnTile();
                  Client.getInstance().sendCommand(Actions.FLIP_POI, obj.toJSONString());
                  activateKnockDownChoosePos = false;
                }
              }
            });

        stage.addActor(tilesImg[i][j]);
      }
    }

    // GUI elements creation (first time)
    createAllGameButtons();
    boardGameInfoLabel.drawGameInfoLabel();
    drawGameUnitsOnTile();

    boolean isAmbulanceNotSet =
        BoardManager.getInstance().isAdvanced()
            && !((BoardManagerAdvanced) BoardManagerAdvanced.getInstance()).hasAmbulancePlaced();
    boolean isEngineNotSet =
        BoardManager.getInstance().isAdvanced()
            && !((BoardManagerAdvanced) BoardManagerAdvanced.getInstance()).hasFireTruckPlaced();

    if ((isAmbulanceNotSet || isEngineNotSet) && User.getInstance().isMyTurn()) { // TODO && User.getInstance().isMyTurn()
      boardDialog.drawDialog(
          "Ambulance position", "Choose the ambulance's initial position (green tiles).");
      addFilterOnTileForAmbulance();
    } else if (!FireFighterTurnManager.getInstance().currentHasTile() && User.getInstance().isMyTurn()) { // TODO && User.getInstance().isMyTurn()
      boardDialog.drawDialog(
          "Initial position", "Choose your initial position on the board (green tiles).");
      addFilterOnTileForChooseInitPos();
      boardChooseRolePanel.drawChooseSpecialtyPanel();
    } else {

      redrawBoard();
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
    if (tiles[i][j].hasAmbulance()) {
      if ((i == 0 || i == 7) && (!tiles[i][j - 1].hasAmbulance())) { // horizontally positioned
        gameUnit = new Image(new Texture("game_units/vehicles/h_ambulance.png"));
        gameUnit.setHeight(60);
        gameUnit.setWidth(100);
        gameUnit.setPosition(myTile.getX() + 25, myTile.getY() + 8);
        gameUnits.add(gameUnit);
        stage.addActor(gameUnit);
      } else if ((j == 0 || j == 9) && (!tiles[i + 1][j].hasAmbulance())) { // vertically positioned
        gameUnit = new Image(new Texture("game_units/vehicles/v_ambulance.png"));
        gameUnit.setHeight(100);
        gameUnit.setWidth(60);
        gameUnit.setPosition(myTile.getX() + 8, myTile.getY() + 25);
        gameUnits.add(gameUnit);
        stage.addActor(gameUnit);
      }
    } else if (tiles[i][j].hasFireTruck()) {
      if ((i == 0 || i == 7) && (!tiles[i][j - 1].hasFireTruck())) { // horizontally positioned
        gameUnit = new Image(new Texture("game_units/vehicles/h_engine.png"));
        gameUnit.setHeight(60);
        gameUnit.setWidth(100);
        gameUnit.setPosition(myTile.getX() + 25, myTile.getY() + 8);
        gameUnits.add(gameUnit);
        stage.addActor(gameUnit);
      } else if ((j == 0 || j == 9) && (!tiles[i + 1][j].hasFireTruck())) { // vertically positioned
        gameUnit = new Image(new Texture("game_units/vehicles/v_engine.png"));
        gameUnit.setHeight(100);
        gameUnit.setWidth(60);
        gameUnit.setPosition(myTile.getX() + 8, myTile.getY() + 25);
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

      if (tiles[i][j].getVictim().isCured()) {
        gameUnit = new Image(new Texture("game_units/Heal_Marker.png"));
        gameUnit.setHeight(15);
        gameUnit.setWidth(15);
        gameUnit.setPosition(
            myTile.getX() + myTile.getHeight() / 2 + 10,
            myTile.getY() + myTile.getHeight() / 2 + 10);
        gameUnits.add(gameUnit);
        stage.addActor(gameUnit);
      }
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
    if (tiles[i][j].hasHazmat()) { // placed at bottom right corner of tile
      gameUnit = new Image(new Texture("game_units/Hazmat.png"));
      gameUnit.setHeight(30);
      gameUnit.setWidth(30);
      gameUnit.setPosition(myTile.getX() + 45, myTile.getY());

      gameUnits.add(gameUnit);
      stage.addActor(gameUnit);
    }

    // Hot spot
    if (tiles[i][j].hasHotSpot()) { // placed at bottom right corner of tile
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

  public static void drawEngineTilesColor() {
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

  // menu buttons

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
            audioMusic.stop();
            Client.getInstance()
                .sendCommand(ServerCommands.EXIT_GAME, User.getInstance().getName());
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
            boardCheatSFragment.drawCheatSFragment();
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
            boardStatsFragment.drawStatsFragment();
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
            Client.getInstance().sendCommand(ServerCommands.GET_CHAT_MESSAGES,"");
          }
        });

    stage.addActor(btnChat);
  }

  // show MovesAndDirectionsPanel on resume
  private static void createResumeButton() {

    Texture myTexture =
        new Texture(
            Gdx.files.internal("icons/resumeBtn_" + User.getInstance().getColor() + ".png"));

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
            if (User.getInstance().isMyTurn()) {
              redrawBoard();
            }
          }
        });

    stage.addActor(btnResume);
  }

  // add filters

  private static void addFilterOnTileForChooseInitPos() {
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
    }

    activateKnockDownChoosePos = true;
  }

  private static void addFilterOnTileForAmbulance() {
    Tile[][] tiles = BoardManager.getInstance().getTiles();
    for (int i = 0; i < tiles.length; i++) {
      for (int j = 0; j < tiles[i].length; j++) {
        if (tiles[i][j].canContainAmbulance()) {
          tilesImg[i][j].setColor(Color.GREEN);
        }
      }
    }
  }

  public static void addFilterOnTileForEngine() {
    Tile[][] tiles = BoardManager.getInstance().getTiles();
    for (int i = 0; i < tiles.length; i++) {
      for (int j = 0; j < tiles[i].length; j++) {
        if (tiles[i][j].canContainFireTruck()) {
          tilesImg[i][j].setColor(Color.GREEN);
        }
      }
    }
  }

  public static void addFilterOnChoosePOIChoosePos() {

    List<Tile> tiles = BoardManagerAdvanced.getInstance().tilesWithPOI();

    for (int i = 0; i < tiles.size(); i++) {
      tilesImg[tiles.get(i).getI()][tiles.get(i).getJ()].setColor(Color.GREEN);
    }

    activateFlipPOIChoosePos = true;
  }

  public static void addFilterOnFireDeckGun(int i, int j) {
    tilesImg[i][j].setColor(Color.NAVY);
  }


  // remove GUI elements

  public static void clearAllGameUnits() {
    for (int i = 0; i < gameUnits.size(); i++) {
      gameUnits.get(i).remove();
    }
    gameUnits.clear();
  }

  public static void removeAllPrevFragments() {
    boardMovesPanel.removeMovesAndDirectionsPanel();
    boardChooseRolePanel.removeChooseSpecialtyPanel();
    boardChatFragment.removeChatFragment();
    boardCheatSFragment.removeCheatSFragment();
    boardStatsFragment.removeStatsFragment();
  }

  private static void removeAllFilterOnTile() {
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

  private boolean isClickableTileOnFlipPOI(int i_input, int j_input) {

    List<Tile> tiles = BoardManagerAdvanced.getInstance().tilesWithPOI();

    for (int i = 0; i < tiles.size(); i++) {
      if (tiles.get(i).getI() == i_input && tiles.get(i).getJ() == j_input) {
        return true;
      }
    }

    return false;
  }

  private String[] getAmbulanceClickableTiles() {

    ArrayList<String> clickableTiles = new ArrayList<String>();

    Tile[][] tiles = BoardManager.getInstance().getTiles();
    for (int i = 0; i < NUMBER_OF_ROWS; i++) {
      for (int j = 0; j < NUMBER_OF_COLS; j++) {
        if (tiles[i][j].canContainAmbulance()) {
          clickableTiles.add(i + "-" + j);
        }
      }
    }

    return clickableTiles.toArray(new String[0]);
  }

  private String[] getEngineClickableTiles() {

    ArrayList<String> clickableTiles = new ArrayList<String>();

    Tile[][] tiles = BoardManager.getInstance().getTiles();
    for (int i = 0; i < NUMBER_OF_ROWS; i++) {
      for (int j = 0; j < NUMBER_OF_COLS; j++) {
        if (tiles[i][j].canContainFireTruck()) {
          clickableTiles.add(i + "-" + j);
        }
      }
    }

    return clickableTiles.toArray(new String[0]);
  }

//  public static void redrawBoard() {
//    if (game.getScreen() == game.boardScreen) {
//      setBoardScreen();
//    }
//  }

  public static boolean isOnBoardScreen() {
    return game.getScreen() == game.boardScreen;
  }

  public static void setLobbyPageOutOfGame(ArrayList<String> games) {
    audioMusic.stop();
    if (game.getScreen() == game.boardScreen) {
      LobbyScreen.setSavedGames(games);
      game.setScreen(game.lobbyScreen);
    }
  }

  public static void setLobbyPage() {
    audioMusic.stop();
      game.setScreen(game.lobbyScreen);
  }

  public static void setBoardScreen() {
    audioMusic.stop();
    game.setScreen(game.boardScreen);
  }

  public static void setSideFragment(Fragment fragment) {

    removeAllPrevFragments();

    if (fragment == Fragment.CHEATSHEET) {
      boardCheatSFragment.drawCheatSFragment();
    } else if (fragment == Fragment.STATS) {
      boardStatsFragment.drawStatsFragment();
    } else if (fragment == Fragment.CHAT) {
      boardChatFragment.drawChatFragment();
    } else if (fragment == Fragment.CHOOSESPECIALTY) {
      boardChooseRolePanel.drawChooseSpecialtyPanel();
    }

    currentFragment = fragment;
  }

  // Redraw for Network
  public static void redrawBoard() {
    if (game.getScreen() == game.boardScreen) {
      removeAllPrevFragments();
      clearAllGameUnits();
      removeAllFilterOnTile();

      drawEngineTilesColor();

      GameSetupActions gameSetupActions = GameSetupActions.NO_SETUP_ACTION;

      if (BoardManager.getInstance().isAdvanced()) { // experienced version
        if (!(BoardManagerAdvanced.getInstance()).hasAmbulancePlaced() && User.getInstance().isMyTurn()) { // TODO && User.getInstance().isMyTurn()
          boardDialog.drawDialog(
                  "Ambulance position", "Choose the ambulance's initial position (green tiles).");
          addFilterOnTileForAmbulance();

        } else if (!(BoardManagerAdvanced.getInstance()).hasFireTruckPlaced()) {
          gameSetupActions = GameSetupActions.PLACE_FIRETRUCK;
          addFilterOnTileForEngine();
        } else if(!FireFighterTurnManager.getInstance().currentHasTile()){
            if (User.getInstance().isMyTurn()) { // TODO : || true (if play with single computer multi-player)
              gameSetupActions = GameSetupActions.CHOOSE_INIT_POS;
              addFilterOnTileForChooseInitPos();
            }
        } else if (FireFighterTurnManagerAdvance.getInstance().getCurrentFireFighter().getSpeciality() == FireFighterAdvanceSpecialities.NO_SPECIALITY && User.getInstance().isMyTurn()) { // TODO && User.getInstance().isMyTurn()
          gameSetupActions = GameSetupActions.CHOOSE_INIT_SPECIALTY;
          boardChooseRolePanel.drawChooseSpecialtyPanel();
        } else {
          boardMovesPanel.drawMovesAndDirectionsPanel();
        }

      } else { // family version

        if (!FireFighterTurnManager.getInstance().currentHasTile()) { // choose init pos
          if (User.getInstance().isMyTurn()) { // TODO : || true (if play with single computer multi-player)
            addFilterOnTileForChooseInitPos();
            boardChooseRolePanel.drawChooseSpecialtyPanel();
          }
        } else {
          boardMovesPanel.drawMovesAndDirectionsPanel();
        }
      }

      drawGameUnitsOnTile();
      boardGameInfoLabel.drawGameInfoLabel();

      if(gameSetupActions != GameSetupActions.NO_SETUP_ACTION){
        showDialogForGameSetupAction(gameSetupActions);
      }
    }
  }

  private static void showDialogForGameSetupAction(GameSetupActions gameSetupActions){
    switch (gameSetupActions) {
      case PLACE_FIRETRUCK:
        boardDialog.drawDialog(
                "Engine position",
                "Choose the fire engine's initial position (green tiles).");
        break;
      case CHOOSE_INIT_POS:
        boardDialog.drawDialog(
                "Initial position", "Choose your initial position on the board.");
        break;
      case CHOOSE_INIT_SPECIALTY:
        boardDialog.drawDialog(
                "Specialty", "Choose your initial specialty on the right panel.");
        break;
      default:
    }
  }

  public static void setLobbyString() {
    game.setScreen(game.lobbyScreen);
  }
  public static void refreshLobbyScreen() {
    if (game.getScreen() == game.lobbyScreen)
    {
      Client.getInstance().sendCommand(ServerCommands.GET_SAVED_GAMES,"");
    }
  }
  public static void setCurrentFragment(Fragment currFrag){
    currentFragment = currFrag;
  }

  public static boolean isChatFragment() {
    return currentFragment == Fragment.CHAT;
  }

  public static boolean isCallForActionFragment() {
    return currentFragment == Fragment.CALL_FOR_ACTION;
  }

  public static BoardDialog getDialog() {
    return boardDialog;
  }

  public static void displayMessage(String title, String message) {
    if (game.getScreen() == game.boardScreen) {
      boardDialog.drawDialog(title, message);
    } else if (game.getScreen() == game.lobbyScreen){
      LobbyScreen.getDialog().drawDialog(title, message);
    }
  }
}
