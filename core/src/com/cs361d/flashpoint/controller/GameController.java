package com.cs361d.flashpoint.controller;

import com.cs361d.flashpoint.model.BoardElements.Tile;

public class GameController {

    // i = row, j = col
    public static Tile[][] extinguishFireToTile(Tile[][] tiles, int i, int j){
        tiles[i][j].setHas_fire(false);
        return tiles;
    }

    public static Tile[][] moveUp(Tile[][] tiles, int i, int j){
        tiles[i][j].setHas_firefighter("none");
        tiles[i - 1][j].setHas_firefighter("red");
        return tiles;
    }

    public static Tile[][] moveDown(Tile[][] tiles, int i, int j){
        tiles[i][j].setHas_firefighter("none");
        tiles[i + 1][j].setHas_firefighter("red");
        return tiles;
    }

    public static Tile[][] moveLeft(Tile[][] tiles, int i, int j){
        tiles[i][j].setHas_firefighter("none");
        tiles[i][j - 1].setHas_firefighter("red");
        return tiles;
    }

    public static Tile[][] moveRight(Tile[][] tiles, int i, int j){
        tiles[i][j].setHas_firefighter("none");
        tiles[i][j + 1].setHas_firefighter("red");
        return tiles;
    }
}
