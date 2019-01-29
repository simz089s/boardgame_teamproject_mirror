package com.cs361d.flashpoint.manager;


import com.cs361d.flashpoint.model.BoardElements.Direction;
import com.cs361d.flashpoint.model.BoardElements.FireFighter;
import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;
import com.cs361d.flashpoint.model.BoardElements.FireStatus;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

public class DBHandler {

    private static final String TILE_DB_FILENAME = "db/tiles.json";

    // FAMILY GAME BOARD OBSTACLES PLACEMENT
    private static final String[] TOP_WALL_TILE_ID = {"1-1", "1-2", "1-3", "1-4", "1-5","1-6", "1-7", "1-8",
            "7-1", "7-2", "7-3", "7-4", "7-5","7-6", "7-7", "7-8",
            "3-3", "3-4", "3-5", "3-6", "3-7","3-8",
            "5-1", "5-2", "5-3", "5-4", "5-5","5-6", "5-7", "5-8"
    };
    private static final String[] LEFT_WALL_TILE_ID = {"1-1", "2-1", "3-1", "4-1", "5-1", "6-1",
            "1-9", "2-9", "3-9", "4-9", "5-9", "6-9",
            "1-4", "2-4", "1-6", "2-6",
            "3-3", "4-3", "3-7", "4-7",
            "5-6", "6-6", "5-8", "6-8"
    };
    private static final String[] TOP_DOOR_TILE_ID = {"1-6", "3-8", "5-4", "7-3"};
    private static final String[] LEFT_DOOR_TILE_ID = {"1-4", "2-6", "3-1", "3-3", "4-7", "4-9", "6-6", "6-8"};

