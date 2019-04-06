package com.cs361d.flashpoint.model.FireFighterSpecialities;

import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;
import com.cs361d.flashpoint.screen.Actions;

import java.util.List;

public class ImagingTechnician extends FireFighterAdvanced {
  public ImagingTechnician(FireFighterColor color) {
    super(color, 4, 0, FireFighterAdvanceSpecialities.IMAGING_TECHNICIAN, true);
  }

  @Override
  public List<Actions> getActions() {
    List<Actions> actions = Actions.imagingTechnicianActions();
    if (!isFirstMove()) {
      actions.remove(Actions.CREW_CHANGE);
    }
    actionsFilter(actions);
    return actions;
  }

  public boolean flipPOIAP() {
    if (actionPoints < 1) {
      return false;
    }
    else {
      actionPoints--;
      firstMoveDone();
      return true;
    }
  }
}
