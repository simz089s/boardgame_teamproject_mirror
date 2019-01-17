package com.cs361d.flashpoint.Entities;

public class Board
{
    Tile[][] Tiles = new Tile[10][8];

    //create an object of SingleObject
    private static Board instance = new Board();

    //make the constructor private so that this class cannot be instantiated
    private Board(){}

    //Get the only object available
    public static Board getInstance(){
        return instance;
    }
}


