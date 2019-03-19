package com.cs361d.flashpoint.model.FireFighterSpecialities;

import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;

public class Paramedic extends FireFighterAdvanced{
  public Paramedic(FireFighterColor color) {
    super(color, 4, 0, FireFighterAdvanceSpecialities.PARAMEDIC);
  }
}
