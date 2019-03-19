package com.cs361d.flashpoint.model.FireFighterSpecialities;

import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;

public class FireCaptain extends FireFighterAdvanced{
  public FireCaptain(FireFighterColor color) {
    super(color,4,2, FireFighterAdvanceSpecialities.FIRE_CAPTAIN);
    this.maxSpecialAp = 2;
  }
}
