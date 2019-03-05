package com.cs361d.flashpoint.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.cs361d.flashpoint.manager.BoardManager;
import com.cs361d.flashpoint.manager.DBHandler;
import com.cs361d.flashpoint.manager.FireFighterTurnManager;
import com.cs361d.flashpoint.model.BoardElements.Direction;

import java.util.ArrayList;

import static com.cs361d.flashpoint.view.BoardScreen.*;

public class BoardMovesPanel {

    ScrollPane scrollPaneMoveOptions;
    ScrollPane.ScrollPaneStyle scrollStyle;
    List<String> lstMoveOptions;
    List.ListStyle listStyleMoveOptions;

    ScrollPane scrollPaneMoveDirections;
    ScrollPane.ScrollPaneStyle scrollStyleDirections;
    List<String> lstMoveDirections;
    List.ListStyle listStyleDirections;

    Stage stage;
    FireFighterTurnManager fireFighterTurnManager = FireFighterTurnManager.getInstance();

    ArrayList<ScrollPane> directionsList = new ArrayList<ScrollPane>();
    ArrayList<ScrollPane> movesList = new ArrayList<ScrollPane>();

    // constructor
    public BoardMovesPanel(Stage stage){
        this.stage = stage;
    }

    private void performDirectionMove(String move, Direction direction){
        if (move.equals("MOVE")){
            clearAllGameUnits();
            fireFighterTurnManager.move(direction);
            redrawGameUnitsOnTile();
        } else if (move.equals("EXTINGUISH")){
            clearAllGameUnits();
            fireFighterTurnManager.extinguishFire(direction);
            redrawGameUnitsOnTile();
        } else if (move.equals("CHOP")){
            clearAllGameUnits();
            fireFighterTurnManager.chopWall(direction);
            redrawGameUnitsOnTile();
        } else if (move.equals("MOVE WITH VICTIM")){
            clearAllGameUnits();
            fireFighterTurnManager.moveWithVictim(direction);
            redrawGameUnitsOnTile();
        } else if (move.equals("INTERACT WITH DOOR")){
            clearAllGameUnits();
            fireFighterTurnManager.interactWithDoor(direction);
            redrawGameUnitsOnTile();
        }
    }

    private String[] getMovesArrForDisplay(){
        String[] MOVES_ARR = {
                "MOVE", "EXTINGUISH", "CHOP", "MOVE WITH VICTIM", "INTERACT WITH DOOR", "END TURN", "SAVE"
        };

        // TODO: MOVES_ARR = fireFighterTurnManager.getAvailableMoves();

        return MOVES_ARR;
    }

    private String[] getDirectionArrForDisplay(String move){
        String[] directionArr = {"UP", "DOWN", "LEFT", "RIGHT", "CANCEL"};
        if (move.equals("EXTINGUISH")){
            String[] extinguishDirArr = {"CURRENT TILE", "UP", "DOWN", "LEFT", "RIGHT", "CANCEL"};
            return extinguishDirArr;
        } else{
            return directionArr;
        }
    }

    public void createMovesAndDirectionsPanel() {
        // list style
        listStyleMoveOptions = new List.ListStyle();
        listStyleMoveOptions.font = Font.get(25); // font size
        listStyleMoveOptions.fontColorUnselected = Color.BLACK;
        listStyleMoveOptions.fontColorSelected = Color.BLACK;
        listStyleMoveOptions.selection = TextureLoader.getDrawable(50, 100, Color.SKY );

        lstMoveOptions = new List<String>(listStyleMoveOptions);
        lstMoveOptions.setItems(getMovesArrForDisplay());

        // scrollPane style
        scrollStyle = new ScrollPane.ScrollPaneStyle();
        scrollStyle.vScrollKnob = TextureLoader.getDrawable(15, 15, Color.DARK_GRAY);
        scrollStyle.vScroll = TextureLoader.getDrawable(15, 15, Color.LIGHT_GRAY);

        scrollPaneMoveOptions = new ScrollPane(lstMoveOptions, scrollStyle);
        scrollPaneMoveOptions.setOverscroll(false, false);
        scrollPaneMoveOptions.setFadeScrollBars(false);
        scrollPaneMoveOptions.setScrollingDisabled(true, false);
        scrollPaneMoveOptions.setTransform(true);
        scrollPaneMoveOptions.setScale(1.0f);
        scrollPaneMoveOptions.setWidth(300);
        scrollPaneMoveOptions.setHeight(200);
        //scrollMessage.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() + 100);
        scrollPaneMoveOptions.setPosition(
                850,
                Gdx.graphics.getHeight() - scrollPaneMoveOptions.getHeight() - 150);

        lstMoveOptions.addListener(new InputListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {

                String moveSelected = lstMoveOptions.getSelected();

                if (moveSelected.equals("MOVE")) {
                    removeDirectionsPanel();
                    createDirectionsPanel(moveSelected);
                    stage.addActor(scrollPaneMoveDirections);

                } else if (moveSelected.equals("EXTINGUISH")) {
                    removeDirectionsPanel();
                    createDirectionsPanel(moveSelected);
                    stage.addActor(scrollPaneMoveDirections);

                } else if (moveSelected.equals("CHOP")) {
                    removeDirectionsPanel();
                    createDirectionsPanel(moveSelected);
                    stage.addActor(scrollPaneMoveDirections);

                } else if (moveSelected.equals("MOVE WITH VICTIM")) {
                    removeDirectionsPanel();
                    createDirectionsPanel(moveSelected);
                    stage.addActor(scrollPaneMoveDirections);

                } else if (moveSelected.equals("INTERACT WITH DOOR")) {
                    removeDirectionsPanel();
                    createDirectionsPanel(moveSelected);
                    stage.addActor(scrollPaneMoveDirections);

                } else if (moveSelected.equals("END TURN")) {
                    clearAllGameUnits();
                    try {
                        fireFighterTurnManager.endTurn();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                    redrawGameUnitsOnTile();
                    gameInfoLabel.setColor(Color.BLACK);
                    updateGameInfoLabel();

                }  else if (moveSelected.equals("SAVE")) {
                    DBHandler.saveBoardToDB(BoardManager.getInstance());
                      createDialog("Save", "Your game has been successfully saved.");
                }  else {
                    //debugLbl.setText("failed action");
                }

                return true;
            }
        });

        movesList.add(scrollPaneMoveOptions);

        stage.addActor(scrollPaneMoveOptions);
    }

