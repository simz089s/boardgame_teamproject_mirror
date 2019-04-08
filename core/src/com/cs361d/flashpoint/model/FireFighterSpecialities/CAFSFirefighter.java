package com.cs361d.flashpoint.model.FireFighterSpecialities;

import com.cs361d.flashpoint.model.BoardElements.Direction;
import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;

public class CAFSFirefighter extends FireFighterAdvanced {
  private int saveForCaptain = 0;
  public CAFSFirefighter(FireFighterColor color) {
    super(color,3,3, FireFighterAdvanceSpecialities.CAFS_FIREFIGHTER, true);
    this.maxSpecialAp = 3;
    this.actionsPointPerTurn = 3;
  }

  @Override
  public boolean extinguishAP() {
    if (!(specialActionPoints < 1)) {
      specialActionPoints--;
      firstMoveDone();
      return true;
    }
    else {
      return super.extinguishAP();
    }
  }

  @Override
  public boolean moveAP(Direction d) {
    return super.moveAP(d);
  }


  @Override
  public int getActionPointsLeft() {
    int val = super.getActionPointsLeft()+this.saveForCaptain;
    this.saveForCaptain = 0;
    return val;
  }

  @Override
  public boolean setForFireCaptainAction(FireCaptain fc) {
    this.specialActionPointToSave = this.specialActionPoints;
    this.actionPointToSave = this.actionPoints;
    this.saveForCaptain = fc.getSpecialActionPoints();
    if (this.saveForCaptain > 1) {
      this.actionPoints = 1;
      this.saveForCaptain--;
    }
    else {
      this.actionPoints = this.saveForCaptain;
      this.saveForCaptain = 0;
    }
    this.specialActionPoints = 0;
    return true;
  }

}
