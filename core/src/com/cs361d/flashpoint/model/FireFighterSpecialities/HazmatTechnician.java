package com.cs361d.flashpoint.model.FireFighterSpecialities;

import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;
import com.cs361d.flashpoint.screen.Actions;

import java.util.List;

public class HazmatTechnician extends FireFighterAdvanced {

  public HazmatTechnician(FireFighterColor color) {
    super(color, 4, 0, FireFighterAdvanceSpecialities.HAZMAT_TECHNICIAN, true);
  }

  @Override
  public List<Actions> getActions() {
    List<Actions> actions = Actions.hazmatTechnicianActions();
    if (!isFirstMove()) {
      actions.remove(Actions.CREW_CHANGE);
    }
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
}
