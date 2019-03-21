package com.cs361d.flashpoint.model.FireFighterSpecialities;

import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;
import com.cs361d.flashpoint.screen.Actions;

import java.util.List;

public class Paramedic extends FireFighterAdvanced{
  public Paramedic(FireFighterColor color) {
    super(color, 4, 0, FireFighterAdvanceSpecialities.PARAMEDIC);
  }

  public boolean treatVictimAP() {
    if (!(actionPoints < 1)) {
      actionPoints--;
      return true;
    }
    return false;
  }

  @Override
  public boolean extinguishAP() {
    if (!(actionPoints < 2)) {
      actionPoints -= 2;
      return true;
    }
    return false;
  }

  @Override
  public List<Actions> getActions() {
    return Actions.paramedicActions();
  }
}
