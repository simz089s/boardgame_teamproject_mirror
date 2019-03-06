package com.cs361d.flashpoint.manager;

import com.cs361d.flashpoint.model.BoardElements.FireStatus;
import com.cs361d.flashpoint.model.BoardElements.Tile;

public class CreateNewGameManager {

  public static void createNewGame(String gameName, int numPlayers, MapKind map, Difficulty diff) {
    switch (diff) {
      case FAMILLY:
        createFamilyGame(map);
        break;
      default:
        creatExperienceGame(map);
        ;
    }
    BoardManager.getInstance().setGameName(gameName);
    BoardManager.getInstance().setFireFighterNumber(numPlayers);
  }

  private static void createFamilyGame(MapKind map) {
    BoardManager.getInstance().reset();
    switch (map) {
      case MAP1:
        DBHandler.loadBoardFromDB("map1");
        break;
      default:
    }
    populateFamillyMap();
  }

  public static void loadSavedGame(String name) {
    DBHandler.loadBoardFromDB(name);
  }

  private static void creatExperienceGame(MapKind map) {}

  private static void populateFamillyMap() {
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

  private static void populateExperiencedMap(Difficulty diff) {
    BoardManagerAdvanced mg = (BoardManagerAdvanced) BoardManager.getInstance();
    Tile[][] tiles = mg.getTiles();
    int width = 3 + (int) (Math.random() * (1));
    int height = 3 + (int) (Math.random() * (3));
    mg.explosion(width, height);
    mg.addHotspot(width, height);
    do {
      width = 1 + (int) (Math.random() * (BoardManager.HEIGHT - 3));
      height = 1 + (int) (Math.random() * (BoardManager.WIDTH - 3));
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
          width = 1 + (int) (Math.random() * (BoardManager.HEIGHT - 3));
      } while (tiles[width][height].hasFire());
      mg.explosion(width, height);
      mg.addHotspot(width, height);

  }
}
