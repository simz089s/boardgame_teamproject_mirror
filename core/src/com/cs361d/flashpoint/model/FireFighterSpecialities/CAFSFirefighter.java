package com.cs361d.flashpoint.model.FireFighterSpecialities;

import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;

public class CAFSFirefighter extends FireFighterAdvanced {
  public CAFSFirefighter(FireFighterColor color) {
    super(color,3,3, FireFighterAdvanceSpecialities.CAFS_FIREFIGHTER);
    this.maxSpecialAp = 3;
    this.actionsPointPerTurn = 3;
  }
}
