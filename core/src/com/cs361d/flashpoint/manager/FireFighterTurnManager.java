package com.cs361d.flashpoint.manager;

import com.cs361d.flashpoint.model.BoardElements.*;
import com.cs361d.flashpoint.model.FireFighterSpecialities.FireFighterAdvanced;
import com.cs361d.flashpoint.networking.Commands;
import com.cs361d.flashpoint.screen.BoardScreen;
import org.jetbrains.annotations.NotNull;
import com.cs361d.flashpoint.networking.NetworkManager;
import java.util.*;
import java.util.List;

public class FireFighterTurnManager implements Iterable<FireFighter> {

  protected final int MAX_NUMBER_OF_PLAYERS = 6;
  protected LinkedList<FireFighter> FIREFIGHTERS = new LinkedList<FireFighter>();
  protected boolean allAssigned = false;
  protected static FireFighterTurnManager instance = new FireFighterTurnManager();

  public static FireFighterTurnManager getInstance() {
    return instance;
  }

  protected void addFireFighter(FireFighter f) {
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
    FIREFIGHTERS.addLast(f);
    if (f.getTile() != null) {
      throw new IllegalAccessException("The FireFighter has already been assigned a tile");
    }
    f.setTile(t);
    sendChangeToNetwork();
    return FIREFIGHTERS.getFirst().getTile() != null;
  }

  public boolean allAssigned() {
    for (FireFighter f : FIREFIGHTERS) {
      if (f.getTile() == null) {
        return allAssigned;
      }
    }
    allAssigned = true;
    return true;
  }

  public void removeFireFighter(FireFighter f) {
    FIREFIGHTERS.remove(f);
    f.removeFromBoard();
  }

  public void endTurn() throws IllegalAccessException {

    FireFighter fireFighter = getCurrentFireFighter();
    if (!fireFighter.getTile().hasFire()) {
      FireFighter last = FIREFIGHTERS.removeFirst();
      last.resetActionPoints();
      FIREFIGHTERS.addLast(last);
      BoardManager.getInstance().endTurnFireSpread();
      sendChangeToNetwork();
    } else {
      sendMessageToGui("You cannot end turn as you are currently on a tile with fire");
    }
  }

  public void move(Direction d) {
    FireFighter f = getCurrentFireFighter();
    if (canMove(d)) {
      if (f.moveAP(d)) {
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
      } else {
        sendMessageToGui(
            "You do not have enough AP to move to that spot you need 1 ap to move to a tile with smoke or nothing"
                + " and 2 to move on a tile with fire");
      }
    }
  }

  public void moveWithVictim(Direction d) {
    if (canMoveWithVictim(d)) {
      FireFighter f = getCurrentFireFighter();
      if (f.moveWithVictimAP()) {
        AbstractVictim v = f.getTile().getVictim();
        Tile oldTile = f.getTile();
        Tile newTile = oldTile.getAdjacentTile(d);
        oldTile.setNullVictim();
        newTile.setVictim(v);
        f.setTile(newTile);
        if (!BoardManager.getInstance().verifyVictimRescueStatus(newTile)) {
          sendChangeToNetwork();
         }
      } else {
        sendMessageToGui("You need 2 AP to move with a victim");
      }
    }
  }

  public void chopWall(Direction d) {

    // Don't let him chop wall if ap < 3
    if ((getCurrentFireFighter().getTile().hasFire())
        && (getCurrentFireFighter().getActionPointsLeft() < 3)) {
      sendMessageToGui(
          "You cannot chop a wall if either you have less than 2 AP points"
              + " and you stand on a tile with fire and have less than 3 ap points");
      return;
    }

    Obstacle o = getCurrentFireFighter().getTile().getObstacle(d);
    if (o.isDoor()) {
      sendMessageToGui("Open, close or destroyed doors cannot be chopped");
      return;
    }
    if (o.isDestroyed()) {
      sendMessageToGui("You cannot chop the air or a wall with 2 damage" + " markers.");
      return;
    }
    if (getCurrentFireFighter().chopAP()) {
      if (o.applyDamage()) {
        sendChangeToNetwork();
        }
    } else {
      sendMessageToGui("You need 2 ap to chop a wall");
    }
  }

  public void interactWithDoor(Direction d) {

    // Don't let him interact with door if ap < 2
    if ((getCurrentFireFighter().getTile().hasFire())
        && (getCurrentFireFighter().getActionPointsLeft() < 2)) return;

    Obstacle o = getCurrentFireFighter().getTile().getObstacle(d);
    if (!o.isDoor()) {
      sendMessageToGui("You cannot open the air or a wall");
      return;
    }
    if (o.isDestroyed()) {
      sendMessageToGui("You cannot interact with a destroyed door");
      return;
    }
    if (getCurrentFireFighter().openCloseDoorAP()) {
      o.interactWithDoor();
      sendChangeToNetwork();
    }
  }

