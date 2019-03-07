package com.cs361d.flashpoint.model.FireFighterRoles;

import com.cs361d.flashpoint.model.BoardElements.FireFighterAdvanced;
import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;

class Generalist extends FireFighterAdvanced {
  protected Generalist(FireFighterColor color, int actionPoints) {
    super(color, actionPoints);
    this.actionsPointPerTurn = 5;
  }
}
