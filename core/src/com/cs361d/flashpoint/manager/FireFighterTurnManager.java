package com.cs361d.flashpoint.manager;

import com.cs361d.flashpoint.model.BoardElements.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FireFighterTurnManager {
  private final int MAX_NUMBER_OF_PLAYERS = 6;

  private final LinkedList<FireFighter> FIREFIGHTERS = new LinkedList<FireFighter>();
  private static FireFighterTurnManager instance =
      new FireFighterTurnManager();

  public static FireFighterTurnManager getInstance() {
    return instance;
  }

  public void addFireFighter(FireFighter f) {
    if (FIREFIGHTERS.contains(f)) {
      throw new IllegalArgumentException();
    }
    FIREFIGHTERS.add(f);
    if (FIREFIGHTERS.size() > MAX_NUMBER_OF_PLAYERS) {
      throw new IllegalStateException();
    }
  }

  public void removeFireFighter(FireFighter f) {
    FIREFIGHTERS.remove(f);
  }

  public void endTurn() {
    FireFighter last = FIREFIGHTERS.removeFirst();
    last.resetActionPoints();
    FIREFIGHTERS.addLast(last);
    BoardManager.getInstance().endTurnFireSpread();
  }

  public void move(Direction d) {
    if (canMove(d) && getCurrentFireFighter().moveAP(d)) {
      FireFighter f = getCurrentFireFighter();
      Tile oldTile = f.getTile();
      Tile newTile = oldTile.getAdjacentTile(d);
      oldTile.removeFirefighter(f);
      newTile.addFirefighter(f);
      f.setTile(newTile);
      if (newTile.hasPointOfInterest()) {
        if (newTile.hasRealVictim()) {
          newTile.getVictim().reveal();
        } else {
          newTile.setNullVictim();
          // TODO
          // Method to add new victim
        }
      }
    }
  }

  public void moveWithVictim(Direction d) {
    if (canMoveWithVictim(d) && getCurrentFireFighter().moveWithVictimAP()) {
      FireFighter f = getCurrentFireFighter();
      AbstractVictim v = f.getTile().getVictim();
      Tile oldTile = f.getTile();
      Tile newTile = oldTile.getAdjacentTile(d);
      oldTile.removeFirefighter(f);
      newTile.addFirefighter(f);
      oldTile.setNullVictim();
      newTile.setVictim(v);
      f.setTile(newTile);
      BoardManager.getInstance().verifyVictimRescueStatus(newTile);
    }
  }

  public void chopWall(Direction d) {
    Obstacle o = getCurrentFireFighter().getTile().getObstacle(d);
    if (o.isDoor()) {
      return;
    }
    if (o.isDestroyed()) {
      return;
    }
    if (getCurrentFireFighter().chopAP()) {
      o.applyDamage();
    }
  }

  public void interactWithDoor(Direction d) {
    Obstacle o = getCurrentFireFighter().getTile().getObstacle(d);
    if (!o.isDoor()) {
      return;
    }
    if (o.isDestroyed()) {
      return;
    }
    if (getCurrentFireFighter().openCloseDoorAP()) {
      o.interactWithDoor();
    }
  }

  public void extinguishFire(Direction d) {
    Tile tileToExtinguish = getCurrentFireFighter().getTile().getAdjacentTile(d);
    if (tileToExtinguish == null) {
      return;
    } else if (tileToExtinguish.hasNoFireAndNoSmoke() || getCurrentFireFighter().getTile().hasObstacle(d)) {
      return;
    }
    if (getCurrentFireFighter().extinguishAP()) {
      if (tileToExtinguish.hasFire()) {
        tileToExtinguish.setFireStatus(FireStatus.SMOKE);
      } else if (tileToExtinguish.hasSmoke()) {
        tileToExtinguish.setFireStatus(FireStatus.EMPTY);
      }
    }
  }

  public boolean isFireFighterTurn(FireFighter f) {
    return f == FIREFIGHTERS.peek();
  }

  private boolean canMove(Direction d) {
    Tile currentTile = getCurrentFireFighter().getTile();
    // verifies that there is a tile in the direction
    if (currentTile.hasObstacle(d) || currentTile.getAdjacentTile(d) == null) {
      return false;
    }

    return true;
  }

  private boolean canMoveWithVictim(Direction d) {
    Tile currentTile = getCurrentFireFighter().getTile();
    Tile adjacentTile = currentTile.getAdjacentTile(d);
    if (!canMove(d)) {
      return false;
    }
    if (!currentTile.hasRealVictim()
        || adjacentTile.hasFire()
        || adjacentTile.hasPointOfInterest()) {
      return false;
    }
    return true;
  }

  public FireFighter getCurrentFireFighter() {
    return FIREFIGHTERS.peek();
  }

  public void reset() {
    instance = new FireFighterTurnManager();
  }

  public void placeInitialFireFighter(FireFighter f, Tile t) {
    int i = t.getI();
    int j = t.getJ();
    if (i > 0 || i > BoardManager.HEIGHT-2 || j > 0 || j > BoardManager.WIDTH-2) {
      return;
    }
    // if the fireFighter already had a tile that means it was already one the board so we cannot place it initially.
    if (f.getTile() != null) {
      return;
    }
    addFireFighter(f);
    f.setTile(t);
    t.addFirefighter(f);
  }

  /*
  TODO
   */
  public void knockedDownReapearChoice(FireFighter f, List<Tile> tiles) {}
}
