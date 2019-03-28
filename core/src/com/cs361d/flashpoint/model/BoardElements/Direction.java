package com.cs361d.flashpoint.model.BoardElements;

import com.cs361d.flashpoint.manager.Difficulty;

import java.util.ArrayList;
import java.util.List;

public enum Direction {
    TOP("top"),
    BOTTOM("bottom"),
    LEFT("left"),
    RIGHT("right"),
    NODIRECTION("nodirection"),
    NULLDIRECTION("nulldirection");

    private String text;

    public String toString() {
        return this.text;
    }

    public static Direction fromString(String s) {
        for (Direction d : Direction.values()) {
            if (d.text.equalsIgnoreCase(s)) {
                return d;
            }
        }
        throw new IllegalArgumentException("The String " + s + " does not correspond to any Direction ENUM");
    }
    Direction(String text){
        this.text = text;
    }

    public static List<Direction> outwardDirections() {
        List<Direction> directionList = new ArrayList<Direction>();
        directionList.add(TOP);
        directionList.add(BOTTOM);
        directionList.add(RIGHT);
        directionList.add(LEFT);
        return directionList;
    }
}
