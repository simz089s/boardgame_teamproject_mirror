package com.cs361d.flashpoint.model.FireFighterSpecialities;

import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;
import com.cs361d.flashpoint.screen.Actions;

import java.util.List;

public class FireCaptain extends FireFighterAdvanced{
  public FireCaptain(FireFighterColor color) {
    super(color,4,2, FireFighterAdvanceSpecialities.FIRE_CAPTAIN, true
    );
    this.maxSpecialAp = 2;
  }

  @Override
  public List<Actions> getActions() {
    List<Actions> actions = Actions.fireCaptainActions();
    if (!isFirstMove()) {
      actions.remove(Actions.CREW_CHANGE);
    }
    return actions;
  }
}
