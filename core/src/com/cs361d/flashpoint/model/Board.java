package com.cs361d.flashpoint.model;

import com.cs361d.flashpoint.model.BoardElements.*;
import com.cs361d.flashpoint.model.FireFighterRoles.*;

import java.util.ArrayList;

public class Board {
  public static final int WIDTH = 10;
  public static final int HEIGHT = 8;
  private final Tile[][] TILE_MAP = new Tile[HEIGHT][WIDTH];
  private int numberofWallDestroyed = 0;
  private ArrayList<FireFighterColor> COLOR = new ArrayList<FireFighterColor>();

  // create an object of SingleObject
  private static Board instance = new Board();

  // Get the only object available
  public static Board getInstance() {
    return instance;
  }

  // make the constructor private so that this class cannot be instantiated
  private Board() {
    for (int i = 0; i < HEIGHT; i++) {
      for (int j = 0; j < WIDTH; j++) {
        Tile newTile = new Tile(FireStatus.EMPTY, false);
        TILE_MAP[i][j] = newTile;
        Obstacle top = new Obstacle(1);
        Obstacle left = new Obstacle(-1);
        newTile.addObstacle(Direction.TOP, top);
        newTile.addObstacle(Direction.LEFT, left);

        /*
        Add wall to the bottom and right of adjacent tiles
         */
        if (i != 0) {
          TILE_MAP[i - 1][j].addObstacle(Direction.BOTTOM, top);
        }
        if (j != 0) {
          TILE_MAP[i][j - 1].addObstacle(Direction.RIGHT, left);
        }
        /*
         Add wall to the right of the final tile or the bottom of the final tiles
        */
        if (j == WIDTH - 1) {
          Obstacle right = new Obstacle(-1);
          newTile.addObstacle(Direction.RIGHT, right);
        }
        if (i == HEIGHT - 1) {
          Obstacle bottom = new Obstacle(-1);
          newTile.addObstacle(Direction.BOTTOM, bottom);
        }
      }
    }
  }

  public void addWall(int i, int j, Direction d, int health) {
    /*
      if the health is less than 1 then no need to update the walls
     */
    if (health == -1) {
      return ;
    }
    else {
      TILE_MAP[i][j].getObstacle(d).setHealth(health);
    }
  }

  public void addDoor(int i, int j, Direction d, int health, boolean isOpen) {
    Obstacle obstacle = TILE_MAP[i][j].getObstacle(d);
    /*
    set the boolean isDoor to true
     */
    obstacle.makeDoor();
    obstacle.setHealth(health);
    obstacle.setOpen(isOpen);
  }

  public void addFireFighter(int i, int j, Card pCard, FireFighterColor color, int numVictimsSaved, int actionPoints) {
    /*
    If color is already assigned throws an error
     */
    if (COLOR.contains(color)) {
      throw new IllegalArgumentException("The fireFighter with color " + color + " Already exists on the board");
    }
    COLOR.add(color);
    FireFighter f = new FireFighter(pCard, color, numVictimsSaved, actionPoints);
    TILE_MAP[i][j].addFirefighter(f);
  }

  public void addFireStatus(int i, int j, FireStatus f) {
    TILE_MAP[i][j].setFireStatus(f);
  }

  public void addVictim(int i, int j, boolean isRevealed, boolean isCured, boolean isFalseAlarm) {
    AbstractVictim victim = new Victim(isRevealed, isCured, isFalseAlarm);
    TILE_MAP[i][j].setVictim(victim);
  }

  public Tile[][] getTiles() {
    return TILE_MAP;
  }
}