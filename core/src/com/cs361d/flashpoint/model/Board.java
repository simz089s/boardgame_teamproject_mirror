package com.cs361d.flashpoint.model;

import com.cs361d.flashpoint.model.BoardElements.*;
import com.cs361d.flashpoint.model.FireFighterRoles.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Board {
  public static final int WIDTH = 10;
  public static final int HEIGHT = 8;
  private final Tile[][] TILE_MAP = new Tile[HEIGHT][WIDTH];
  private int numberofWallDestroyed = 0;
  private int numVictimSaved = 0;
  private int numVictimDead = 0;

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
        Obstacle top = new Obstacle(-1);
        Obstacle left = new Obstacle(1);
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
          Obstacle right = new Obstacle(1);
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
      return;
    } else {
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

  public void addFireFighter(
      int i, int j, Card pCard, FireFighterColor color, int numVictimsSaved, int actionPoints) {
    /*
    If color is already assigned throws an error
     */
    if (COLOR.contains(color)) {
      throw new IllegalArgumentException(
          "The fireFighter with color " + color + " Already exists on the board");
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

  // Spread the fire at the end of the turn
  public void endTurnFireSpread(int i, int j) {
    Tile hitLocation = TILE_MAP[i][j];
    if (hitLocation.hasNoFireAndNoSmoke()) {
      hitLocation.setFireStatus(FireStatus.SMOKE);
    } else if (hitLocation.hasSmoke()) {
      hitLocation.setFireStatus(FireStatus.FIRE);
    } else {
      explosion(i, j);
    }
    updateSmoke();
    updateVictim();
    /*
    updatePlayer();
    must be done to ensure on player is in fire at the end of the spread of the fire
     */

    removeEdgeFire();
  }

  // Spread the fire in a specific direction after an explosion
  private void explosionFireSpread(int i, int j, Direction d) {
    Tile hitLocation = TILE_MAP[i][j];
    if (hitLocation.hasNoFireAndNoSmoke() || hitLocation.hasSmoke()) {
      hitLocation.setFireStatus(FireStatus.FIRE);
    } else {
      Obstacle obs = hitLocation.getObstacle(d);
      if (obs.isDestroyed() || (obs.isDoor() && obs.isOpen())) {
        switch (d) {
          case TOP:
            if (i < 1) {
              return;
            }
            explosionFireSpread(i - 1, j, d);
            break;
          case BOTTOM:
            if (i > HEIGHT - 2) {
              return;
            }
            explosionFireSpread(i + 1, j, d);
            break;
          case LEFT:
            if (j < 1) {
              return;
            }
            explosionFireSpread(i, j - 1, d);
            break;
          case RIGHT:
            if (j > WIDTH - 2) {
              return;
            }
            explosionFireSpread(i, j + 1, d);
            break;
          default:
            throw new IllegalArgumentException("The Direction " + d + " does not exit.");
        }
      } else {
        obs.applyDamage();
      }
    }
  }

  // Create an explosion at the specified location
  private void explosion(int i, int j) {
    Tile hitLocation = TILE_MAP[i][j];
    if (i > 0) {
      Obstacle top = hitLocation.getObstacle(Direction.TOP);
      if (top.isDestroyed() || (top.isDoor() && top.isOpen())) {
        explosionFireSpread(i - 1, j, Direction.TOP);
      } else {
        top.applyDamage();
      }
    }
    if (i < HEIGHT - 1) {
      Obstacle bottom = hitLocation.getObstacle(Direction.BOTTOM);
      if (bottom.isDestroyed() || (bottom.isDoor() && bottom.isOpen())) {
        explosionFireSpread(i + 1, j, Direction.BOTTOM);
      } else {
        bottom.applyDamage();
      }
    }
    if (j > 0) {
      Obstacle left = hitLocation.getObstacle(Direction.LEFT);
      if (left.isDestroyed() || (left.isDoor() && left.isOpen())) {
        explosionFireSpread(i, j - 1, Direction.LEFT);
      } else {
        left.applyDamage();
      }
    }
    if (j < WIDTH - 1) {
      Obstacle right = hitLocation.getObstacle(Direction.RIGHT);
      if (right.isDestroyed() || (right.isDoor() && right.isOpen())) {
        explosionFireSpread(i, j + 1, Direction.RIGHT);
      } else {
        right.applyDamage();
      }
    }
  }

  // Ensure that all the tiles with smoke catch fire at the end of turn
  private void updateSmoke() {
    boolean repeat = false;
    for (int i = 0; i < HEIGHT; i++) {
      for (int j = 0; j < WIDTH; j++) {
        if (TILE_MAP[i][j].hasSmoke() && hasFireNextToTile(i, j)) {
          TILE_MAP[i][j].setFireStatus(FireStatus.FIRE);
          repeat = true;
        }
      }
    }
    // Not optimal but we repeat the method until no more changes are seen
    if (repeat) {
      updateSmoke();
    }
  }

  private boolean hasFireNextToTile(int i, int j) {
    if (i > 0 && TILE_MAP[i - 1][j].hasFire()) {
      Obstacle obs = TILE_MAP[i][j].getObstacle(Direction.TOP);
      if (obs.isDestroyed() || (obs.isDoor() && obs.isOpen())) {
        return true;
      }
    }

    if (i < HEIGHT - 1 && TILE_MAP[i + 1][j].hasFire()) {
      Obstacle obs = TILE_MAP[i][j].getObstacle(Direction.BOTTOM);
      if (obs.isDestroyed() || (obs.isDoor() && obs.isOpen())) {
        return true;
      }
    }

    if (j > 0 && TILE_MAP[i][j - 1].hasFire()) {
      Obstacle obs = TILE_MAP[i][j].getObstacle(Direction.LEFT);
      if (obs.isDestroyed() || (obs.isDoor() && obs.isOpen())) {
        return true;
      }
    }
    if (i < HEIGHT - 1 && TILE_MAP[i][j + 1].hasFire()) {
      Obstacle obs = TILE_MAP[i][j].getObstacle(Direction.RIGHT);
      if (obs.isDestroyed() || (obs.isDoor() && obs.isOpen())) {
        return true;
      }
    }
    return false;
  }

  // removes victims that are in a place with fire at the end of turn
  private void updateVictim() {
    for (Tile[] rows : TILE_MAP) {
      for (Tile t : rows) {
        if (t.hasFire() && t.containsPointOfInterest()) {
          numVictimDead++;
          /*
          Must add code to replace the victim hear
           */
        }
      }
    }
  }

  // removes the fire outside the building
  private void removeEdgeFire() {
    for (int i = 0; i < HEIGHT; i++) {
      TILE_MAP[i][0].setFireStatus(FireStatus.EMPTY);
      TILE_MAP[i][WIDTH-1].setFireStatus(FireStatus.EMPTY);
    }
    for (int j = 0; j < WIDTH; j++) {
      TILE_MAP[0][j].setFireStatus(FireStatus.EMPTY);
      TILE_MAP[HEIGHT-1][j].setFireStatus(FireStatus.EMPTY);

    }
  }
}
