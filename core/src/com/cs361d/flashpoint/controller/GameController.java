package com.cs361d.flashpoint.controller;

import com.cs361d.flashpoint.model.BoardElements.FireFighter;
import com.cs361d.flashpoint.model.BoardElements.Tile;

import java.util.ArrayList;

public class GameController {

    // i = row, j = col
    public static Tile[][] extinguishFireToTile(Tile[][] tiles, int i, int j){
        tiles[i][j].setHas_fire(false);
        return tiles;
    }

    public static Tile[][] moveUp(Tile[][] tiles, int i, int j){

        FireFighter f = tiles[i][j].getFirefighters().get(0);

        tiles[i][j].setFirefighters(null);

        ArrayList<FireFighter> firefightersArr = new ArrayList <FireFighter>();
        firefightersArr.add(f);

        tiles[i - 1][j].setFirefighters(firefightersArr);

        return tiles;
    }

    public static Tile[][] moveDown(Tile[][] tiles, int i, int j){
        FireFighter f = tiles[i][j].getFirefighters().get(0);

        tiles[i][j].setFirefighters(null);

        ArrayList<FireFighter> firefightersArr = new ArrayList <FireFighter>();
        firefightersArr.add(f);

        tiles[i + 1][j].setFirefighters(firefightersArr);

        return tiles;
    }

    public static Tile[][] moveLeft(Tile[][] tiles, int i, int j){
        FireFighter f = tiles[i][j].getFirefighters().get(0);

        tiles[i][j].setFirefighters(null);

        ArrayList<FireFighter> firefightersArr = new ArrayList <FireFighter>();
        firefightersArr.add(f);

        tiles[i][j - 1].setFirefighters(firefightersArr);

        return tiles;
    }

    public static Tile[][] moveRight(Tile[][] tiles, int i, int j){
        FireFighter f = tiles[i][j].getFirefighters().get(0);

        tiles[i][j].setFirefighters(null);

        ArrayList<FireFighter> firefightersArr = new ArrayList <FireFighter>();
        firefightersArr.add(f);

        tiles[i][j + 1].setFirefighters(firefightersArr);
        
        return tiles;
    }
}
