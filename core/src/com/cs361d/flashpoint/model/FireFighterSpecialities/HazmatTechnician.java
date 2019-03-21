package com.cs361d.flashpoint.model.FireFighterSpecialities;

import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;
import com.cs361d.flashpoint.screen.Actions;

import java.util.List;

public class HazmatTechnician extends FireFighterAdvanced {

  public HazmatTechnician(FireFighterColor color) {
    super(color, 4, 0, FireFighterAdvanceSpecialities.HAZMAT_TECHNICIAN);
  }

  @Override
  public List<Actions> getActions() {
    return Actions.hazmatTechnicianActions();
  }
}