  public void extinguishFire(Direction d) {

    // Don't let him extenguish another Tile's fire or smoke if ap < 2
    if ((getCurrentFireFighter().getTile().hasFire())
        && (getCurrentFireFighter().getActionPointsLeft() < 2)
        && (!d.equals(Direction.NODIRECTION))) {
      sendMessageToGui(
          "You cannot end on a tile with fire on it so you cannot perform t"
              + "hat move since if you did that rule would not be repsected.");
      return;
    }
    Tile tileToExtinguish = getCurrentFireFighter().getTile().getAdjacentTile(d);
    if (tileToExtinguish == null
        || tileToExtinguish.hasNoFireAndNoSmoke()
        || getCurrentFireFighter().getTile().hasObstacle(d)) {
      sendMessageToGui(
          "The tile you are trying to extinguish does not contain smoke or fire or there is an obstacle blocking the way");
      return;
    }
    if (getCurrentFireFighter().extinguishAP()) {
      if (tileToExtinguish.hasFire()) {
        tileToExtinguish.setFireStatus(FireStatus.SMOKE);
      } else if (tileToExtinguish.hasSmoke()) {
        tileToExtinguish.setFireStatus(FireStatus.EMPTY);
      }
      sendChangeToNetwork();
    } else {
      sendMessageToGui("You need at least 1 AP to extingish a Tile");
    }
  }

  public boolean isFireFighterTurn(FireFighter f) {
    return f == FIREFIGHTERS.peek();
  }

  protected boolean canMove(Direction d) {

    // Don't allow a move if ap < 3 and moving into fire
    Tile t = getCurrentFireFighter().getTile().getAdjacentTile(d);
    if (t != null && t.hasFire() && (getCurrentFireFighter().getActionPointsLeft() < 3)) {
      sendMessageToGui("You cannot move on a tile with fire if you have less than 3 AP points");
      return false;
    }

    Tile currentTile = getCurrentFireFighter().getTile();
    // verifies that there is a tile in the direction
    if (currentTile.hasObstacle(d) || currentTile.getAdjacentTile(d) == null) {
      sendMessageToGui(
          "You cannot move to that tile as either there is an obstacle obstructing the move or"
              + " no tile at all at that location");
      return false;
    }

    return true;
  }

  protected boolean canMoveWithVictim(Direction d) {
    Tile currentTile = getCurrentFireFighter().getTile();
    Tile adjacentTile = currentTile.getAdjacentTile(d);
    if (!currentTile.hasRealVictim()) {
      sendMessageToGui("You must be on a tile containing a victim to move with it");
      return false;
    }
    if (adjacentTile.hasFire() || adjacentTile.hasPointOfInterest()) {
      sendMessageToGui("You cannot move a victim into a tile with fire or a tile with a POI");
      return false;
    }
    return true;
  }

  public FireFighter getCurrentFireFighter() {
    FireFighter f;
    do {
      f = FIREFIGHTERS.peek();
    } while (f == null);
    return f;
  }

  public static void reset() {
    instance = new FireFighterTurnManager();
  }

  public void placeInitialFireFighter(FireFighter f, Tile t) {
    int i = t.getI();
    int j = t.getJ();
    if (i > 0 || i > BoardManager.ROWS - 2 || j > 0 || j > BoardManager.COLUMNS - 2) {
      return;
    }
    // if the fireFighter already had a tile that means it was already one the board so we cannot
    // place it initially.
    if (f.getTile() != null) {
      return;
    }
    addFireFighter(f);
    f.setTile(t);
    t.addFirefighter(f);
  }

  public void setOrder(List<FireFighterColor> list) {
    LinkedList<FireFighter> newList = new LinkedList<FireFighter>();
    for (FireFighterColor c : list) {
      FireFighter f = FireFighter.getFireFighter(c);
      newList.add(f);
    }
    FIREFIGHTERS = newList;
  }

  private void sendChangeToNetwork() {
    if (!BoardManager.getInstance().gameHasEnded())
      NetworkManager.getInstance().sendCommand(Commands.GAMESTATE, DBHandler.getBoardAsString());
  }

  public static void useFireFighterGameManagerAdvanced() {
    instance = new FireFighterTurnManagerAdvance();
  }

  public static void useFireFighterGameManagerFamily() {
    instance = new FireFighterTurnManager();
  }

  public void sendMessageToGui(String message) {
    BoardScreen.createDialog("Action rejected", message);
  }

  @NotNull
  @Override
  public Iterator<FireFighter> iterator() {
    return FIREFIGHTERS.iterator();
  }
}
