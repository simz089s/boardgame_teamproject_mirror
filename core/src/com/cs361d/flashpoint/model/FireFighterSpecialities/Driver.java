package com.cs361d.flashpoint.model.FireFighterSpecialities;

import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;

public class Driver extends FireFighterAdvanced {

  public Driver(FireFighterColor color) {
    super(color, 4, 2, FireFighterAdvanceSpecialities.DRIVER);
    this.maxSpecialAp = 2;
  }

  @Override
  public boolean fireTheDeckGunAp() {
    if (actionPoints < 2) {
      return false;
    }
    else {
      actionPoints -= 2;
      return true;
    }
  }
}
