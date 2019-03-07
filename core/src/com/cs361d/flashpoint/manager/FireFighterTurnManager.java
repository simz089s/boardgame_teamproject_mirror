package com.cs361d.flashpoint.manager;

import com.cs361d.flashpoint.model.BoardElements.*;
import com.cs361d.flashpoint.networking.Commands;
import com.cs361d.flashpoint.view.BoardScreen;
import org.jetbrains.annotations.NotNull;
import com.cs361d.flashpoint.networking.NetworkManager;
import org.json.simple.JSONArray;
import sun.nio.ch.Net;

import java.util.*;
import java.util.List;

public class FireFighterTurnManager implements Iterable<FireFighter> {

  protected final int MAX_NUMBER_OF_PLAYERS = 6;
  protected LinkedList<FireFighter> FIREFIGHTERS = new LinkedList<FireFighter>();
  protected List<FireFighterColor> notYetAssigned = new ArrayList<FireFighterColor>();
  protected boolean allAssigned = false;
  protected static FireFighterTurnManager instance =
      new FireFighterTurnManager();

  public static FireFighterTurnManager getInstance() {
    return instance;
  }

  public void setNotYetAssigned(List<FireFighterColor> list) {
    this.notYetAssigned = list;
  }
  protected void addFireFighter(FireFighter f) {
    if (FIREFIGHTERS.contains(f)) {
      throw new IllegalArgumentException();
    }
    FIREFIGHTERS.add(f);
    if (FIREFIGHTERS.size() > MAX_NUMBER_OF_PLAYERS) {
      throw new IllegalStateException();
    }
    notYetAssigned.add(f.getColor());
  }

  public void assignUserToFireFighter(User u) {
    if (notYetAssigned.isEmpty()) {
      throw new IllegalArgumentException("There are no firefighter available to join the game");
    }
    FireFighterColor c = notYetAssigned.remove(0);
    u.assignFireFighter(c);
    JSONArray array = new JSONArray();
    for (FireFighterColor col : notYetAssigned) {
        array.add(col.toString());
    }
    NetworkManager.getInstance().sendCommand(Commands.JOINGAME,array.toString());
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
        return allAssigned;
      }
    }
    allAssigned = true;
    return allAssigned;
  }

  public void removeFireFighter(FireFighter f) {
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
      sendChangeToNetwork();
    }
    else {
      BoardScreen.createDialog("Cannot end turn", "You cannot end turn as you are currently on a tile with fire");
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
      sendChangeToNetwork();
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
      sendChangeToNetwork();
    }
  }

  public void chopWall(Direction d) {

    // Don't let him chop wall if ap < 3
    if((getCurrentFireFighter().getTile().hasFire()) && (getCurrentFireFighter().getActionPointsLeft() < 3))
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
      sendChangeToNetwork();
    }
  }

  public void interactWithDoor(Direction d) {

    // Don't let him interact with door if ap < 2
    if((getCurrentFireFighter().getTile().hasFire()) && (getCurrentFireFighter().getActionPointsLeft() < 2))
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
      sendChangeToNetwork();
    }
  }

  public void extinguishFire(Direction d) {

    // Don't let him extenguish another Tile's fire or smoke if ap < 2
    if((getCurrentFireFighter().getTile().hasFire()) && (getCurrentFireFighter().getActionPointsLeft() < 2) && (!d.equals(Direction.NODIRECTION)))
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
      sendChangeToNetwork();
    }
  }

  public boolean isFireFighterTurn(FireFighter f) {
    return f == FIREFIGHTERS.peek();
  }

  private boolean canMove(Direction d) {

    // Don't allow a move if ap < 3 and moving into fire
    Tile t = getCurrentFireFighter().getTile().getAdjacentTile(d);
    if(t != null && t.hasFire() && (getCurrentFireFighter().getActionPointsLeft() < 3))
      return false;

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

  protected void reset() {
    instance = new FireFighterTurnManager();
  }

  public void placeInitialFireFighter(FireFighter f, Tile t) {
    int i = t.getI();
    int j = t.getJ();
    if (i > 0 || i > BoardManager.ROWS - 2 || j > 0 || j > BoardManager.COLUMNS - 2) {
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

  public void setOrder(List<FireFighterColor> list) {
    LinkedList<FireFighter> newList = new LinkedList<FireFighter>();
    if (list.isEmpty()) {
      return;
    }
    else {
      for (FireFighterColor c : list) {
        for (FireFighter f : FIREFIGHTERS) {
          if (f.getColor() == c) {
            newList.add(f);
          }
        }
      }
    }
    if (FIREFIGHTERS.size() != newList.size()) {
      throw new IllegalArgumentException("Not all colors of the list existed as fireFighters");
    }
    FIREFIGHTERS = newList;
  }

  private void sendChangeToNetwork() {
    NetworkManager.getInstance().sendCommand(Commands.GAMESTATE, DBHandler.getBoardAsString());
  }

  public static void useFireFighterGameManagerAdvanced() {
      instance = new FireFighterTurnManagerAdvance();
  }
  public static void useFireFighterGameManagerFamily() {
      instance = new FireFighterTurnManager();
  }

  @NotNull
  @Override
  public Iterator<FireFighter> iterator() {
    return FIREFIGHTERS.iterator();
  }
}
