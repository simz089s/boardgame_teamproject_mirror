package com.cs361d.flashpoint.model.BoardElements;

import com.cs361d.flashpoint.model.FireFighterSpecialities.FireFighterAdvanceSpecialities;
import com.cs361d.flashpoint.screen.Actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FireFighter {

  protected static final Map<FireFighterColor, FireFighter> FIREFIGHTERS =
      new HashMap<FireFighterColor, FireFighter>();
  protected final FireFighterColor color;
  protected int maxActionPoint = 8;
  protected int actionsPointPerTurn = 4;
  protected int actionPoints;
  protected Tile currentTile;
  protected FireFighter(FireFighterColor color, int actionPoints) {
//    if (actionPoints > maxActionPoint) {
//      throw new IllegalStateException(
//          "Action points cannot exceed " + maxActionPoint + " was: " + actionPoints);
//    }
    this.color = color;
    this.actionPoints = actionPoints;
  }

  public List<Actions> getActions() {
    return Actions.basicActions();
  }

  public static FireFighter createFireFighter(
      FireFighterColor color, int actionPoints) {
    FireFighter f;
    if (FIREFIGHTERS.containsKey(color)) {
      f = FIREFIGHTERS.get(color);
      f.actionPoints = actionPoints;
    } else {
      f = new FireFighter(color, actionPoints);
      FIREFIGHTERS.put(color, f);
    }
    return f;
  }

  // If the fireFighter has not yet been Initalized it initialises it with default AP of 4 else it just returns the instance
  public static FireFighter getFireFighter(FireFighterColor color) {
    if (color == null) {
      return null;
    }
    FireFighter f;
    if (FIREFIGHTERS.containsKey(color)) {
      f = FIREFIGHTERS.get(color);
    } else {
      f = new FireFighter(color, 4);
      FIREFIGHTERS.put(color, f);
    }
    return f;

  }

  public void removeFromBoard() {
      if (currentTile == null) {
          throw new IllegalStateException("This method cannot remove a FireFighter from the board if it has no current tile");
      }
      currentTile.removeFirefighter(this);
      currentTile = null;
  }

  public void setTile(Tile t) {
    if (currentTile != null) {
      currentTile.removeFirefighter(this);
    }
    this.currentTile = t;
    t.addFirefighter(this);
  }

  public Tile getTile() {
    return currentTile;
  }

  public FireFighterColor getColor() {
    return color;
  }

  public int getActionPointsLeft() {
    return actionPoints;
  }


  public void resetActionPoints() {
    this.actionPoints += actionsPointPerTurn;
    if (this.actionPoints > maxActionPoint) {
      this.actionPoints = maxActionPoint;
    }
  }

  public boolean moveAP(Direction d) {
    Tile newTile = currentTile.getAdjacentTile(d);
    if (newTile.hasFire()) {
      if (actionPoints >= 2) {
        actionPoints -= 2;
        return true;
      }
    } else if (actionPoints >= 1) {
      actionPoints--;
      return true;
    }
    return false;
  }

  public boolean chopAP() {
    if (actionPoints < 2) {
      return false;
    }
    actionPoints -= 2;
    return true;
  }

  public boolean extinguishAP() {
    if (actionPoints < 1) {
      return false;
    }
    actionPoints--;
    return true;
  }

  public boolean moveWithVictimAP() {
    if (actionPoints < 2) {
      return false;
    }
    actionPoints -= 2;
    return true;
  }

  public boolean openCloseDoorAP() {
    if (actionPoints < 1) {
      return false;
    }
    actionPoints--;
    return true;
  }

  public static void reset() {
    FIREFIGHTERS.clear();
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof FireFighter)) {
      return false;
    }
    else if( o == this) {
      return true;
    }
    else {
      if (this.getColor() == FireFighterColor.NOT_ASSIGNED ) {
        return false;
      }
      return this.getColor() == ((FireFighter) o).getColor();
    }
  }
}
