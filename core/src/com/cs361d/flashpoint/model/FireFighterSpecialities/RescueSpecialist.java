package com.cs361d.flashpoint.model.FireFighterSpecialities;

import com.cs361d.flashpoint.model.BoardElements.Direction;
import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;

public class RescueSpecialist extends FireFighterAdvanced {

  public RescueSpecialist(FireFighterColor color) {
    super(color, 4, 3, FireFighterAdvanceSpecialities.RESCUE_SPECIALIST, true);
    this.maxSpecialAp = 3;
  }

  @Override
  public boolean moveAP(Direction d) {
    int cost = 1;
    if (currentTile.getAdjacentTile(d).hasFire()) {
      cost = 2;
    }
    if (specialActionPoints > cost) {
      specialActionPoints -= cost;
      firstMoveDone();
      return true;
    } else {
      return super.moveAP(d);
    }
  }

  @Override
  public boolean chopAP() {
    if (!(actionPoints < 1)) {
      actionPoints--;
      firstMoveDone();
      return true;
    }
    return false;
  }

  @Override
  public boolean extinguishAP() {
    if (!(actionPoints < 2)) {
      actionPoints -= 2;
      firstMoveDone();
      return true;
    }
    return false;
  }
}
