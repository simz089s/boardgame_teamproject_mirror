package com.cs361d.flashpoint.model.FireFighterSpecialities;

import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;
import com.cs361d.flashpoint.screen.Actions;

import java.util.List;

public class StructuralEngineer extends FireFighterAdvanced {
  protected StructuralEngineer(FireFighterColor color) {
    super(color, 4, 0, FireFighterAdvanceSpecialities.STRUCTURAL_ENGINEER);
  }

  public boolean clearAp() {
    if (actionPoints < 1) {
      return false;
    } else {
      actionPoints--;
      return true;
    }
  }

  public boolean repairAp() {
    if (actionPoints < 2) {
      return false;
    } else {
      actionPoints -= 2;
      return true;
    }
  }

    @Override
    public List<Actions> getActions() {
        return Actions.structuralEngineerActions();
    }
}
