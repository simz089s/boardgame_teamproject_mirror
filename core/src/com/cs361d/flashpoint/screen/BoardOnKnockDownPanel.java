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
import com.cs361d.flashpoint.manager.BoardManager;
import com.cs361d.flashpoint.model.BoardElements.Direction;

import java.util.ArrayList;

import static com.cs361d.flashpoint.screen.BoardScreen.removeAllPrevFragments;
import static com.cs361d.flashpoint.screen.BoardScreen.stage;
import static com.cs361d.flashpoint.screen.FlashPointScreen.skinUI;

public class BoardOnKnockDownPanel {

    static final int DIRECTION_BUTTON_SIZE = 50;

    static Label label;
    static TextButton btnStayKnockDown;

    static Table directionTable;

    static ArrayList<Table> directionsTableList = new ArrayList<Table>();

    public static void drawOnKnockDownPanel(ArrayList<Direction> directions) {

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
                        label.remove();
                        btnStayKnockDown.remove();
                    }
                });

        stage.addActor(label);
        stage.addActor(btnStayKnockDown);
    }


    private static void drawDirectionsPanelTable(ArrayList<Direction> directions) {

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
                        removeTableDirectionsPanel();
                        return true;
                    }
                });

        btnDirectionD.addListener(
                new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        removeTableDirectionsPanel();
                        return true;
                    }
                });

        btnDirectionCurr.addListener(
                new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        removeTableDirectionsPanel();
                        return true;
                    }
                });

        btnDirectionL.addListener(
                new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        removeTableDirectionsPanel();
                        return true;
                    }
                });

        btnDirectionR.addListener(
                new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
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