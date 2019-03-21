package com.cs361d.flashpoint.manager;

import com.cs361d.flashpoint.model.BoardElements.FireStatus;
import com.cs361d.flashpoint.model.BoardElements.Tile;

public class CreateNewGameManager {

  public static void createNewGame(String gameName, int numPlayers, MapKind map, Difficulty diff) {
    BoardManager.reset();
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
    BoardManager.reset();
    DBHandler.loadBoardFromDB(name);
  }

  private static void createExperienceGame(MapKind map, Difficulty diff, int numPlayers) {
    BoardManager.useExperienceGameManager();
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
    BoardManager.reset();
    DBHandler.loadBoardFromString(game);
  }

  private static void populateExperiencedMap(Difficulty diff, int numPlayers) {
    BoardManagerAdvanced mg = (BoardManagerAdvanced) BoardManager.getInstance();
    int blackDie = 1 + (int) (Math.random() * 7);
    int column;
    int row;
    if (blackDie < 5) {
      row = 3;
      switch (blackDie) {
        case 1:
          column = 3;
          break;
        case 2:
          column = 4;
          break;
        case 3:
          column = 5;
          break;
        case 4:
          column = 6;
          break;
        default:
          throw new IllegalArgumentException("Should never reach there");
      }
    } else {
      row = 4;
      switch (blackDie) {
        case 5:
          column = 6;
          break;
        case 6:
          column = 5;
          break;
        case 7:
          column = 4;
          break;
        case 8:
          column = 3;
          break;
        default:
          throw new IllegalArgumentException("Should never reach there");
      }
    }
    generateExplosion(row, column);
    do {
      column = 1 + (int) (Math.random() * (BoardManager.COLUMNS - 2));
      row = 1 + (int) (Math.random() * (BoardManager.ROWS - 2));
    } while (generateExplosion(row, column));
    column = 9-column;
    do {
      row = 1 + (int) (Math.random() * (BoardManager.ROWS - 2));
    } while (generateExplosion(row,column));
    int numExtraHazmat = 0;
    int numExtraHotSpot = 0;
    switch (diff) {
      case HEROIC:
        do {
          column = 1 + (int) (Math.random() * (BoardManager.COLUMNS - 2));
          row = 1 + (int) (Math.random() * (BoardManager.ROWS - 2));
        } while (generateExplosion(row, column));
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
        } else {
          numExtraHotSpot = 2;
        }
        mg.setNumHotSpotLeft(6);
        break;

      default:
        throw new IllegalArgumentException(
            "Cannot set an experience game with familly version selected");
    }
    while (numExtraHazmat > 0) {
      do {
        column = 1 + (int) (Math.random() * (BoardManager.COLUMNS - 2));
        row = 1 + (int) (Math.random() * (BoardManager.ROWS - 2));
      } while (mg.getTiles()[row][column].hasFire() && !mg.getTiles()[row][column].hasHazmat());
      mg.addHazmat(row, column);
      numExtraHazmat--;
    }
    while (numExtraHotSpot > 0) {
      do {
      column = 1 + (int) (Math.random() * (BoardManager.COLUMNS - 2));
      row = 1 + (int) (Math.random() * (BoardManager.ROWS - 2));
      } while (!mg.getTiles()[row][column].hasHotSpot());
      mg.addHotspot(row, column);
      numExtraHotSpot--;
    }
    mg.removeEdgeFire();
    for (int i = 0; i < 3; i++) {
      mg.addNewPointInterest();
    }
  }

  private static boolean generateExplosion(int row, int column) {
    BoardManagerAdvanced mg = (BoardManagerAdvanced) BoardManager.getInstance();
    if (mg.getTiles()[row][column].hasFire()) {
      return false;
    }
    mg.explosion(row, column);
    mg.addHotspot(row, column);
    return true;
  }
}
