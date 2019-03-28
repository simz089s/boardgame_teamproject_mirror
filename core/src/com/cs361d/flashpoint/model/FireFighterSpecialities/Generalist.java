package com.cs361d.flashpoint.model.FireFighterSpecialities;

import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;

public class Generalist extends FireFighterAdvanced {
  public Generalist(FireFighterColor color) {
    super(color, 5,0, FireFighterAdvanceSpecialities.GENERALIST, true);
    this.actionsPointPerTurn = 5;
  }
}
