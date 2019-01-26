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

    public static BoardManager getBFromDB() {
        BoardManager myBoardManager = BoardManager.getInstance();

        //    myBoardManager.addDoor(0,0, Direction.TOP, 1, false);
        myBoardManager.addDoor(5, 5, Direction.BOTTOM, 1, true);
        myBoardManager.addDoor(5, 5, Direction.LEFT, 1, true);
        // myBoardManager.addFireStatus(1, 1, FireStatus.FIRE);
        myBoardManager.addFireStatus(2, 0, FireStatus.SMOKE);
        myBoardManager.addDoor(1, 0, Direction.RIGHT, 1, true);
        myBoardManager.addFireFighter(1, 0, FireFighterColor.BLUE,0, 3);
        myBoardManager.addVictim(1,1,false, false,true);

        return myBoardManager;
    }

    // load the state of the game
    public static BoardManager getBoardFromDB() {
        BoardManager myBoardManager = BoardManager.getInstance();

        JSONParser parser = new JSONParser();

        try {

            Object obj = parser.parse(new FileReader("db/tiles2.json"));

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
                if (Integer.parseInt("" + topDoorStatus.get("status")) > - 1){
                    if (Integer.parseInt("" + topDoorStatus.get("status")) == 0){
                        myBoardManager.addDoor(i, j, Direction.TOP, 2, false);
                    } else if (Integer.parseInt("" + topDoorStatus.get("status")) == 1){
                        myBoardManager.addDoor(i, j, Direction.TOP, 2, true);
                    }
                }

                JSONObject bottomDoorStatus = (JSONObject) object.get("bottom_wall_door");
                if (Integer.parseInt("" + bottomDoorStatus.get("status")) > - 1){
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

    // create a JSON file to save the Tiles
    // walls: [no wall = -1; wall full health = 2; wall one damage = 1; damaged wall = 0]
    // doors: [no door = -1; close = 0; open = 1]
    // POI: [no POI = -1; victim = 1; false alarm = 1]
    // fire status: "none", "smoke", "fire"
    public static void createTilesDB (){

        JSONObject newObj = new JSONObject();
        JSONArray newTilesList = new JSONArray();

        try {

            int count = 0;

            while (count < 80) {

                // modification here
                JSONObject currentTile = new JSONObject();

                currentTile.put("position_id", (count / 10) + "-" + (count % 10));


                JSONObject doorProperties = new JSONObject();
                doorProperties.put("health", 2);
                doorProperties.put("status", -1);

                currentTile.put("top_wall_door", doorProperties);
                currentTile.put("bottom_wall_door", doorProperties);
                currentTile.put("left_wall_door", doorProperties);
                currentTile.put("right_wall_door", doorProperties);

                currentTile.put("top_wall", -1);
                currentTile.put("bottom_wall", -1);
                currentTile.put("left_wall", -1);
                currentTile.put("right_wall", -1);

                JSONArray newFirefightersList = new JSONArray();

                currentTile.put("firefighters", newFirefightersList);

                currentTile.put("POI", -1);

                currentTile.put("fire_status", "none");

                newTilesList.add(currentTile);
                count ++;

            }

            newObj.put("tiles", newTilesList);

            // use "db/tiles2.json" for test
            FileWriter file = new FileWriter("db/tiles2.json");
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


}
