package com.cs361d.flashpoint.model.FireFighterSpecialities;

import com.cs361d.flashpoint.model.BoardElements.Direction;
import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;
import com.cs361d.flashpoint.model.BoardElements.Tile;
import com.cs361d.flashpoint.screen.BoardScreen;

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

    @Override
    public boolean moveAP(Direction d) {
        if(currentTile.getAdjacentTile(d).hasFire()) {
            BoardScreen.createDialog("Action rejected", "The rescue dog cannot go on a space with fire");
            return false;
        }
        else if (currentTile.hasObstacle(d)) {
            if (actionPoints < 2) {
                return false;
            }
            else {
                actionPoints -= 2;
                return true;
            }
        }
        else {
            if (actionPoints < 1) {
                return false;
            }
            else {
                actionPoints--;
                return true;
            }
        }
    }

    @Override
    public boolean moveWithVictimAP() {
        if (currentTile.getVictim().isCured()) {
            return super.moveWithVictimAP();
        }
        else {
            if (actionPoints < 4) {
                return false;
            }
            else {
                actionPoints -= 4;
                return true;
            }
        }
    }

    @Override
    public boolean extinguishAP() {
        return false;
    }

    @Override
    public boolean openCloseDoorAP() {
        return false;
    }

    @Override
    public boolean chopAP() {
        return false;
    }

    @Override
    public boolean driveAp() {
        return false;
    }

    @Override
    public boolean dodgeAp() {
        return false;
    }
}
