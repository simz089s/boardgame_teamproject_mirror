package com.cs361d.flashpoint.controller;

import com.cs361d.flashpoint.model.Board;
import com.cs361d.flashpoint.model.BoardElements.*;

import java.util.LinkedList;

public class FireFighterTurnController {
  private final LinkedList<FireFighter> FIREFIGHTERS = new LinkedList<FireFighter>();
  private static FireFighterTurnController instance =
      new FireFighterTurnController();

  public static FireFighterTurnController getInstance() {
    return instance;
  }

  public void addFireFighter(FireFighter f) {
    if (FIREFIGHTERS.contains(f)) {
      throw new IllegalArgumentException();
    }
    FIREFIGHTERS.add(f);
    if (FIREFIGHTERS.size() > 6) {
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
    Board.getInstance().endTurnFireSpread(0,0);
  }

  public void move(Direction d) {
    if (canMove(d) && getCurrentFireFighter().moveAP(d)) {
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
    if (!currentTile.containsVictim()
        || adjacentTile.hasFire()
        || adjacentTile.containsPointOfInterest()) {
      return false;
    }
    return true;
  }

  public FireFighter getCurrentFireFighter() {
    return FIREFIGHTERS.peek();
  }

  public void reset() {
    instance = new FireFighterTurnController();
  }
}