    // load the board from DB
    public static BoardManager getBoardFromDB() {
        BoardManager myBoardManager = BoardManager.getInstance();

        JSONParser parser = new JSONParser();

        try {

            Object obj = parser.parse(new FileReader(TILE_DB_FILENAME));

            JSONObject jsonObject = (JSONObject) obj;

            int i = 0;
            int j = 0;
            int count = 0;
            // loop array
            JSONArray tilesArr = (JSONArray) jsonObject.get("tiles");
            Iterator<JSONObject> iterator = tilesArr.iterator();
            while (iterator.hasNext()) {

                JSONObject object = iterator.next();

                if (count % 10 == 0 & count != 0){
                    i ++;
                    j = 0;
                }

                // doors
                JSONObject topDoorStatus = (JSONObject) object.get("top_wall_door");
                if (Integer.parseInt("" + topDoorStatus.get("status")) > -1){
                    if (Integer.parseInt("" + topDoorStatus.get("status")) == 0){
                        myBoardManager.addDoor(i, j, Direction.TOP, 2, false);
                    } else if (Integer.parseInt("" + topDoorStatus.get("status")) == 1){
                        myBoardManager.addDoor(i, j, Direction.TOP, 2, true);
                    }
                }

                JSONObject bottomDoorStatus = (JSONObject) object.get("bottom_wall_door");
                if (Integer.parseInt("" + bottomDoorStatus.get("status")) > -1){
                    if (Integer.parseInt("" + bottomDoorStatus.get("status")) == 0){
                        myBoardManager.addDoor(i, j, Direction.BOTTOM, 2, false);
                    } else if (Integer.parseInt("" + bottomDoorStatus.get("status")) == 1){
                        myBoardManager.addDoor(i, j, Direction.BOTTOM, 2, true);
                    }
                }

                JSONObject leftDoorStatus = (JSONObject) object.get("left_wall_door");
                if (Integer.parseInt("" + leftDoorStatus.get("status")) > - 1){
                    if (Integer.parseInt("" + leftDoorStatus.get("status")) == 0){
                        myBoardManager.addDoor(i, j, Direction.LEFT, 2, false);
                    } else if (Integer.parseInt("" + leftDoorStatus.get("status")) == 1){
                        myBoardManager.addDoor(i, j, Direction.LEFT, 2, true);
                    }
                }

                JSONObject rightDoorStatus = (JSONObject) object.get("right_wall_door");
                if (Integer.parseInt("" + rightDoorStatus.get("status")) > - 1){
                    if (Integer.parseInt("" + rightDoorStatus.get("status")) == 0){
                        myBoardManager.addDoor(i, j, Direction.RIGHT, 2, false);
                    } else if (Integer.parseInt("" + rightDoorStatus.get("status")) == 1){
                        myBoardManager.addDoor(i, j, Direction.RIGHT, 2, true);
                    }
                }

                // walls
                if (Integer.parseInt("" + object.get("top_wall")) > -1){
                    myBoardManager.addWall(i, j, Direction.TOP, Integer.parseInt("" + object.get("top_wall")));
                }

                if (Integer.parseInt("" + object.get("bottom_wall")) > -1){
                    myBoardManager.addWall(i, j, Direction.BOTTOM, Integer.parseInt("" + object.get("bottom_wall")));
                }

                if (Integer.parseInt("" + object.get("left_wall")) > -1){
                    myBoardManager.addWall(i, j, Direction.LEFT, Integer.parseInt("" + object.get("left_wall")));
                }

                if (Integer.parseInt("" + object.get("right_wall")) > -1){
                    myBoardManager.addWall(i, j, Direction.RIGHT, Integer.parseInt("" + object.get("right_wall")));
                }

                // firefighters
                JSONArray firefightersArr = (JSONArray) object.get("firefighters");
                if (!firefightersArr.isEmpty()) {
                    Iterator<String> firefighterIter = firefightersArr.iterator();
                    while (firefighterIter.hasNext()) {
                        String firefighterColor = firefighterIter.next();
                        // create a firefighter object by retrieving its data from DB using its unique color id
                        //FireFighter f = getFirefighterFromDB(firefighterColor);
                        myBoardManager.addFireFighter(i, j, getEnumFromString(firefighterColor), 5, 8);
                    }

                }

                // POI

                int POICode = Integer.parseInt("" + object.get("POI"));
                if (POICode > -1){
                    if (POICode == 0) {
                        myBoardManager.addVictim(i, j, false, false, true);
                    } else if (POICode == 1){
                        myBoardManager.addVictim(i, j, false, false, false);
                    }
                }

                String fireStatus = "" + object.get("fire_status");

                if(fireStatus.equals("smoke")){
                    myBoardManager.addFireStatus(i, j, FireStatus.SMOKE);
                } else if (fireStatus.equals("fire")){
                    myBoardManager.addFireStatus(i, j, FireStatus.FIRE);
                }

                j ++;
                count ++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return myBoardManager;
    }

    // save board to DB
    public static void saveBoardToDB (BoardManager boardManager){


        JSONObject newObj = new JSONObject();
        JSONArray newTilesList = new JSONArray();

        JSONParser parser = new JSONParser();

        try {

            Object obj = parser.parse(new FileReader(TILE_DB_FILENAME));
            JSONObject jsonObject = (JSONObject) obj;
            int count = 0;

            JSONArray tilesArr = (JSONArray) jsonObject.get("tiles");
            Iterator<JSONObject> iterator = tilesArr.iterator();
            while (iterator.hasNext()) {

                JSONObject currentTile = (JSONObject) iterator.next();
                int i = count / 10;
                int j = count % 10;

                // walls
                currentTile.put("top_wall", boardManager.getTiles()[i][j].getObstacle(Direction.TOP).getHealth());
                currentTile.put("bottom_wall", boardManager.getTiles()[i][j].getObstacle(Direction.BOTTOM).getHealth());
                currentTile.put("left_wall", boardManager.getTiles()[i][j].getObstacle(Direction.LEFT).getHealth());
                currentTile.put("right_wall", boardManager.getTiles()[i][j].getObstacle(Direction.RIGHT).getHealth());

                // firefighters
                JSONArray newFirefightersList = new JSONArray();
                if (boardManager.getTiles()[i][j].getFirefighters() != null) {
                    for (FireFighter f : boardManager.getTiles()[i][j].getFirefighters()) {
                        newFirefightersList.add(f.getColor());
                    }
                }

                currentTile.put("firefighters", newFirefightersList);

                // POI
                if (boardManager.getTiles()[i][j].hasPointOfInterest()){
                    if (boardManager.getTiles()[i][j].getVictim().isFalseAlarm()){
                        currentTile.put("POI", 0);
                    } else if (!boardManager.getTiles()[i][j].getVictim().isFalseAlarm()){
                        currentTile.put("POI", 1);
                    }
                }

                // fire_status
                if (boardManager.getTiles()[i][j].hasFire()){
                    currentTile.put("fire_status", "fire");
                } else if (boardManager.getTiles()[i][j].hasSmoke()){
                    currentTile.put("fire_status", "smoke");
                } else {
                    currentTile.put("fire_status", "none");
                }

                // top door
                JSONObject topDoorObject = new JSONObject();
                if (boardManager.getTiles()[i][j].getObstacle(Direction.TOP).isDoor()){
                    topDoorObject.put("health", boardManager.getTiles()[i][j].getObstacle(Direction.TOP).getHealth());
                    int status = boardManager.getTiles()[i][j].getObstacle(Direction.TOP).isOpen() ? 1 : 0;
                    topDoorObject.put("status", status);
                } else {
                    topDoorObject.put("health", 2);
                    topDoorObject.put("status", -1);
                }

                currentTile.put("top_wall_door", topDoorObject);

                // bottom door
                JSONObject bottomDoorObject = new JSONObject();
                if (boardManager.getTiles()[i][j].getObstacle(Direction.BOTTOM).isDoor()){
                    bottomDoorObject.put("health", boardManager.getTiles()[i][j].getObstacle(Direction.BOTTOM).getHealth());
                    int status = boardManager.getTiles()[i][j].getObstacle(Direction.BOTTOM).isOpen() ? 1 : 0;
                    bottomDoorObject.put("status", status);
                } else {
                    bottomDoorObject.put("health", 2);
                    bottomDoorObject.put("status", -1);
                }

                currentTile.put("bottom_wall_door", topDoorObject);

                // left door
                JSONObject leftDoorObject = new JSONObject();
                if (boardManager.getTiles()[i][j].getObstacle(Direction.LEFT).isDoor()){
                    leftDoorObject.put("health", boardManager.getTiles()[i][j].getObstacle(Direction.LEFT).getHealth());
                    int status = boardManager.getTiles()[i][j].getObstacle(Direction.LEFT).isOpen() ? 1 : 0;
                    leftDoorObject.put("status", status);
                } else {
                    leftDoorObject.put("health", 2);
                    leftDoorObject.put("status", -1);
                }

                currentTile.put("left_wall_door", topDoorObject);

                // right door
                JSONObject rightDoorObject = new JSONObject();
                if (boardManager.getTiles()[i][j].getObstacle(Direction.RIGHT).isDoor()){
                    rightDoorObject.put("health", boardManager.getTiles()[i][j].getObstacle(Direction.RIGHT).getHealth());
                    int status = boardManager.getTiles()[i][j].getObstacle(Direction.RIGHT).isOpen() ? 1 : 0;
                    rightDoorObject.put("status", status);
                } else {
                    rightDoorObject.put("health", 2);
                    rightDoorObject.put("status", -1);
                }

                currentTile.put("right_wall_door", topDoorObject);

                newTilesList.add(currentTile);
                count ++;

            }

            newObj.put("tiles", newTilesList);

            // use "db/tiles2.json" for test
            FileWriter file = new FileWriter("db/tiles.json");
            file.write(newObj.toJSONString());
            file.flush();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // create a JSON file to save the Tiles
    // walls: [no wall = -1; wall full health = 2; wall one damage = 1; damaged wall = 0]
    // doors: [no door = -1; close = 0; open = 1]
    // POI: [no POI = -1; false alarm = 0; victim = 1]
    // fire status: "none", "smoke", "fire"
    public static void createTilesDBFamilyVersion (){

        JSONObject newObj = new JSONObject();
        JSONArray newTilesList = new JSONArray();

        try {

            int count = 0;

            while (count < 80) {

                int i = count / 10;
                int j = count % 10;

                // modification here
                JSONObject currentTile = new JSONObject();

                currentTile.put("position_id", i + "-" + j);

                // walls
                if (isPresentInArr(TOP_WALL_TILE_ID, i + "-" + j) && !isPresentInArr(TOP_DOOR_TILE_ID, i + "-" + j)){
                    currentTile.put("top_wall", 2);
                } else {
                    currentTile.put("top_wall", -1);
                }

                if (isPresentInArr(LEFT_WALL_TILE_ID, i + "-" + j) && !isPresentInArr(LEFT_DOOR_TILE_ID, i + "-" + j)){
                    currentTile.put("left_wall", 2);
                } else {
                    currentTile.put("left_wall", -1);
                }

                currentTile.put("bottom_wall", -1);
                currentTile.put("right_wall", -1);


                // doors
                JSONObject doorProperties = new JSONObject();
                doorProperties.put("health", 2);
                doorProperties.put("status", -1);

                if (isPresentInArr(TOP_DOOR_TILE_ID, i + "-" + j)){
                    JSONObject openedDoorObject = new JSONObject();
                    openedDoorObject.put("health", 2);
                    openedDoorObject.put("status", 1);
                    currentTile.put("top_wall_door", openedDoorObject);
                } else {
                    currentTile.put("top_wall_door", doorProperties);
                }

                if (isPresentInArr(LEFT_DOOR_TILE_ID, i + "-" + j)){
                    JSONObject openedDoorObject = new JSONObject();
                    openedDoorObject.put("health", 2);
                    openedDoorObject.put("status", 1);
                    currentTile.put("left_wall_door", openedDoorObject);
                } else {
                    currentTile.put("left_wall_door", doorProperties);
                }

                currentTile.put("bottom_wall_door", doorProperties);
                currentTile.put("right_wall_door", doorProperties);


                JSONArray newFirefightersList = new JSONArray();

                currentTile.put("firefighters", newFirefightersList);

                currentTile.put("POI", -1);

                currentTile.put("fire_status", "none");

                newTilesList.add(currentTile);
                count ++;

            }

            newObj.put("tiles", newTilesList);

            // use "db/tiles2.json" for test
            FileWriter file = new FileWriter(TILE_DB_FILENAME);
            file.write(newObj.toJSONString());
            file.flush();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//
//    // load data of a single firefighter from the firefighter.json DB
//    public static FireFighter getFirefighterFromDB(String color) {
//        FireFighterColor enumColor = FireFighterColor.valueOf(color);
//        JSONParser parser = new JSONParser();
//
//        try {
//
//            Object obj = parser.parse(new FileReader("db/firefighters.json"));
//
//            JSONObject jsonObject = (JSONObject) obj;
//
//            // loop array
//            JSONArray firefightersArr = (JSONArray) jsonObject.get("firefighters");
//            Iterator<JSONObject> iterator = firefightersArr.iterator();
//            while (iterator.hasNext()) {
//
//                JSONObject object = iterator.next();
//
//                if (("" + object.get("color_id")).equals(color)){
//                    fireFighter.setActionPointsLeft(Integer.parseInt("" + object.get("AP")));
//                    fireFighter.setNumVictimsSaved(Integer.parseInt("" + object.get("num_victims_saved")));
//                    break;
//                }
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        return fireFighter;
//    }

    private static FireFighterColor getEnumFromString(String color){
        if (color.equals("RED")){
            return FireFighterColor.RED;
        } else if (color.equals("BLUE")){
            return FireFighterColor.BLUE;
        } else if (color.equals("YELLOW")){
            return FireFighterColor.YELLOW;
        } else if (color.equals("WHITE")){
            return FireFighterColor.WHITE;
        } else if (color.equals("ORANGE")){
            return FireFighterColor.ORANGE;
        } else if (color.equals("GREEN")){
            return FireFighterColor.GREEN;
        }
        return null;

    }

    private static boolean isPresentInArr(String[] arr, String str){
        for (int i = 0; i < arr.length; i++){
            if (arr[i].equals(str)){
                return true;
            }
        }

        return false;
    }


}
