package com.cs361d.flashpoint.model.FireFighterSpecialities;

import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;
import com.cs361d.flashpoint.screen.Actions;

import java.util.List;

public class HazmatTechnician extends FireFighterAdvanced {

  public HazmatTechnician(FireFighterColor color) {
    super(color, 4, 0, FireFighterAdvanceSpecialties.HAZMAT_TECHNICIAN, true);
  }

  @Override
  public List<Actions> getActions() {
    List<Actions> actions = Actions.hazmatTechnicianActions();
    if (!isFirstMove()) {
      actions.remove(Actions.CREW_CHANGE);
    }
    actionsFilter(actions);
    return actions;
  }

  public boolean removeHazmatAp() {
    if (actionPoints < 2) {
      return false;
    }
    else {
      actionPoints -= 2;
      firstMoveDone();
      return true;
    }
  }

  @Override
  protected void actionsFilter(List<Actions> actions) {
    super.actionsFilter(actions);
    if (!this.currentTile.hasHazmat()) {
      actions.remove(Actions.REMOVE_HAZMAT);
    }
  }
}
