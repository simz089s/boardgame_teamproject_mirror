package com.cs361d.flashpoint.model;

import com.cs361d.flashpoint.model.BoardElements.Tile;

class Board {
  private static final int height = 10;
  private static final int width = 8;
  private Tile[][] tileMap = new Tile[height][width];

  // create an object of SingleObject
  private static Board instance = new Board();

  // make the constructor private so that this class cannot be instantiated
  private Board() {
    // TODO
  }

  // Get the only object available
  public static Board getInstance() {
    return instance;
  }
}
