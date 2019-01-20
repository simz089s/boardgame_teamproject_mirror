package com.cs361d.flashpoint.model.BoardElements;

import java.util.ArrayList;
import java.util.HashMap;

public class Tile {

    private final ArrayList<FireFighter> firefighters = new ArrayList<FireFighter>();
    private FireStatus fireStatus;
    private AbstractVictim victim;
    private boolean has_hotSpot;
    private final HashMap<Direction, Obstacle> OBSTACLES = new HashMap<Direction, Obstacle>();

    public Tile(FireStatus fireStatus, boolean has_hotSpot){
        this.fireStatus = fireStatus;
        this.victim = NullVictim.getInstance();
        this.has_hotSpot = has_hotSpot;
    }

    public boolean addObstacle(Direction d, Obstacle o) {
        if (OBSTACLES.containsKey(d)) {
            throw new UnsupportedOperationException("You cannot overwrite a Obstacle that already exits");
        }
        else {
            OBSTACLES.put(d, o);
            return true;
        }
    }

    public Obstacle getObstacle(Direction d) {
        return OBSTACLES.get(d);
    }

    public ArrayList<FireFighter> getFirefighters() {
        return firefighters;
    }

    public void addFirefighter(FireFighter firefighter) {

        firefighters.add(firefighter);
    }

    public void removeFirefighter(FireFighter firefighter) {

        firefighters.remove(firefighter);
    }

    public boolean containsPointOfInterest() {
        return !victim.isNull();
    }
    public AbstractVictim getVictim() {
        return victim;
    }
    /*
    verifies that the Victim is not a FALSE_ALARM
     */
    public boolean containsVictim() {
        if (victim.isNull()) {
            return false;
        }
        return !victim.isFalseAlarm();
    }

    public void setVictim(AbstractVictim victim) {
        this.victim = victim;
    }

    /*
    Makes the victim null again just like Trump
     */
    public void setNullVictim() {
        this.victim = NullVictim.getInstance();
    }

    public void setFireStatus(FireStatus f) {
        this.fireStatus = f;
    }

    public boolean hasFire() {
        return this.fireStatus == FireStatus.FIRE;
    }

    public boolean hasSmoke() {
        return this.fireStatus == FireStatus.SMOKE;
    }

    public boolean hasNoFireAndNoSmoke() {
        return this.fireStatus == FireStatus.EMPTY;
    }
}
