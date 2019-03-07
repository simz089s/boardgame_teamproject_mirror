package com.cs361d.flashpoint.manager;

import com.cs361d.flashpoint.model.BoardElements.FireStatus;
import com.cs361d.flashpoint.model.BoardElements.Tile;

public class CreateNewGameManager {

  public static void createNewGame(String gameName, int numPlayers, MapKind map, Difficulty diff) {
    BoardManager.getInstance().reset();
    switch (diff) {
      case FAMILLY:
        createFamilyGame(map);
        break;
      default:
        createExperienceGame(map, diff, numPlayers);
    }
    BoardManager.getInstance().setGameName(gameName);
    BoardManager.getInstance().setFireFighterNumber(numPlayers);
  }

  private static void createFamilyGame(MapKind map) {
    BoardManager.useFamilyGameManager();
    FireFighterTurnManager.useFireFighterGameManagerFamily();
    loadMap(map);
    populateFamilyMap();
  }

  private static void loadMap(MapKind map) {
    switch (map) {
      case MAP1:
        DBHandler.loadBoardFromDB("map1");
        break;
      case MAP2:
        DBHandler.loadBoardFromDB("map2");
        break;
      default:
        }
  }
  public static void loadSavedGame(String name) {
    BoardManager.getInstance().reset();
    DBHandler.loadBoardFromDB(name);
  }

  private static void createExperienceGame(MapKind map, Difficulty diff, int numPlayers) {
    BoardManager.useExperienceGameManager();
    FireFighterTurnManager.useFireFighterGameManagerAdvanced();
    loadMap(map);
    populateExperiencedMap(diff, numPlayers);
  }

  private static void populateFamilyMap() {
    BoardManager bm = BoardManager.getInstance();
    bm.addNewPointInterest(2, 4);
    bm.addNewPointInterest(5, 1);
    bm.addNewPointInterest(5, 8);
    bm.addFireStatus(2, 2, FireStatus.FIRE);
    bm.addFireStatus(2, 3, FireStatus.FIRE);
    bm.addFireStatus(3, 2, FireStatus.FIRE);
    bm.addFireStatus(3, 3, FireStatus.FIRE);
    bm.addFireStatus(3, 4, FireStatus.FIRE);
    bm.addFireStatus(3, 5, FireStatus.FIRE);
    bm.addFireStatus(4, 4, FireStatus.FIRE);
    bm.addFireStatus(5, 6, FireStatus.FIRE);
    bm.addFireStatus(5, 7, FireStatus.FIRE);
    bm.addFireStatus(6, 6, FireStatus.FIRE);
  }

  public static void loadGameFromString(String game) {
    BoardManager.getInstance().reset();
    DBHandler.loadBoardFromString(game);
  }
  private static void populateExperiencedMap(Difficulty diff, int numPlayers) {
    BoardManagerAdvanced mg = (BoardManagerAdvanced) BoardManager.getInstance();
    Tile[][] tiles = mg.getTiles();
    int width = 3 + (int) (Math.random() * (1));
    int height = 3 + (int) (Math.random() * (3));
    mg.explosion(width, height);
    mg.addHotspot(width, height);
    do {
      width = 1 + (int) (Math.random() * (BoardManager.COLUMNS - 2));
      height = 1 + (int) (Math.random() * (BoardManager.ROWS - 2));
    } while (tiles[width][height].hasFire());
    mg.explosion(width, height);
    mg.addHotspot(width, height);
    switch (height) {
      case 7:
        height = 2;
        break;
      case 6:
        height = 3;
        break;
      case 5:
        height = 4;
        break;
      case 4:
        height = 5;
        break;
      case 3:
        height = 6;
        break;
      case 2:
        height = 7;
        break;
      case 1:
        height = 8;
        break;
      default:
        throw new IllegalArgumentException();
    }
    do {
      width = 1 + (int) (Math.random() * (BoardManager.COLUMNS - 2));
    } while (tiles[width][height].hasFire());
    mg.explosion(width, height);
    mg.addHotspot(width, height);
    int numExtraHazmat = 0;
    int numExtraHotSpot = 0;
    switch (diff) {
      case HEROIC:
        do {
        width = 1 + (int) (Math.random() * (BoardManager.COLUMNS - 2));
        height = 1 + (int) (Math.random() * (BoardManager.ROWS - 2));
        } while (tiles[width][height].hasFire());
        mg.explosion(width, height);
        mg.addHotspot(width, height);
        numExtraHazmat = 5;
        numExtraHotSpot = 3;
        mg.setNumHotSpotLeft(12);
        break;
      case VETERAN:
        numExtraHazmat = 4;
        numExtraHotSpot = 3;
        mg.setNumHotSpotLeft(6);
        break;
      case RECRUIT:
        numExtraHazmat = 3;
        if (numPlayers > 3) {
          numExtraHotSpot = 3;
        }
        else {
          numExtraHotSpot = 2;
        }
        mg.setNumHotSpotLeft(6);
        break;

        default:
          throw new IllegalArgumentException("Cannot set an experience game with familly version selected");
    }
    while (numExtraHazmat > 0) {
      do {
        width = 1 + (int) (Math.random() * (BoardManager.COLUMNS - 2));
        height = 1 + (int) (Math.random() * (BoardManager.ROWS - 2));
      } while (tiles[width][height].hasFire());
      mg.addHazmat(width, height);
      numExtraHazmat--;
    }
    while (numExtraHotSpot > 0) {
      width = 1 + (int) (Math.random() * (BoardManager.COLUMNS - 2));
      height = 1 + (int) (Math.random() * (BoardManager.ROWS - 2));
      mg.addHotspot(width, height);
    }

  }
}
