package com.cs361d.flashpoint.model.FireFighterSpecialities;

import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;
import com.cs361d.flashpoint.screen.Actions;

import java.util.List;

public class StructuralEngineer extends FireFighterAdvanced {
  protected StructuralEngineer(FireFighterColor color) {
    super(color, 4, 0, FireFighterAdvanceSpecialities.STRUCTURAL_ENGINEER, true);
  }

  public boolean clearAp() {
    if (actionPoints < 1) {
      return false;
    } else {
      actionPoints--;
      firstMoveDone();
      return true;
    }
  }

  public boolean repairAp() {
    if (actionPoints < 2) {
      return false;
    } else {
      actionPoints -= 2;
      firstMoveDone();
      return true;
    }
  }

  @Override
  public List<Actions> getActions() {
    List<Actions> actions = Actions.structuralEngineerActions();
    if (!isFirstMove()) {
      actions.remove(crewChangeAP());
    }
    actionsFilter(actions);
    return actions;
  }

  @Override
  protected void actionsFilter(List<Actions> actions) {
    super.actionsFilter(actions);
    if (!this.currentTile.hasHotSpot()) {
      actions.remove(Actions.CLEAR_HOTSPOT);
    }
  }
}
