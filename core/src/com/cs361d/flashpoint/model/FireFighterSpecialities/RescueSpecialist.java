package com.cs361d.flashpoint.model.FireFighterSpecialities;

import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;

public class RescueSpecialist extends FireFighterAdvanced{

  public RescueSpecialist(FireFighterColor color) {
    super(color, 4, 3, FireFighterAdvanceSpecialities.RESCUE_SPECIALIST);
    this.maxSpecialAp = 3;
  }
}
