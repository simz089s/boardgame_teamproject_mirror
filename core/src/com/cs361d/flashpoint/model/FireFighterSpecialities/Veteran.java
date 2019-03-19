package com.cs361d.flashpoint.model.FireFighterSpecialities;

import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;

public class Veteran extends FireFighterAdvanced {
    public Veteran(FireFighterColor color) {
        super(color, 4, 0, FireFighterAdvanceSpecialities.VETERAN);
    }
}
