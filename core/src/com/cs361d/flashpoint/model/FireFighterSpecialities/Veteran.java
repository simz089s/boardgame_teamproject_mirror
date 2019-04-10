package com.cs361d.flashpoint.model.FireFighterSpecialities;

import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;

public class Veteran extends FireFighterAdvanced {
    public Veteran(FireFighterColor color) {
        super(color, 4, 0, FireFighterAdvanceSpecialties.VETERAN, true);
    }

    @Override
    public boolean dodgeAp() {
        if (actionPoints < 1) {
            return false;
        }
        else {
            actionPoints--;
            return true;
        }
    }

    // The veteran is not affected by the bonus actions.
    @Override
    public boolean veteranBonus() {
        return false;
    }
}
