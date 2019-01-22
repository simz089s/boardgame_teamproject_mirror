package com.cs361d.flashpoint.controller;

import com.cs361d.flashpoint.model.Board;
import com.cs361d.flashpoint.model.BoardElements.*;

import java.util.LinkedList;

public class FireFighterTurnController {
  private final LinkedList<FireFighter> FIREFIGHTERS = new LinkedList<FireFighter>();
  private static FireFighterTurnController fireFighterTurnController =
      new FireFighterTurnController();

  public static FireFighterTurnController getInstance() {
    return fireFighterTurnController;
  }

  public void addFireFighter(FireFighter f) {
    FIREFIGHTERS.add(f);
    if (FIREFIGHTERS.size() > 6) {
        throw new IllegalStateException();
    }
  }

  public void removeFireFighter(FireFighter f) {
    FIREFIGHTERS.remove(f);
  }

  public void nextTurn() {
    FireFighter last = FIREFIGHTERS.remove();
    last.resetActionPoints();
    FIREFIGHTERS.addLast(last);
  }

  public void move(Direction d) {
    // we will add an ap function here
    if (canMove(d)) {
      FireFighter f = getCurrentFireFighter();
      Tile oldTile = f.getTile();
      Tile newTile = oldTile.getAdjacentTile(d);
      oldTile.removeFirefighter(f);
      newTile.addFirefighter(f);
      f.setTile(newTile);

      if (newTile.containsPointOfInterest()) {
        if (newTile.containsVictim()) {
          newTile.getVictim().reveal();
        } else {
          newTile.setNullVictim();
          // Method to add new victim
        }
      }
    }
  }

  public void moveWithVictim(Direction d) {
    if (canMoveWithVictim(d)) {
      FireFighter f = getCurrentFireFighter();
      AbstractVictim v = f.getTile().getVictim();
      Tile oldTile = f.getTile();
      Tile newTile = oldTile.getAdjacentTile(d);
      oldTile.removeFirefighter(f);
      newTile.addFirefighter(f);
      oldTile.setNullVictim();
      newTile.setVictim(v);
      f.setTile(newTile);
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
    Tile currentTile = getCurrentFireFighter().getTile();
    if (d == null) {
      if (currentTile.hasNoFireAndNoSmoke()) {
        return;
      }
    }
    Tile adjacentTile = currentTile.getAdjacentTile(d);
    if (adjacentTile == null) {
      return;
    } else if (adjacentTile.hasNoFireAndNoSmoke()) {
      return;
    }
    if (getCurrentFireFighter().extinguishAP()) {
      if (adjacentTile.hasFire()) {
        adjacentTile.setFireStatus(FireStatus.SMOKE);
      } else if (adjacentTile.hasSmoke()) {
        adjacentTile.setFireStatus(FireStatus.EMPTY);
      }
    }
  }

  public boolean isFireFighterTurn(FireFighter f) {
    return f == FIREFIGHTERS.peek();
  }

  private boolean canMove(Direction d) {
    Tile currentTile = getCurrentFireFighter().getTile();
    if (currentTile.hasObstacle(d) || currentTile.getAdjacentTile(d) == null) {
      return false;
    }

    // verifies that there is a tile in the direction
    return getCurrentFireFighter().moveAP(d);
  }

  private boolean canMoveWithVictim(Direction d) {
    Tile currentTile = getCurrentFireFighter().getTile();
    Tile adjacentTile = currentTile.getAdjacentTile(d);
    if (currentTile.hasObstacle(d) || adjacentTile == null) {
      return false;
    }
    if (!currentTile.containsVictim()
        || adjacentTile.hasFire()
        || adjacentTile.containsPointOfInterest()) {
      return false;
    }
    return getCurrentFireFighter().moveWithVictimAP();
  }

  public FireFighter getCurrentFireFighter() {
    return FIREFIGHTERS.peek();
  }
}
