package com.cs361d.flashpoint.model.FireFighterSpecialities;

import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;
import com.cs361d.flashpoint.screen.Actions;

import java.util.List;

public class Paramedic extends FireFighterAdvanced{
  public Paramedic(FireFighterColor color) {
    super(color, 4, 0, FireFighterAdvanceSpecialities.PARAMEDIC, true);
  }

  public boolean treatVictimAP() {
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

  @Override
  public List<Actions> getActions() {
    List<Actions> actions = Actions.paramedicActions();
    if (!isFirstMove()) {
      actions.remove(Actions.CREW_CHANGE);
    }
    actionsFilter(actions);
    return actions;
  }

  @Override
  protected void actionsFilter(List<Actions> actions) {
    super.actionsFilter(actions);
    if (!this.currentTile.hasPointOfInterest()) {
      actions.remove(Actions.CURE_VICTIM);
    }
  }
}