    private void createDirectionsPanel(String moveSelected){

        final String MOVE = moveSelected;

        // list style
        listStyleDirections = new List.ListStyle();
        listStyleDirections.font = Font.get(25); // font size
        listStyleDirections.fontColorUnselected = Color.BLACK;
        listStyleDirections.fontColorSelected = Color.BLACK;
        listStyleDirections.selection = TextureLoader.getDrawable(50, 100, Color.YELLOW);

        lstMoveDirections = new List<String>(listStyleDirections);
        lstMoveDirections.setItems(getDirectionArrForDisplay(MOVE));

        // scrollPane style
        scrollStyleDirections = new ScrollPane.ScrollPaneStyle();
        scrollStyleDirections.vScrollKnob = TextureLoader.getDrawable(15, 15, Color.DARK_GRAY);
        scrollStyleDirections.vScroll = TextureLoader.getDrawable(15, 15, Color.LIGHT_GRAY);

        scrollPaneMoveDirections = new ScrollPane(lstMoveDirections, scrollStyleDirections);
        scrollPaneMoveDirections.setOverscroll(false, false);
        scrollPaneMoveDirections.setFadeScrollBars(false);
        scrollPaneMoveDirections.setScrollingDisabled(true, false);
        scrollPaneMoveDirections.setTransform(true);
        scrollPaneMoveDirections.setScale(1.0f);
        scrollPaneMoveDirections.setWidth(300);
        scrollPaneMoveDirections.setHeight(200);
        scrollPaneMoveDirections.setPosition(
                850,
                Gdx.graphics.getHeight() - scrollPaneMoveDirections.getHeight() - 400);

        lstMoveDirections.addListener(new InputListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {

                if (lstMoveDirections.getSelected().equals("CURRENT TILE")) {
                    performDirectionMove(MOVE, Direction.NODIRECTION);
                } else if (lstMoveDirections.getSelected().equals("UP")){
                    performDirectionMove(MOVE, Direction.TOP);
                } else if (lstMoveDirections.getSelected().equals("DOWN")) {
                    performDirectionMove(MOVE, Direction.BOTTOM);
                } else if (lstMoveDirections.getSelected().equals("LEFT")) {
                    performDirectionMove(MOVE, Direction.LEFT);
                } else if (lstMoveDirections.getSelected().equals("RIGHT")) {
                    performDirectionMove(MOVE, Direction.RIGHT);
                } else if (lstMoveDirections.getSelected().equals("CANCEL")){

                }

                updateGameInfoLabel();
                scrollPaneMoveDirections.remove();

                return true;
            }
        });

        directionsList.add(scrollPaneMoveDirections);
    }

    private void removeDirectionsPanel(){
        for (int i = 0; i < directionsList.size(); i++) {
            directionsList.get(i).remove();
        }
        directionsList.clear();
    }

    public void removeMovesAndDirectionsPanel(){
        for (int i = 0; i < movesList.size(); i++) {
            movesList.get(i).remove();
        }

        movesList.clear();

        removeDirectionsPanel();
    }
}
