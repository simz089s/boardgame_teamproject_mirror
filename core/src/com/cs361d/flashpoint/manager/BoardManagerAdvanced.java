package com.cs361d.flashpoint.manager;

public class BoardManagerAdvanced extends BoardManager {
    protected BoardManagerAdvanced() {
        super();
    }
    private int numHotSpotLeft = 24;
    private int numHazmatLeft = 6;

    public void addHazmat(int i, int j) {
        TILE_MAP[i][j].setHasHazmat(true);
    }
    public void addHotspot(int i, int j) {
        TILE_MAP[i][j].addHotSpot();
    }

}
