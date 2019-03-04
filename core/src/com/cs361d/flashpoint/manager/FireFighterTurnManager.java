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

  public boolean chooseInitialPosition(Tile t) throws IllegalAccessException {
    FireFighter f = FIREFIGHTERS.removeFirst();
    if (f.getTile() != null) {
        throw new IllegalAccessException("The FireFighter has already been assigned a tile");
    }
    f.setTile(t);
    FIREFIGHTERS.addLast(f);
    return FIREFIGHTERS.getFirst().getTile() != null;
  }

  public boolean allAssigned() {
    for (FireFighter f : FIREFIGHTERS) {
      if (f.getTile() == null) {
        return false;
      }
    }
    return true;
  }

  public void removeFireFighter(FireFighter f) throws IllegalAccessException {
    FIREFIGHTERS.remove(f);
    f.removeFromBoard();
  }

  public void endTurn() throws IllegalAccessException {

    FireFighter fireFighter = getCurrentFireFighter();
    if(!fireFighter.getTile().hasFire()) {
      FireFighter last = FIREFIGHTERS.removeFirst();
      last.resetActionPoints();
      FIREFIGHTERS.addLast(last);
      BoardManager.getInstance().endTurnFireSpread();
    }
  }

  public void move(Direction d) {
    if (canMove(d) && getCurrentFireFighter().moveAP(d)) {
      FireFighter f = getCurrentFireFighter();
      Tile oldTile = f.getTile();
      Tile newTile = oldTile.getAdjacentTile(d);
      f.setTile(newTile);
      if (newTile.hasPointOfInterest()) {
        if (newTile.hasRealVictim()) {
          newTile.getVictim().reveal();
        } else {
          newTile.setNullVictim();
          BoardManager.getInstance().addNewPointInterest();
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
      oldTile.setNullVictim();
      newTile.setVictim(v);
      f.setTile(newTile);
      BoardManager.getInstance().verifyVictimRescueStatus(newTile);
    }
  }

  public void chopWall(Direction d) {

    // Don't let him chop wall if ap < 3
    if(getCurrentFireFighter().getTile().hasFire() && getCurrentFireFighter().getActionPointsLeft() < 3)
      return;
    
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

    // Don't let him interact with door if ap < 2
    if(getCurrentFireFighter().getTile().hasFire() && getCurrentFireFighter().getActionPointsLeft() < 2)
      return;

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

    // Don't let him extenguish another Tile's fire or smoke if ap < 2
    if(getCurrentFireFighter().getTile().hasFire() && getCurrentFireFighter().getActionPointsLeft() < 2 && !d.equals(Direction.NODIRECTION))
      return;

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

  // Method that checks if you are allowed to end turn
  private boolean endTurnCheck(){
    return true;
//    Tile currentTile = getCurrentFireFighter().getTile();
//    if(!currentTile.hasFire()) return true; // return true if there is no fire on Tile
//
//    int currentAP = getCurrentFireFighter().getActionPointsLeft();
//
//    if(currentAP < 2){
//      if(currentAP == 0) return true; // No move possible sadly if ap = 0
//      if(currentAP == 1){
//        // Check if possible to move in 1 of 4 directions
//        Tile left = currentTile.getAdjacentTile(Direction.LEFT);
//        Tile right = currentTile.getAdjacentTile(Direction.RIGHT);
//        Tile up = currentTile.getAdjacentTile(Direction.TOP);
//        Tile bottom = currentTile.getAdjacentTile(Direction.BOTTOM);
//
//        if(!left.hasFire() && canMove(Direction.LEFT)) return false;
//        if(!right.hasFire() && canMove(Direction.RIGHT)) return false;
//        if(!up.hasFire() && canMove(Direction.TOP)) return false;
//        if(!bottom.hasFire() && canMove(Direction.BOTTOM)) return false;
//
//        return true; // If you can't move then sadly no block possible
//      }
//    }
//
//    return false; // false if ap >= 2, you can always extenguish fire first
  }

}
