package com.cs361d.flashpoint.manager;

import com.cs361d.flashpoint.model.BoardElements.FireStatus;

public class CreateNewGameManager {

    public static void createNewGame(String gameName, int numPlayers, MapKind map, Difficulty diff) {
        switch (diff) {
            case FAMILLY:
                createFamilyGame(gameName, numPlayers, map);
                break;

        }
    }

    private static void createFamilyGame(String gameName, int numPlayer, MapKind map) {
        BoardManager.getInstance().reset();
        switch (map) {
            case ORIGINAL1:
                DBHandler.createBoardDBFamilyVersion();
//                DBHandler.loadSavedGame("map1");
                populateMap1Familly();
                break;
            default:


        }
        BoardManager.getInstance().setGameName(gameName);
        BoardManager.getInstance().setFireFighterNumber(numPlayer);
    }

    public static void loadSavedGame(String name) {
//        DBHandler.loadSavedGame(name);
    }

    private static void populateMap1Familly() {
        BoardManager bm = BoardManager.getInstance();
        bm.addNewPointInterest(2,4);
        bm.addNewPointInterest(5,1);
        bm.addNewPointInterest(5,8);
        bm.addFireStatus(2,2, FireStatus.FIRE);
        bm.addFireStatus(2,3, FireStatus.FIRE);
        bm.addFireStatus(3,2, FireStatus.FIRE);
        bm.addFireStatus(3,3, FireStatus.FIRE);
        bm.addFireStatus(3,4, FireStatus.FIRE);
        bm.addFireStatus(3,5, FireStatus.FIRE);
        bm.addFireStatus(4,4, FireStatus.FIRE);
        bm.addFireStatus(5,6, FireStatus.FIRE);
        bm.addFireStatus(5,7, FireStatus.FIRE);
        bm.addFireStatus(6,6, FireStatus.FIRE);
    }
}

