package com.cs361d.flashpoint.model.FireFighterSpecialities;

import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;

public class HazmatTechnician extends FireFighterAdvanced {

  public HazmatTechnician(FireFighterColor color) {
    super(color, 4, 0, FireFighterAdvanceSpecialities.HAZMAT_TECHNICIAN);
  }
}
