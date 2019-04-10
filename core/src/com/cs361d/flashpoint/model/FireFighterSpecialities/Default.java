package com.cs361d.flashpoint.model.FireFighterSpecialities;

import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;

public class Default extends FireFighterAdvanced {

    public Default(FireFighterColor color) {
        super(color, 0, 0, FireFighterAdvanceSpecialties.NO_SPECIALTY, true);
    }
}
