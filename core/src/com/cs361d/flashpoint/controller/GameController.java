package com.cs361d.flashpoint.controller;

import com.cs361d.flashpoint.model.BoardElements.FireFighter;
import com.cs361d.flashpoint.model.BoardElements.Tile;

import java.util.ArrayList;

public class GameController {

  private static FireFighter removeAndGetFireFighter(Tile[][] tiles, int i, int j) {
    FireFighter f = tiles[i][j].getFirefighters().get(0);
    tiles[i][j].removeFirefighter(f);
    return f;
  }

  // i = row, j = col
  public static boolean extinguishFireToTile(Tile[][] tiles, int i, int j) {
    if (tiles[i][j].hasFire()) {
      return true;
    }
    return false;
  }

  public static boolean moveUp(Tile[][] tiles, int i, int j) {
    tiles[i - 1][j].addFirefighter(removeAndGetFireFighter(tiles, i, j));
    return true;
  }

  public static boolean moveDown(Tile[][] tiles, int i, int j) {
    tiles[i + 1][j].addFirefighter(removeAndGetFireFighter(tiles, i, j));
    return true;
  }

  public static boolean moveLeft(Tile[][] tiles, int i, int j) {
    tiles[i][j - 1].addFirefighter(removeAndGetFireFighter(tiles, i, j));
    return true;
  }

  public static boolean moveRight(Tile[][] tiles, int i, int j) {
    tiles[i][j + 1].addFirefighter(removeAndGetFireFighter(tiles, i, j));
    return true;
  }
}
