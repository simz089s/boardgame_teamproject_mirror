package com.cs361d.flashpoint.model.FireFighterSpecialities;

import com.cs361d.flashpoint.manager.FireFighterTurnManager;
import com.cs361d.flashpoint.manager.FireFighterTurnManagerAdvance;
import com.cs361d.flashpoint.model.BoardElements.FireFighter;
import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;
import com.cs361d.flashpoint.model.BoardElements.Tile;

import java.util.HashMap;
import java.util.Map;

public abstract class FireFighterAdvanced extends FireFighter {
  protected int specialActionPoints = 0;
  protected final FireFighterAdvanceSpecialities SPECIALITY;
  protected int maxSpecialAp = 0;
  protected static final Map<FireFighterColor, FireFighterAdvanced> FIREFIGHTERS =
      new HashMap<FireFighterColor, FireFighterAdvanced>();

  protected FireFighterAdvanced(
      FireFighterColor color,
      int actionPoints,
      int specialActionPoints,
      FireFighterAdvanceSpecialities role) {
    super(color, actionPoints);
    this.specialActionPoints = specialActionPoints;
    this.SPECIALITY = role;
  }

  @Override
  public void setTile(Tile t) {
    if (currentTile != null) {
      currentTile.removeFirefighter(this);
    }
    if (SPECIALITY == FireFighterAdvanceSpecialities.NO_ROLE) {
        throw new IllegalArgumentException("Cannot Place a FireFighter on board with no speciality");
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

  public FireFighterAdvanceSpecialities getSpeciality() {
    return SPECIALITY;
  }

  @Override
  public boolean equals(Object o) {
    boolean supertruth = super.equals(o);
    if (supertruth) {
      return true;
    } else if (!(o instanceof FireFighterAdvanced)) {
      return false;
    } else if (((FireFighterAdvanced) o).SPECIALITY == FireFighterAdvanceSpecialities.NO_ROLE) {
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
      if (!f.hasSpeciality(role)) {
      } else {
        return f;
      }
    }
    if (role == FireFighterAdvanceSpecialities.NO_ROLE) {
      f = new Default(color);
    } else {
      FireFighterTurnManagerAdvance tm =
          (FireFighterTurnManagerAdvance) FireFighterTurnManager.getInstance();
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
        default:
          throw new IllegalArgumentException("the enum is not yet supported");
      }
    }
    FIREFIGHTERS.put(color, f);
    return f;
  }

  @Override
  public void resetActionPoints() {
    this.actionPoints += actionsPointPerTurn;
    if (this.actionPoints > maxActionPoint) {
      this.actionPoints = maxActionPoint;
    }
    this.specialActionPoints = maxSpecialAp;
  }

  public void setActionPoint(int points) {
    if (points > maxActionPoint) {
      actionPoints = maxActionPoint;
    } else {
      actionPoints = points;
    }
  }

  public void setSpecialActionPoints(int points) {
    if (points > maxSpecialAp) {
      actionPoints = maxSpecialAp;
    } else {
      actionPoints = points;
    }
  }

  public boolean crewChangeAP() {
    if (actionPoints < 2) {
      return false;
    }
    actionPoints -= 2;
    return true;
  }
}
