package com.cs361d.flashpoint.controller;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.cs361d.flashpoint.model.BoardElements.*;


public class DBHandler {

    // load the state of the game
    public static Tile[][] getTiles() {

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

                tiles[i][j].setTop_wall(Integer.parseInt("" + object.get("top_wall")));
                tiles[i][j].setBottom_wall(Integer.parseInt("" + object.get("bottom_wall")));
                tiles[i][j].setLeft_wall(Integer.parseInt("" + object.get("left_wall")));
                tiles[i][j].setRight_wall(Integer.parseInt("" + object.get("right_wall")));

                tiles[i][j].setHas_firefighter((String) object.get("has_firefighter"));

                tiles[i][j].setHas_victim((Boolean) object.get("has_victim"));
                tiles[i][j].setHas_false_alarm((Boolean) object.get("has_false_alarm"));

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
    public static void saveGame(Tile[][] tiles){


        JSONObject newObj = new JSONObject();
        JSONArray newList = new JSONArray();

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
                currentTile.put("has_firefighter", tiles[i][j].getHas_firefighter());
                currentTile.put("has_victim", tiles[i][j].isHas_victim());
                currentTile.put("has_false_alarm", tiles[i][j].isHas_false_alarm());
                currentTile.put("has_smoke", tiles[i][j].isHas_smoke());
                currentTile.put("has_fire", tiles[i][j].isHas_fire());
                currentTile.put("has_explosion", tiles[i][j].isHas_explosion());

                newList.add(currentTile);
                count ++;

            }

            newObj.put("tiles", newList);

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


}
