package com.cs361d.flashpoint.manager;


import com.cs361d.flashpoint.model.BoardElements.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class DBHandler {

    // MAP 1
    private static final String[] TOP_WALL_TILE_MAP1 = {"1-1", "1-2", "1-3", "1-4", "1-5", "1-7", "1-8",
            "7-1", "7-2", "7-4", "7-5","7-6", "7-7", "7-8",
            "3-3", "3-4", "3-5", "3-6", "3-7","3-8",
            "5-1", "5-2", "5-3", "5-4", "5-5","5-6", "5-7", "5-8"
    };

    private static final String[] LEFT_WALL_TILE_MAP1 = {"1-1", "2-1", "4-1", "5-1", "6-1",
            "1-9", "2-9", "3-9", "5-9", "6-9",
            "1-4", "2-4", "1-6", "2-6",
            "3-3", "4-3", "3-7", "4-7",
            "5-6", "6-6", "5-8", "6-8"
    };
    private static final String[] TOP_DOOR_TILE_MAP1 = {"3-8", "5-4"};
    private static final String[] LEFT_DOOR_TILE_MAP1 = {"1-4", "2-6", "3-3", "4-7", "6-6", "6-8"};

    private static final String[] TOP_DOOR_TILE_DESTROYED_MAP1 = {"1-6", "7-3"};
    private static final String[] LEFT_DOOR_TILE_DESTROYED_MAP1 = {"3-1", "4-9"};

    private static final String[] AMBULANCE_POS_MAP1 = {"3-0", "4-0", "7-3", "7-4", "0-5", "0-6", "3-9", "4-9"};
    private static final String[] FIRETRUCK_POS_MAP1 = {"1-0", "2-0", "0-7", "0-8", "5-9", "6-9", "7-1", "7-2"};


    // ----------------------------------------------------------------------------------------------------- //


    // MAP 2
    // TODO
    private static final String[] TOP_WALL_TILE_MAP2 = {"1-1", "1-2", "1-3", "1-4", "1-5","1-6", "1-7", "1-8",
            "2-4", "2-5",
            "3-1", "3-2", "4-4", "4-5", "4-6", "4-7", "4-8",
            "5-1", "5-2", "5-3", "5-4", "5-5", "5-6",
            "7-1", "7-2", "7-4", "7-5", "7-6", "7-7", "7-8"
    };
    private static final String[] LEFT_WALL_TILE_MAP2 = {"1-1", "2-1", "4-1", "5-1", "6-1",
            "1-9", "2-9", "3-9", "4-9", "5-9", "6-9",
            "1-4", "2-4", "3-4", "1-6", "2-6", "3-6", "4-7",
            "5-4", "6-4", "5-7", "6-7"
    };
    private static final String[] TOP_DOOR_TILE_MAP2 = {"4-5", "4-6", "5-3", "5-6"};
    private static final String[] LEFT_DOOR_TILE_MAP2 = {"1-6", "3-6", "4-7"};

    private static final String[] TOP_DOOR_TILE_DESTROYED_MAP2 = {"7-3"};
    private static final String[] LEFT_DOOR_TILE_DESTROYED_MAP2 = {"3-1"};

    private static final String[] AMBULANCE_POS_MAP2 = {"2-0", "3-0", "0-5", "0-6", "4-9", "5-9", "7-3", "7-4"};
    private static final String[] FIRETRUCK_POS_MAP2 = {"4-0", "5-0", "0-3", "0-4", "2-9", "3-9", "7-5", "7-6"};



    // load the board from DB



    protected static void loadBoardFromDB(String fileName) {
        BoardManager myBoardManager = BoardManager.getInstance();

        JSONParser parser = new JSONParser();

        try {

            Object obj = parser.parse(new FileReader("db/" + fileName + ".json"));

            JSONObject jsonObject = (JSONObject) obj;

            int i = 0;
            int j = 0;
            int count = 0;

            JSONObject gameParams = (JSONObject) jsonObject.get("gameParams");
            String gameName = "" + gameParams.get("gameName");
            int numVictimsLost = Integer.parseInt("" + gameParams.get("numVictimsLost"));
            int numVictimsSaved = Integer.parseInt("" + gameParams.get("numVictimsSaved"));
            int numFalseAlarmRemoved = Integer.parseInt("" + gameParams.get("numFalseAlarmRemoved"));
            int numDamageLeft = Integer.parseInt("" + gameParams.get("numDamageLeft"));

            JSONArray playersOrderingArr = (JSONArray) gameParams.get("playersOrdering");
            Iterator<JSONObject> playersOrderingIter = playersOrderingArr.iterator();
            ArrayList<FireFighterColor> playersColorOrderArr = new ArrayList<FireFighterColor>();
            while (playersOrderingIter.hasNext()) {
                playersColorOrderArr.add(FireFighterColor.fromString("" + playersOrderingIter.next()));
            }

            int numPlayersNeededToPlay = Integer.parseInt("" + gameParams.get("numPlayersNeededToPlay"));
            int numPlayersLeftToJoin = Integer.parseInt("" + gameParams.get("numPlayersLeftToJoin"));

            BoardManager.getInstance().setGameName(gameName);
            BoardManager.getInstance().setGameAtStart(numFalseAlarmRemoved, numVictimsLost, numVictimsSaved, numDamageLeft);

            BoardManager.getInstance().setTotalPlayerNeeded(numPlayersNeededToPlay);
            BoardManager.getInstance().setNumPlayerLeftToJoin(numPlayersLeftToJoin);

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
                JSONObject topDoor = (JSONObject) object.get("top_wall_door");
                if (Integer.parseInt("" + topDoor.get("status")) > -1){
                    int health = Integer.parseInt("" + topDoor.get("health"));
                    if (Integer.parseInt("" + topDoor.get("status")) == 0){
                        myBoardManager.addDoor(i, j, Direction.TOP, health, false);
                    } else if (Integer.parseInt("" + topDoor.get("status")) == 1){
                        myBoardManager.addDoor(i, j, Direction.TOP, health, true);
                    }
                }

                JSONObject bottomDoor = (JSONObject) object.get("bottom_wall_door");
                if (Integer.parseInt("" + bottomDoor.get("status")) > -1){
                    int health = Integer.parseInt("" + bottomDoor.get("health"));
                    if (Integer.parseInt("" + bottomDoor.get("status")) == 0){
                        myBoardManager.addDoor(i, j, Direction.BOTTOM, health, false);
                    } else if (Integer.parseInt("" + bottomDoor.get("status")) == 1){
                        myBoardManager.addDoor(i, j, Direction.BOTTOM, health, true);
                    }
                }

                JSONObject leftDoor = (JSONObject) object.get("left_wall_door");
                if (Integer.parseInt("" + leftDoor.get("status")) > - 1){
                    int health = Integer.parseInt("" + leftDoor.get("health"));
                    if (Integer.parseInt("" + leftDoor.get("status")) == 0){
                        myBoardManager.addDoor(i, j, Direction.LEFT, health, false);
                    } else if (Integer.parseInt("" + leftDoor.get("status")) == 1){
                        myBoardManager.addDoor(i, j, Direction.LEFT, health, true);
                    }
                }

                JSONObject rightDoor = (JSONObject) object.get("right_wall_door");
                if (Integer.parseInt("" + rightDoor.get("status")) > - 1){
                    int health = Integer.parseInt("" + rightDoor.get("health"));
                    if (Integer.parseInt("" + rightDoor.get("status")) == 0){
                        myBoardManager.addDoor(i, j, Direction.RIGHT, health, false);
                    } else if (Integer.parseInt("" + rightDoor.get("status")) == 1){
                        myBoardManager.addDoor(i, j, Direction.RIGHT, health, true);
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

                // engine
                myBoardManager.setCarrierStatus(i, j, CarrierStatus.fromString("" + object.get("engine")));

                // firefighters
                JSONArray firefightersArr = (JSONArray) object.get("firefighters");
                if (!firefightersArr.isEmpty()) {
                    Iterator<JSONObject> firefighterIter = firefightersArr.iterator();
                    while (firefighterIter.hasNext()) {
                        JSONObject firefighterParams =  firefighterIter.next();
                        FireFighterColor fc = FireFighterColor.fromString("" + firefighterParams.get("color"));
                        int numAP = Integer.parseInt("" + firefighterParams.get("numAP"));
                        myBoardManager.addFireFighter(i, j, fc, numAP);
                    }
                }

                // POI

                JSONObject pointOfInterest = (JSONObject) object.get("POI");
                int POIStatus = Integer.parseInt("" + pointOfInterest.get("status"));
                if (POIStatus > -1){
                    if ((Boolean) pointOfInterest.get("revealed")){
                        if (POIStatus == 0) {
                            myBoardManager.addVictim(i, j, true, false, true);
                        } else if (POIStatus == 1){
                            myBoardManager.addVictim(i, j, true, false, false);
                        }
                    } else {
                        if (POIStatus == 0) {
                            myBoardManager.addVictim(i, j, false, false, true);
                        } else if (POIStatus == 1){
                            myBoardManager.addVictim(i, j, false, false, false);
                        }
                    }
                }

                myBoardManager.addFireStatus(i, j, FireStatus.fromString("" + object.get("fire_status")));

                j ++;
                count ++;
            }

            FireFighterTurnManager.getInstance().setOrder(playersColorOrderArr);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }



    // save board to DB



    public static void saveBoardToDB (String fileName){

        BoardManager boardManager = BoardManager.getInstance();

        JSONObject newObj = new JSONObject();
        JSONArray newTilesList = new JSONArray();

        JSONObject gameParams = new JSONObject();
        JSONArray playersOrdering = new JSONArray();
        Iterator<FireFighter> it = FireFighterTurnManager.getInstance().iterator();
        while(it.hasNext()) {
            FireFighter f = it.next();
            playersOrdering.add("" + f.getColor());
        }

        gameParams.put("gameName", fileName);
        gameParams.put("numVictimsLost", BoardManager.getInstance().getNumVictimDead());
        gameParams.put("numVictimsSaved", BoardManager.getInstance().getNumVictimSaved());
        gameParams.put("numFalseAlarmRemoved", BoardManager.getInstance().getNumFalseAlarmRemoved());
        gameParams.put("numDamageLeft", BoardManager.getInstance().getTotalWallDamageLeft());

        gameParams.put("playersOrdering", playersOrdering);
        gameParams.put("numPlayersNeededToPlay", BoardManager.getInstance().getTotalPlayer());
        gameParams.put("numPlayersLeftToJoin", BoardManager.getInstance().getNumPlayerLeftToJoin());

        try {

            int count = 0;

            while (count < 80) {

                int i = count / 10;
                int j = count % 10;

                JSONObject currentTile = new JSONObject();

                currentTile.put("position_id", i + "-" + j);

                // walls
                currentTile.put("top_wall", boardManager.getTiles()[i][j].getObstacle(Direction.TOP).getHealth());
                currentTile.put("bottom_wall", boardManager.getTiles()[i][j].getObstacle(Direction.BOTTOM).getHealth());
                currentTile.put("left_wall", boardManager.getTiles()[i][j].getObstacle(Direction.LEFT).getHealth());
                currentTile.put("right_wall", boardManager.getTiles()[i][j].getObstacle(Direction.RIGHT).getHealth());

                // firefighters
                JSONArray newFirefightersList = new JSONArray();
                if (boardManager.getTiles()[i][j].getFirefighters() != null) {
                    for (FireFighter f : boardManager.getTiles()[i][j].getFirefighters()) {
                        JSONObject firefighterParams = new JSONObject();
                        firefighterParams.put("color", f.getColor().toString());
                        firefighterParams.put("numAP", f.getActionPointsLeft());
                        newFirefightersList.add(firefighterParams);
                    }
                }

                currentTile.put("firefighters", newFirefightersList);

                // POI
                JSONObject pointOfInterestObj = new JSONObject();
                if (boardManager.getTiles()[i][j].hasPointOfInterest()){
                    if (boardManager.getTiles()[i][j].getVictim().isRevealed()) {
                        pointOfInterestObj.put("revealed", true);
                    } else {
                        pointOfInterestObj.put("revealed", false);
                    }

                    if (boardManager.getTiles()[i][j].getVictim().isFalseAlarm()){
                        pointOfInterestObj.put("status", 0);
                    } else if (!boardManager.getTiles()[i][j].getVictim().isFalseAlarm()){
                        pointOfInterestObj.put("status", 1);
                    }

                    currentTile.put("POI", pointOfInterestObj);
                } else {
                    pointOfInterestObj.put("revealed", false);
                    pointOfInterestObj.put("status", -1);
                    currentTile.put("POI", pointOfInterestObj);
                }

                // fire_status
                String fireStatus = boardManager.getTiles()[i][j].getFireStatusString();
                currentTile.put("fire_status", fireStatus);

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

                currentTile.put("bottom_wall_door", bottomDoorObject);

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

                currentTile.put("left_wall_door", leftDoorObject);

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

                currentTile.put("right_wall_door", rightDoorObject);


                // engine
                String carrier = boardManager.getTiles()[i][j].getCarrierStatusString();
                currentTile.put("engine", carrier);

                newTilesList.add(currentTile);
                count ++;

            }

            newObj.put("gameParams", gameParams);
            newObj.put("tiles", newTilesList);

            // create save board json file

            FileWriter file = new FileWriter("db/" + fileName + ".json");
            file.write(newObj.toJSONString());
            file.flush();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    // Create board with only walls and doors
    // call this right after show() method declaration in BoardScreen.java



    // create a JSON file to save the Tiles
    // walls: [no wall = -1; wall full health = 2; wall one damage = 1; damaged wall = 0]
    // doors status: [no door = -1; close = 0; open = 1]
    // POI status: [no POI = -1; false alarm = 0; victim = 1]
    // fire status: "none", "smoke", "fire"

    public static void createBoard(MapKind mk){
        if(mk == MapKind.MAP1){
            createMap1Board();
        } else if (mk == MapKind.MAP2){
            createMap2Board();
        } else if (mk == MapKind.RANDOM){

        }
    }


    protected static void createMap1Board(){

        JSONObject newObj = new JSONObject();
        JSONArray newTilesList = new JSONArray();

        JSONObject gameParams = new JSONObject();
        JSONArray playersOrdering = new JSONArray();
        gameParams.put("gameName", MapKind.MAP1.getText());
        gameParams.put("numVictimsLost", 0);
        gameParams.put("numVictimsSaved", 0);
        gameParams.put("numFalseAlarmRemoved", 0);
        gameParams.put("numDamageLeft", 24);

        gameParams.put("playersOrdering", playersOrdering);
        gameParams.put("numPlayersNeededToPlay", 3);
        gameParams.put("numPlayersLeftToJoin", 3);

        try {

            int count = 0;

            while (count < 80) {

                int i = count / 10;
                int j = count % 10;

                // modification here
                JSONObject currentTile = new JSONObject();

                currentTile.put("position_id", i + "-" + j);

                // walls
                if (isPresentInArr(TOP_WALL_TILE_MAP1, i + "-" + j) && !isPresentInArr(TOP_DOOR_TILE_MAP1, i + "-" + j)){
                    currentTile.put("top_wall", 2);
                } else {
                    currentTile.put("top_wall", -1);
                }

                if (isPresentInArr(LEFT_WALL_TILE_MAP1, i + "-" + j) && !isPresentInArr(LEFT_DOOR_TILE_MAP1, i + "-" + j)){
                    currentTile.put("left_wall", 2);
                } else {
                    currentTile.put("left_wall", -1);
                }

                currentTile.put("bottom_wall", -1);
                currentTile.put("right_wall", -1);


                // doors
                JSONObject doorProperties = new JSONObject();
                doorProperties.put("health", 1);
                doorProperties.put("status", -1);

                if (isPresentInArr(TOP_DOOR_TILE_MAP1, i + "-" + j)){
                    JSONObject openedDoorObject = new JSONObject();
                    openedDoorObject.put("health", 1);
                    openedDoorObject.put("status", 0);
                    currentTile.put("top_wall_door", openedDoorObject);
                } else if (isPresentInArr(TOP_DOOR_TILE_DESTROYED_MAP1, i + "-" + j)){
                    JSONObject openedDoorObject = new JSONObject();
                    openedDoorObject.put("health", 0);
                    openedDoorObject.put("status", 1);
                    currentTile.put("top_wall_door", openedDoorObject);
                } else {
                    currentTile.put("top_wall_door", doorProperties);
                }

                if (isPresentInArr(LEFT_DOOR_TILE_MAP1, i + "-" + j)){
                    JSONObject openedDoorObject = new JSONObject();
                    openedDoorObject.put("health", 1);
                    openedDoorObject.put("status", 0);
                    currentTile.put("left_wall_door", openedDoorObject);
                } else if(isPresentInArr(LEFT_DOOR_TILE_DESTROYED_MAP1, i + "-" + j)){
                    JSONObject openedDoorObject = new JSONObject();
                    openedDoorObject.put("health", 0);
                    openedDoorObject.put("status", 1);
                    currentTile.put("left_wall_door", openedDoorObject);
                } else {
                    currentTile.put("left_wall_door", doorProperties);
                }


                currentTile.put("bottom_wall_door", doorProperties);
                currentTile.put("right_wall_door", doorProperties);

                // engines
                if (isPresentInArr(AMBULANCE_POS_MAP1, i + "-" + j)){
                    currentTile.put("engine", "canhaveambulance");
                }

                if (isPresentInArr(FIRETRUCK_POS_MAP1, i + "-" + j)){
                    currentTile.put("engine", "canhavefiretruck");
                }

                if (!isPresentInArr(AMBULANCE_POS_MAP1, i + "-" + j) && !isPresentInArr(FIRETRUCK_POS_MAP1, i + "-" + j)){
                    currentTile.put("engine", "empty");
                }

                // firefighters
                JSONArray newFirefightersList = new JSONArray();
                currentTile.put("firefighters", newFirefightersList);


                // POI

                JSONObject pointOfInterest = new JSONObject();
                pointOfInterest.put("revealed", false);
                pointOfInterest.put("status", -1);
                currentTile.put("POI", pointOfInterest);


                // Fire status

                currentTile.put("fire_status", "empty");

                newTilesList.add(currentTile);
                count ++;

            }


            newObj.put("gameParams", gameParams);
            newObj.put("tiles", newTilesList);

            // create empty board json file

            FileWriter file = new FileWriter("db/" + MapKind.MAP1.getText() + ".json");
            file.write(newObj.toJSONString());
            file.flush();


            loadBoardFromDB(MapKind.MAP1.getText());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void createMap2Board(){

        JSONObject newObj = new JSONObject();
        JSONArray newTilesList = new JSONArray();

        JSONObject gameParams = new JSONObject();
        JSONArray playersOrdering = new JSONArray();
        gameParams.put("gameName", MapKind.MAP2.getText());
        gameParams.put("numVictimsLost", 0);
        gameParams.put("numVictimsSaved", 0);
        gameParams.put("numFalseAlarmRemoved", 0);
        gameParams.put("numDamageLeft", 24);

        gameParams.put("playersOrdering", playersOrdering);
        gameParams.put("numPlayersNeededToPlay", 3);
        gameParams.put("numPlayersLeftToJoin", 3);

        try {

            int count = 0;

            while (count < 80) {

                int i = count / 10;
                int j = count % 10;

                // modification here
                JSONObject currentTile = new JSONObject();

                currentTile.put("position_id", i + "-" + j);

                // walls
                if (isPresentInArr(TOP_WALL_TILE_MAP2, i + "-" + j) && !isPresentInArr(TOP_DOOR_TILE_MAP2, i + "-" + j)){
                    currentTile.put("top_wall", 2);
                } else {
                    currentTile.put("top_wall", -1);
                }

                if (isPresentInArr(LEFT_WALL_TILE_MAP2, i + "-" + j) && !isPresentInArr(LEFT_DOOR_TILE_MAP2, i + "-" + j)){
                    currentTile.put("left_wall", 2);
                } else {
                    currentTile.put("left_wall", -1);
                }

                currentTile.put("bottom_wall", -1);
                currentTile.put("right_wall", -1);


                // doors
                JSONObject doorProperties = new JSONObject();
                doorProperties.put("health", 1);
                doorProperties.put("status", -1);

                if (isPresentInArr(TOP_DOOR_TILE_MAP2, i + "-" + j)){
                    JSONObject openedDoorObject = new JSONObject();
                    openedDoorObject.put("health", 1);
                    openedDoorObject.put("status", 0);
                    currentTile.put("top_wall_door", openedDoorObject);
                } else if (isPresentInArr(TOP_DOOR_TILE_DESTROYED_MAP2, i + "-" + j)){
                    JSONObject openedDoorObject = new JSONObject();
                    openedDoorObject.put("health", 0);
                    openedDoorObject.put("status", 1);
                    currentTile.put("top_wall_door", openedDoorObject);
                } else {
                    currentTile.put("top_wall_door", doorProperties);
                }

                if (isPresentInArr(LEFT_DOOR_TILE_MAP2, i + "-" + j)){
                    JSONObject openedDoorObject = new JSONObject();
                    openedDoorObject.put("health", 1);
                    openedDoorObject.put("status", 0);
                    currentTile.put("left_wall_door", openedDoorObject);
                } else if(isPresentInArr(LEFT_DOOR_TILE_DESTROYED_MAP2, i + "-" + j)){
                    JSONObject openedDoorObject = new JSONObject();
                    openedDoorObject.put("health", 0);
                    openedDoorObject.put("status", 1);
                    currentTile.put("left_wall_door", openedDoorObject);
                } else {
                    currentTile.put("left_wall_door", doorProperties);
                }


                currentTile.put("bottom_wall_door", doorProperties);
                currentTile.put("right_wall_door", doorProperties);

                // engines
                if (isPresentInArr(AMBULANCE_POS_MAP2, i + "-" + j)){
                    currentTile.put("engine", "canhaveambulance");
                }

                if (isPresentInArr(FIRETRUCK_POS_MAP2, i + "-" + j)){
                    currentTile.put("engine", "canhavefiretruck");
                }

                if (!isPresentInArr(AMBULANCE_POS_MAP2, i + "-" + j) && !isPresentInArr(FIRETRUCK_POS_MAP2, i + "-" + j)){
                    currentTile.put("engine", "empty");
                }

                // firefighters
                JSONArray newFirefightersList = new JSONArray();
                currentTile.put("firefighters", newFirefightersList);


                // POI

                JSONObject pointOfInterest = new JSONObject();
                pointOfInterest.put("revealed", false);
                pointOfInterest.put("status", -1);
                currentTile.put("POI", pointOfInterest);


                // Fire status

                currentTile.put("fire_status", "empty");

                newTilesList.add(currentTile);
                count ++;

            }


            newObj.put("gameParams", gameParams);
            newObj.put("tiles", newTilesList);

            // create empty board json file

            FileWriter file = new FileWriter("db/" + MapKind.MAP2.getText() + ".json");
            file.write(newObj.toJSONString());
            file.flush();


            loadBoardFromDB(MapKind.MAP2.getText());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected static void removeGameFile(String filename){
        if (filename.equalsIgnoreCase("map1") ||filename.equalsIgnoreCase("map1") ) {
            throw new IllegalArgumentException("Cannot delete the main maps from db");
        }
        File file = new File("db/" + filename + ".json");
        file.delete();
    }






    // network





    protected static void loadBoardFromString(String jsonString) {
        BoardManager myBoardManager = BoardManager.getInstance();

        JSONParser parser = new JSONParser();

        try {

            JSONObject jsonObject = (JSONObject) parser.parse(jsonString);

            int i = 0;
            int j = 0;
            int count = 0;

            JSONObject gameParams = (JSONObject) jsonObject.get("gameParams");
            String gameName = "" + gameParams.get("gameName");
            int numVictimsLost = Integer.parseInt("" + gameParams.get("numVictimsLost"));
            int numVictimsSaved = Integer.parseInt("" + gameParams.get("numVictimsSaved"));
            int numFalseAlarmRemoved = Integer.parseInt("" + gameParams.get("numFalseAlarmRemoved"));
            int numDamageLeft = Integer.parseInt("" + gameParams.get("numDamageLeft"));

            JSONArray playersOrderingArr = (JSONArray) gameParams.get("playersOrdering");
            Iterator<JSONObject> playersOrderingIter = playersOrderingArr.iterator();
            ArrayList<FireFighterColor> playersColorOrderArr = new ArrayList<FireFighterColor>();
            while (playersOrderingIter.hasNext()) {
                playersColorOrderArr.add(FireFighterColor.fromString("" + playersOrderingIter.next()));
            }

            int numPlayersNeededToPlay = Integer.parseInt("" + gameParams.get("numPlayersNeededToPlay"));
            int numPlayersLeftToJoin = Integer.parseInt("" + gameParams.get("numPlayersLeftToJoin"));

            BoardManager.getInstance().setGameName(gameName);
            BoardManager.getInstance().setGameAtStart(numFalseAlarmRemoved, numVictimsLost, numVictimsSaved, numDamageLeft);

            BoardManager.getInstance().setTotalPlayerNeeded(numPlayersNeededToPlay);
            BoardManager.getInstance().setNumPlayerLeftToJoin(numPlayersLeftToJoin);

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
                JSONObject topDoor = (JSONObject) object.get("top_wall_door");
                if (Integer.parseInt("" + topDoor.get("status")) > -1){
                    int health = Integer.parseInt("" + topDoor.get("health"));
                    if (Integer.parseInt("" + topDoor.get("status")) == 0){
                        myBoardManager.addDoor(i, j, Direction.TOP, health, false);
                    } else if (Integer.parseInt("" + topDoor.get("status")) == 1){
                        myBoardManager.addDoor(i, j, Direction.TOP, health, true);
                    }
                }

                JSONObject bottomDoor = (JSONObject) object.get("bottom_wall_door");
                if (Integer.parseInt("" + bottomDoor.get("status")) > -1){
                    int health = Integer.parseInt("" + bottomDoor.get("health"));
                    if (Integer.parseInt("" + bottomDoor.get("status")) == 0){
                        myBoardManager.addDoor(i, j, Direction.BOTTOM, health, false);
                    } else if (Integer.parseInt("" + bottomDoor.get("status")) == 1){
                        myBoardManager.addDoor(i, j, Direction.BOTTOM, health, true);
                    }
                }

                JSONObject leftDoor = (JSONObject) object.get("left_wall_door");
                if (Integer.parseInt("" + leftDoor.get("status")) > - 1){
                    int health = Integer.parseInt("" + leftDoor.get("health"));
                    if (Integer.parseInt("" + leftDoor.get("status")) == 0){
                        myBoardManager.addDoor(i, j, Direction.LEFT, health, false);
                    } else if (Integer.parseInt("" + leftDoor.get("status")) == 1){
                        myBoardManager.addDoor(i, j, Direction.LEFT, health, true);
                    }
                }

                JSONObject rightDoor = (JSONObject) object.get("right_wall_door");
                if (Integer.parseInt("" + rightDoor.get("status")) > - 1){
                    int health = Integer.parseInt("" + rightDoor.get("health"));
                    if (Integer.parseInt("" + rightDoor.get("status")) == 0){
                        myBoardManager.addDoor(i, j, Direction.RIGHT, health, false);
                    } else if (Integer.parseInt("" + rightDoor.get("status")) == 1){
                        myBoardManager.addDoor(i, j, Direction.RIGHT, health, true);
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

                // engine
                myBoardManager.setCarrierStatus(i, j, CarrierStatus.fromString("" + object.get("engine")));

                // firefighters
                JSONArray firefightersArr = (JSONArray) object.get("firefighters");
                if (!firefightersArr.isEmpty()) {
                    Iterator<JSONObject> firefighterIter = firefightersArr.iterator();
                    while (firefighterIter.hasNext()) {
                        JSONObject firefighterParams =  firefighterIter.next();
                        FireFighterColor fc = FireFighterColor.fromString("" + firefighterParams.get("color"));
                        int numAP = Integer.parseInt("" + firefighterParams.get("numAP"));
                        myBoardManager.addFireFighter(i, j, fc, numAP);
                    }
                }

                // POI

                JSONObject pointOfInterest = (JSONObject) object.get("POI");
                int POIStatus = Integer.parseInt("" + pointOfInterest.get("status"));
                if (POIStatus > -1){
                    if ((Boolean) pointOfInterest.get("revealed")){
                        if (POIStatus == 0) {
                            myBoardManager.addVictim(i, j, true, false, true);
                        } else if (POIStatus == 1){
                            myBoardManager.addVictim(i, j, true, false, false);
                        }
                    } else {
                        if (POIStatus == 0) {
                            myBoardManager.addVictim(i, j, false, false, true);
                        } else if (POIStatus == 1){
                            myBoardManager.addVictim(i, j, false, false, false);
                        }
                    }
                }

                myBoardManager.addFireStatus(i, j, FireStatus.fromString("" + object.get("fire_status")));

                j ++;
                count ++;
            }

            FireFighterTurnManager.getInstance().setOrder(playersColorOrderArr);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static String getBoardAsString (){

        BoardManager boardManager = BoardManager.getInstance();

        JSONObject newObj = new JSONObject();
        JSONArray newTilesList = new JSONArray();

        JSONObject gameParams = new JSONObject();
        JSONArray playersOrdering = new JSONArray();
        Iterator<FireFighter> it = FireFighterTurnManager.getInstance().iterator();
        while(it.hasNext()) {
            FireFighter f = it.next();
            playersOrdering.add("" + f.getColor());
        }

        gameParams.put("gameName", boardManager.getInstance().getGameName());
        gameParams.put("numVictimsLost", BoardManager.getInstance().getNumVictimDead());
        gameParams.put("numVictimsSaved", BoardManager.getInstance().getNumVictimSaved());
        gameParams.put("numFalseAlarmRemoved", BoardManager.getInstance().getNumFalseAlarmRemoved());
        gameParams.put("numDamageLeft", BoardManager.getInstance().getTotalWallDamageLeft());

        gameParams.put("playersOrdering", playersOrdering);
        gameParams.put("numPlayersNeededToPlay", BoardManager.getInstance().getTotalPlayer());
        gameParams.put("numPlayersLeftToJoin", BoardManager.getInstance().getNumPlayerLeftToJoin());


        int count = 0;

        while (count < 80) {

            int i = count / 10;
            int j = count % 10;

            JSONObject currentTile = new JSONObject();

            currentTile.put("position_id", i + "-" + j);

            // walls
            currentTile.put("top_wall", boardManager.getTiles()[i][j].getObstacle(Direction.TOP).getHealth());
            currentTile.put("bottom_wall", boardManager.getTiles()[i][j].getObstacle(Direction.BOTTOM).getHealth());
            currentTile.put("left_wall", boardManager.getTiles()[i][j].getObstacle(Direction.LEFT).getHealth());
            currentTile.put("right_wall", boardManager.getTiles()[i][j].getObstacle(Direction.RIGHT).getHealth());

            // firefighters
            JSONArray newFirefightersList = new JSONArray();
            if (boardManager.getTiles()[i][j].getFirefighters() != null) {
                for (FireFighter f : boardManager.getTiles()[i][j].getFirefighters()) {
                    JSONObject firefighterParams = new JSONObject();
                    firefighterParams.put("color", f.getColor().toString());
                    firefighterParams.put("numAP", f.getActionPointsLeft());
                    newFirefightersList.add(firefighterParams);
                }
            }

            currentTile.put("firefighters", newFirefightersList);

            // POI
            JSONObject pointOfInterestObj = new JSONObject();
            if (boardManager.getTiles()[i][j].hasPointOfInterest()){
                if (boardManager.getTiles()[i][j].getVictim().isRevealed()) {
                    pointOfInterestObj.put("revealed", true);
                } else {
                    pointOfInterestObj.put("revealed", false);
                }

                if (boardManager.getTiles()[i][j].getVictim().isFalseAlarm()){
                    pointOfInterestObj.put("status", 0);
                } else if (!boardManager.getTiles()[i][j].getVictim().isFalseAlarm()){
                    pointOfInterestObj.put("status", 1);
                }

                currentTile.put("POI", pointOfInterestObj);
            } else {
                pointOfInterestObj.put("revealed", false);
                pointOfInterestObj.put("status", -1);
                currentTile.put("POI", pointOfInterestObj);
            }

            // fire_status
            String fireStatus = boardManager.getTiles()[i][j].getFireStatusString();
            currentTile.put("fire_status", fireStatus);

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

            currentTile.put("bottom_wall_door", bottomDoorObject);

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

            currentTile.put("left_wall_door", leftDoorObject);

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

            currentTile.put("right_wall_door", rightDoorObject);


            // engine
            String carrier = boardManager.getTiles()[i][j].getCarrierStatusString();
            currentTile.put("engine", carrier);

            newTilesList.add(currentTile);
            count ++;

        }

        newObj.put("gameParams", gameParams);
        newObj.put("tiles", newTilesList);


        return newObj.toJSONString();
    }


    // helper




    public static boolean isPresentInArr(String[] arr, String str){
        for (int i = 0; i < arr.length; i++){
            if (arr[i].equals(str)){
                return true;
            }
        }

        return false;
    }


}
