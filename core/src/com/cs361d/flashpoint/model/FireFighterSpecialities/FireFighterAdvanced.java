package com.cs361d.flashpoint.model.FireFighterSpecialities;

import com.cs361d.flashpoint.model.BoardElements.Direction;
import com.cs361d.flashpoint.model.BoardElements.FireFighter;
import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;
import com.cs361d.flashpoint.model.BoardElements.Tile;
import com.cs361d.flashpoint.screen.Actions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class FireFighterAdvanced extends FireFighter {
  protected int specialActionPoints = 0;
  protected boolean hadVeteranBonus = false;
  protected final FireFighterAdvanceSpecialities SPECIALITY;
  protected int maxSpecialAp = 0;
  protected boolean firstTurn;
  protected int actionPointToSave = 0;
  protected int specialActionPointToSave = 0;
  protected boolean firstMove = true;
  protected static final Map<FireFighterColor, FireFighterAdvanced> FIREFIGHTERS =
      new HashMap<FireFighterColor, FireFighterAdvanced>();

  protected FireFighterAdvanced(
      FireFighterColor color,
      int actionPoints,
      int specialActionPoints,
      FireFighterAdvanceSpecialities role, boolean firstMove) {
    super(color, actionPoints);
    this.specialActionPoints = specialActionPoints;
    this.SPECIALITY = role;
    this.firstTurn = true;
    this.firstMove = firstMove;
  }

  public void firstTurnDone() {
    this.firstTurn = false;
  }

  public void setFirstTurn(boolean firstTurn) {
    this.firstTurn = firstTurn;
  }

  public boolean isFirstTurn() {
    return firstTurn;
  }

  @Override
  public List<Actions> getActions() {
    List<Actions> actions = Actions.advancedActions();
    if (!firstMove) {
      actions.remove(Actions.CREW_CHANGE);
    }
    actionsFilter(actions);
    return actions;
  }

  public static void reset() {
    FIREFIGHTERS.clear();
  }

  @Override
  public void setTile(Tile t) {
    if (currentTile != null) {
      currentTile.removeFirefighter(this);
    }
    this.currentTile = t;
    t.addFirefighter(this);
  }

  public boolean isActualAdvanceFireFighter() {
    return false;
  }

  public boolean hasSpeciality(FireFighterAdvanceSpecialities speciality) {
    return SPECIALITY == speciality;
  }

  public int getSpecialActionPoints() {
    return this.specialActionPoints;
  }

  public FireFighterAdvanceSpecialities getSpeciality() {
    return SPECIALITY;
  }

  @Override
  public boolean equals(Object o) {
    boolean parentTruth = super.equals(o);
    if (parentTruth) {
      return true;
    } else if (!(o instanceof FireFighterAdvanced)) {
      return false;
    } else if (((FireFighterAdvanced) o).SPECIALITY
        == FireFighterAdvanceSpecialities.NO_SPECIALITY) {
      return false;
    }

    return ((FireFighterAdvanced) o).SPECIALITY == SPECIALITY;
  }

  public boolean isDefault() {
    return true;
  }

  public static FireFighterAdvanced createFireFighter(
      FireFighterColor color, FireFighterAdvanceSpecialities role) {
    FireFighterAdvanced f;
    if (FIREFIGHTERS.containsKey(color)) {
      f = FIREFIGHTERS.get(color);
      if (f.hasSpeciality(role)) {
        return f;
      }
    }
    if (role == FireFighterAdvanceSpecialities.NO_SPECIALITY) {
      f = new Default(color);
    } else {
      switch (role) {
        case GENERALIST:
          f = new Generalist(color);
          break;
        case PARAMEDIC:
          f = new Paramedic(color);
          break;
        case FIRE_CAPTAIN:
          f = new FireCaptain(color);
          break;
        case CAFS_FIREFIGHTER:
          f = new CAFSFirefighter(color);
          break;
        case HAZMAT_TECHNICIAN:
          f = new HazmatTechnician(color);
          break;
        case RESCUE_SPECIALIST:
          f = new RescueSpecialist(color);
          break;
        case DRIVER:
          f = new Driver(color);
          break;
        case RESCUE_DOG:
          f = new RescueDog(color);
          break;
        case VETERAN:
          f = new Veteran(color);
          break;
        case IMAGING_TECHNICIAN:
          f = new ImagingTechnician(color);
          break;
        case STRUCTURAL_ENGINEER:
          f = new StructuralEngineer(color);
          break;
        case PYROMANCER:
          f = new Pyromancer(color);
          break;
        default:
          throw new IllegalArgumentException("the enum is not yet supported");
      }
    }
    FIREFIGHTERS.put(color, f);
    return f;
  }

  public boolean getHadVeteranBonus() {
    return hadVeteranBonus;
  }

  @Override
  public boolean chopAP() {
    if(super.chopAP())
    {
      firstMoveDone();
      return true;
    }
    return false;
  }

  @Override
  public boolean openCloseDoorAP() {
    if(super.openCloseDoorAP()) {
      firstMoveDone();
      return true;
    }
    return false;
  }

  @Override
  public boolean extinguishAP() {
    if (super.extinguishAP()) {
      firstMoveDone();
      return true;
    }
    return false;
  }

  @Override
  public boolean moveAP(Direction d) {
    if (super.moveAP(d)) {
      firstMoveDone();
      return true;
    }
    return false;
  }


  @Override
  public void resetActionPoints() {
    this.actionPoints += actionsPointPerTurn;
    if (this.actionPoints > maxActionPoint) {
      this.actionPoints = maxActionPoint;
    }
    this.specialActionPoints = maxSpecialAp;
    this.hadVeteranBonus = false;
    this.firstMove = true;
  }

  public void setActionPoint(int points) {
    if (points > maxActionPoint) {
        actionPoints = maxActionPoint;
      if (hadVeteranBonus) {
        actionPoints++;
      }
    } else {
      actionPoints = points;
    }
  }

  public void setSpecialActionPoints(int points) {
    if (points > maxSpecialAp) {
      specialActionPoints = maxSpecialAp;
    } else {
      specialActionPoints = points;
    }
  }

  public boolean crewChangeAP() {
    if (actionPoints < 2) {
      return false;
    }
    actionPoints -= 2;
    firstMoveDone();
    return true;
  }

  @Override
  public boolean moveWithVictimAP() {
    int cost = 2;
    if (currentTile.getVictim().isCured()) {
      cost = 1;
    }
    if (actionPoints < cost) {
      return false;
    }
    actionPoints -= cost;
    firstMoveDone();
    return true;
  }

  public boolean fireTheDeckGunAp() {
    if (actionPoints < 4) {
      return false;
    } else {
      actionPoints -= 4;
      firstMoveDone();
      return true;
    }
  }

  public boolean driveAp() {
    if (actionPoints < 2) {
      return false;
    } else {
      actionPoints -= 2;
      firstMoveDone();
      return true;
    }
  }

  public boolean dodgeAp() {
    if (actionPoints < 2) {
      return false;
    } else {
      actionPoints -= 2;
      return true;
    }
  }

  public boolean veteranBonus() {
    if (!hadVeteranBonus) {
      actionPoints++;
      hadVeteranBonus = true;
      return true;
      }
    return false;
  }

  public void setHadVeteranBonus(boolean value) {
    this.hadVeteranBonus = value;
  }

  public void removeVeteranBonus() {
    if (hadVeteranBonus && actionPoints > 0) {
      actionPoints--;
      hadVeteranBonus = false;
    }
  }

  public boolean moveWithHazmatAp() {
    if (this.actionPoints < 2) {
      return false;
    } else {
      actionPoints -= 2;
      firstMoveDone();
      return true;
    }
  }

  // If the fireFighter has not yet been Initalized it initialises it with default AP of 4 else it
  // just returns the instance
  public static FireFighter getFireFighter(FireFighterColor color) {
    if (color == null) {
      return null;
    }
    FireFighterAdvanced f;
    if (FIREFIGHTERS.containsKey(color)) {
      f = FIREFIGHTERS.get(color);
    } else {
      f = new Default(color);
      FIREFIGHTERS.put(color, f);
    }
    return f;
  }

  public boolean setForFireCaptainAction(FireCaptain fc) {
    this.specialActionPointToSave = this.specialActionPoints;
    this.actionPointToSave = this.actionPoints;
    this.actionPoints = fc.getSpecialActionPoints();
    this.specialActionPoints = 0;
    return true;
  }

  public void resetSavedActionPoints() {
    this.specialActionPoints = this.specialActionPointToSave;
    this.actionPoints = this.actionPointToSave;
  }

  public boolean isFirstMove() {
    return firstMove;
  }

  public void setFirstMove(boolean firstMove) {
    this.firstMove = firstMove;
  }

  protected void firstMoveDone() {
    this.firstMove = false;
  }

  @Override
  protected void actionsFilter(List<Actions> actions) {
    super.actionsFilter(actions);
    if (!this.currentTile.hasHazmat()) {
      actions.remove(Actions.MOVE_WITH_HAZMAT);
    }
    if (!this.currentTile.hasFireTruck()) {
      actions.remove(Actions.DRIVE_FIRETRUCK);
      actions.remove(Actions.CREW_CHANGE);
      actions.remove(Actions.FIRE_DECK_GUN);
    }
  }
}
