package com.cs361d.flashpoint.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;
import com.cs361d.flashpoint.manager.BoardManager;
import com.cs361d.flashpoint.model.BoardElements.Direction;
import com.cs361d.flashpoint.networking.Client;
import com.cs361d.flashpoint.networking.Server;
import com.cs361d.flashpoint.networking.ServerCommands;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.cs361d.flashpoint.screen.BoardScreen.removeAllPrevFragments;
import static com.cs361d.flashpoint.screen.BoardScreen.stage;
import static com.cs361d.flashpoint.screen.FlashPointScreen.skinUI;

public class BoardOnKnockDownPanel {

    static final int DIRECTION_BUTTON_SIZE = 50;

    static Label label;
    static TextButton btnStayKnockDown;

    static Table directionTable;

    static ArrayList<Table> directionsTableList = new ArrayList<Table>();

    public static void drawOnKnockDownPanel(List<Direction> directions) {

        removeAllPrevFragments();

        drawDirectionsPanelTable(directions);

        if (!BoardManager.getInstance().isAdvanced()) {
            return;
        }

        label = new Label("What do you want to do?", skinUI);
        label.setFontScale(1.5f);
        label.setColor(Color.BLACK);
        label.setPosition(
                1000,
                Gdx.graphics.getHeight() - 300);

        btnStayKnockDown = new TextButton("Get knocked down!", skinUI, "default");
        btnStayKnockDown.setWidth(label.getWidth());
        btnStayKnockDown.setHeight(25);
        btnStayKnockDown.setColor(Color.GREEN);
        btnStayKnockDown.setPosition(
                1000,
                Gdx.graphics.getHeight() - 350);

        btnStayKnockDown.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        JSONObject obj = new JSONObject();
                        obj.put("value", true);
                        obj.put("direction", Direction.NODIRECTION.toString());
                        Client.getInstance().sendCommand(ServerCommands.REPLY_KNOWCKED_DOWN_CHOICE, obj.toJSONString());
                        label.remove();
                        btnStayKnockDown.remove();
                    }
                });

        stage.addActor(label);
        stage.addActor(btnStayKnockDown);
    }


    private static void drawDirectionsPanelTable(List<Direction> directions) {

        ImageButton btnDirectionU = new ImageButton(BoardMovesPanel.getTextureForDirectionsTable(Direction.TOP));
        ImageButton btnDirectionD = new ImageButton(BoardMovesPanel.getTextureForDirectionsTable(Direction.BOTTOM));
        ImageButton btnDirectionCurr = new ImageButton(BoardMovesPanel.getTextureForDirectionsTable(Direction.NODIRECTION));
        ImageButton btnDirectionL = new ImageButton(BoardMovesPanel.getTextureForDirectionsTable(Direction.LEFT));
        ImageButton btnDirectionR = new ImageButton(BoardMovesPanel.getTextureForDirectionsTable(Direction.RIGHT));

        directionTable = new Table();

        directionTable.add();

        if (directions.contains(Direction.TOP)) {
            directionTable.add(btnDirectionU).size(DIRECTION_BUTTON_SIZE, DIRECTION_BUTTON_SIZE);
        }
        directionTable.add();

        directionTable.row();
        if (directions.contains(Direction.LEFT)) {
            directionTable.add(btnDirectionL).size(DIRECTION_BUTTON_SIZE, DIRECTION_BUTTON_SIZE);
        }

        directionTable.add();

        if (directions.contains(Direction.RIGHT)) {
            directionTable.add(btnDirectionR).size(DIRECTION_BUTTON_SIZE, DIRECTION_BUTTON_SIZE);
        }

        directionTable.row();

        directionTable.add();

        if (directions.contains(Direction.BOTTOM)) {
            directionTable.add(btnDirectionD).size(DIRECTION_BUTTON_SIZE, DIRECTION_BUTTON_SIZE);
        }
        directionTable.add();

        directionTable.setPosition(1000, Gdx.graphics.getHeight() - directionTable.getHeight() - 550);

    btnDirectionU.addListener(
        new InputListener() {
          @Override
          public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            JSONObject obj = new JSONObject();
            obj.put("value", false);
            obj.put("direction", Direction.TOP.toString());
            Client.getInstance().sendCommand(ServerCommands.REPLY_KNOWCKED_DOWN_CHOICE, obj.toJSONString());
            removeTableDirectionsPanel();
            return true;
          }
        });

        btnDirectionD.addListener(
                new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        JSONObject obj = new JSONObject();
                        obj.put("value", false);
                        obj.put("direction", Direction.BOTTOM.toString());
                        Client.getInstance().sendCommand(ServerCommands.REPLY_KNOWCKED_DOWN_CHOICE, obj.toJSONString());
                        removeTableDirectionsPanel();
                        return true;
                    }
                });


        btnDirectionL.addListener(
                new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        JSONObject obj = new JSONObject();
                        obj.put("value", false);
                        obj.put("direction", Direction.LEFT.toString());
                        Client.getInstance().sendCommand(ServerCommands.REPLY_KNOWCKED_DOWN_CHOICE, obj.toJSONString());
                        removeTableDirectionsPanel();
                        return true;
                    }
                });

        btnDirectionR.addListener(
                new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        JSONObject obj = new JSONObject();
                        obj.put("value", false);
                        obj.put("direction", Direction.RIGHT.toString());
                        Client.getInstance().sendCommand(ServerCommands.REPLY_KNOWCKED_DOWN_CHOICE, obj.toJSONString());
                        removeTableDirectionsPanel();
                        return true;
                    }
                });

        directionsTableList.add(directionTable);
        stage.addActor(directionTable);
    }

    private static void removeTableDirectionsPanel() { //
        for (int i = 0; i < directionsTableList.size(); i++) {
            directionsTableList.get(i).remove();
        }
        directionsTableList.clear();
    }


}