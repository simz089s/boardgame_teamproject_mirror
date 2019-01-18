package com.cs361d.flashpoint.Entities.BoardElements;

public class Tile {
    Wall leftWall;
    Wall rightWall;
    Wall topWall;
    Wall bottomWall;

    int hasFire; //0-no fire; 1-smoke; 2-fire
    //boolean hasSmoke; Can just be part of hasFire
    boolean hasHotSpot;
    boolean hasHazmat;
}
