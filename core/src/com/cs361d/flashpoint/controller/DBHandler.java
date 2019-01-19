package com.cs361d.flashpoint.controller;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.cs361d.flashpoint.model.BoardElements.*;


public class DBHandler {

    // load the state of the game
    public static Tile[][] getTilesFromDB() {

        Tile[][] tiles = new Tile[8][10];

        JSONParser parser = new JSONParser();

        try {

            Object obj = parser.parse(new FileReader("db/tiles.json"));

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

                tiles[i][j] = new Tile();

                // wall
                tiles[i][j].setTop_wall(Integer.parseInt("" + object.get("top_wall")));
                tiles[i][j].setBottom_wall(Integer.parseInt("" + object.get("bottom_wall")));
                tiles[i][j].setLeft_wall(Integer.parseInt("" + object.get("left_wall")));
                tiles[i][j].setRight_wall(Integer.parseInt("" + object.get("right_wall")));

                // firefighters
                JSONArray firefightersArr = (JSONArray) object.get("has_firefighter");
                if (firefightersArr.isEmpty()){
                    tiles[i][j].setFirefighters(null);
                } else {

                    ArrayList<FireFighter> firefightersObjArr = new ArrayList<FireFighter>();

                    Iterator<String> firefighterIter = firefightersArr.iterator();
                    while (firefighterIter.hasNext()) {
                        String firefighterColor = firefighterIter.next();
                        // create a firefighter object by retrieving its data from DB using its unique color id
                        FireFighter f = getFirefighterFromDB(firefighterColor);
                        firefightersObjArr.add(f);
                    }

                    tiles[i][j].setFirefighters(firefightersObjArr);
                }

                // POI
                tiles[i][j].setHas_victim((Boolean) object.get("has_victim"));
                tiles[i][j].setHas_false_alarm((Boolean) object.get("has_false_alarm"));

                // dangerous mat
                tiles[i][j].setHas_smoke((Boolean) object.get("has_smoke"));
                tiles[i][j].setHas_fire((Boolean) object.get("has_fire"));
                tiles[i][j].setHas_explosion((Boolean) object.get("has_explosion"));

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

        return tiles;
    }

    // save the state of the game
    public static void saveTilesToDB (Tile[][] tiles){


        JSONObject newObj = new JSONObject();
        JSONArray newTilesList = new JSONArray();

        JSONParser parser = new JSONParser();

        try {

            Object obj = parser.parse(new FileReader("db/tiles.json"));
            JSONObject jsonObject = (JSONObject) obj;
            int count = 0;

            JSONArray tilesArr = (JSONArray) jsonObject.get("tiles");
            Iterator<JSONObject> iterator = tilesArr.iterator();
            while (iterator.hasNext()) {

                // modification here
                JSONObject currentTile = (JSONObject) iterator.next();
                int i = count / 10;
                int j = count % 10;

                currentTile.put("top_wall", tiles[i][j].getTop_wall());
                currentTile.put("bottom_wall", tiles[i][j].getBottom_wall());
                currentTile.put("left_wall", tiles[i][j].getLeft_wall());
                currentTile.put("right_wall", tiles[i][j].getRight_wall());

                JSONArray newFirefightersList = new JSONArray();
                if (tiles[i][j].getFirefighters() != null) {
                    for (FireFighter f : tiles[i][j].getFirefighters()) {
                        newFirefightersList.add(f.getColor());
                    }
                }

                currentTile.put("has_firefighter", newFirefightersList);

                currentTile.put("has_victim", tiles[i][j].isHas_victim());
                currentTile.put("has_false_alarm", tiles[i][j].isHas_false_alarm());

                currentTile.put("has_smoke", tiles[i][j].isHas_smoke());
                currentTile.put("has_fire", tiles[i][j].isHas_fire());
                currentTile.put("has_explosion", tiles[i][j].isHas_explosion());

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

    // load data of a single firefighter from the firefighter.json DB
    public static FireFighter getFirefighterFromDB(String color) {

        FireFighter fireFighter = new FireFighter(color);

        JSONParser parser = new JSONParser();

        try {

            Object obj = parser.parse(new FileReader("db/firefighters.json"));

            JSONObject jsonObject = (JSONObject) obj;

            // loop array
            JSONArray firefightersArr = (JSONArray) jsonObject.get("firefighters");
            Iterator<JSONObject> iterator = firefightersArr.iterator();
            while (iterator.hasNext()) {

                JSONObject object = iterator.next();

                if (("" + object.get("color_id")).equals(color)){
                    fireFighter.setActionPointsLeft(Integer.parseInt("" + object.get("AP")));
                    fireFighter.setStatus("" + object.get("status"));
                    fireFighter.setNumVictimsSaved(Integer.parseInt("" + object.get("num_victims_saved")));
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return fireFighter;
    }


}
