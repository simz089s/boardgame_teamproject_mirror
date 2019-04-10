package com.cs361d.flashpoint.manager;

import com.cs361d.flashpoint.model.BoardElements.*;
import com.cs361d.flashpoint.model.FireFighterSpecialities.FireCaptain;
import com.cs361d.flashpoint.model.FireFighterSpecialities.FireFighterAdvanced;
import com.cs361d.flashpoint.networking.ClientCommands;
import com.cs361d.flashpoint.networking.Server;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.List;

public class FireFighterTurnManager implements Iterable<FireFighter> {

  protected final int MAX_NUMBER_OF_PLAYERS = 6;
  protected LinkedList<FireFighter> FIREFIGHTERS = new LinkedList<FireFighter>();
  protected boolean sendToCaptain = false;
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
    return FIREFIGHTERS.getFirst().getTile() != null;
  }

  public boolean currentHasTile() {
    return getCurrentFireFighter().getTile() != null;
  }

  public void removeFireFighter(FireFighter f) {
    FIREFIGHTERS.remove(f);
    f.removeFromBoard();
  }

  public boolean endTurn() {
    Server.saveIp = true;
    FireFighter fireFighter = getCurrentFireFighter();
      FireFighter last = FIREFIGHTERS.removeFirst();
      FIREFIGHTERS.addLast(last);
      BoardManager.getInstance().endTurnFireSpread();
      last.resetActionPoints();
      return true;
  }

  public boolean move(Direction d) {
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
        return true;
      } else {
        sendActionRejectedMessageToCurrentPlayer(
            "You do not have enough AP to move to that spot you need 1 ap to move to a tile with smoke or nothing"
                + " and 2 to move on a tile with fire");
        return false;
      }
    }
    return false;
  }

  public boolean moveWithVictim(Direction d) {
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
          return true;
        }
      } else {
        sendActionRejectedMessageToCurrentPlayer("You do not have enough AP to move with a victim");
        return false;
      }
    }
    return false;
  }

  public boolean chopWall(Direction d) {

    Obstacle o = getCurrentFireFighter().getTile().getObstacle(d);
    if (o.isDoor()) {
      sendActionRejectedMessageToCurrentPlayer("Open, close or destroyed doors cannot be chopped");
      return false;
    }
    if (o.isDestroyed()) {
      sendActionRejectedMessageToCurrentPlayer("You cannot chop the air or a wall with 2 damage" + " markers.");
      return false;
    }
    if (getCurrentFireFighter().chopAP()) {
      if (o.applyDamage()) {
        return true;
      }
    }
    sendActionRejectedMessageToCurrentPlayer("You need 2 ap to chop a wall");
    return false;
  }

  public boolean interactWithDoor(Direction d) {

    Obstacle o = getCurrentFireFighter().getTile().getObstacle(d);
    if (!o.isDoor()) {
      sendActionRejectedMessageToCurrentPlayer("You cannot open the air or a wall");
      return false;
    }
    if (o.isDestroyed()) {
      sendActionRejectedMessageToCurrentPlayer("You cannot interact with a destroyed door");
      return false;
    }
    if (getCurrentFireFighter().openCloseDoorAP()) {
      o.interactWithDoor();
      return true;
    }
    sendActionRejectedMessageToCurrentPlayer("You need more Ap to interact with the door");
    return false;
  }

  public boolean extinguishFire(Direction d) {

    Tile tileToExtinguish = getCurrentFireFighter().getTile().getAdjacentTile(d);
    if (tileToExtinguish == null
        || tileToExtinguish.hasNoFireAndNoSmoke()
        || getCurrentFireFighter().getTile().hasObstacle(d)) {
      sendActionRejectedMessageToCurrentPlayer(
          "The tile you are trying to extinguish does not contain smoke or fire or there is an obstacle blocking the way");
      return false;
    }
    if (getCurrentFireFighter().extinguishAP()) {
      if (tileToExtinguish.hasFire()) {
        tileToExtinguish.setFireStatus(FireStatus.SMOKE);
      } else if (tileToExtinguish.hasSmoke()) {
        tileToExtinguish.setFireStatus(FireStatus.EMPTY);
      }
      return true;
    } else {
      sendActionRejectedMessageToCurrentPlayer("You need at least 1 AP to extingish a Tile");
      return false;
    }
  }

  public boolean isFireFighterTurn(FireFighter f) {
    return f == FIREFIGHTERS.peek();
  }

  protected boolean canMove(Direction d) {

    Tile currentTile = getCurrentFireFighter().getTile();
    // verifies that there is a tile in the direction
    if (currentTile.hasObstacle(d) || currentTile.getAdjacentTile(d) == null) {
      sendActionRejectedMessageToCurrentPlayer(
          "You cannot move to that tile as either there is an obstacle obstructing the move or"
              + " no tile at all at that location");
      return false;
    }

    return true;
  }

  protected boolean canMoveWithVictim(Direction d) {
    Tile currentTile = getCurrentFireFighter().getTile();
    Tile adjacentTile = currentTile.getAdjacentTile(d);
    if (currentTile.hasObstacle(d)) {
      sendActionRejectedMessageToCurrentPlayer("You cannot go through an obstacle with a victim");
      return false;
    }
    if (!currentTile.hasRealVictim()) {
      sendActionRejectedMessageToCurrentPlayer("You must be on a tile containing a victim to move with it");
      return false;
    }
    if (adjacentTile.hasFire() || adjacentTile.hasPointOfInterest()) {
      sendActionRejectedMessageToCurrentPlayer("You cannot move a victim into a tile with fire or a tile with a POI");
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

  public static void useFireFighterGameManagerAdvanced() {
    instance = new FireFighterTurnManagerAdvance();
    FireFighterAdvanced.reset();
  }

  public static void useFireFighterGameManagerFamily() {
    instance = new FireFighterTurnManager();
  }

  public void sendActionRejectedMessageToCurrentPlayer(String message) {
    sendMessageToCurrentPlayer("Action rejected",message);

  }

  public void sendMessageToCurrentPlayer(String title, String message) {
    JSONObject obj = new JSONObject();
    obj.put("title",title);
    obj.put("message",message);
    String ip = null;
    // Here if the command comes from the captain we send the result to its screen
    if (this.sendToCaptain) {
      for (FireFighter f : FIREFIGHTERS) {
        if (f instanceof FireCaptain) {
          ip = Server.getClientIP(f.getColor());
          break;
        }
      }

    } else {
      ip = Server.getClientIP(getCurrentFireFighter().getColor());
    }
    if (ip == null) {
      throw new IllegalArgumentException("The color " + getCurrentFireFighter().getColor().toString() +" is not assigned to a client");
    }
    Server.sendCommandToSpecificClient(ClientCommands.SHOW_MESSAGE_ON_SCREEN,obj.toJSONString(),ip);

  }

  @NotNull
  @Override
  public Iterator<FireFighter> iterator() {
    return FIREFIGHTERS.iterator();
  }
}
