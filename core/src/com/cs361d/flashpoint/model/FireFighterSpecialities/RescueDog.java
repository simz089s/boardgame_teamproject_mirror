package com.cs361d.flashpoint.model.FireFighterSpecialities;

import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;

public class RescueDog extends FireFighterAdvanced {

    public RescueDog(FireFighterColor color) {
        super(color, 12, 0, FireFighterAdvanceSpecialities.RESCUE_DOG);
        this.maxActionPoint = 18;
    }

    @Override
    public void resetActionPoints() {
        if (this.actionPoints > 6) {
            this.actionPoints = 6;
        }
        super.resetActionPoints();
    }
}
