package com.cs361d.flashpoint.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.cs361d.flashpoint.manager.BoardManager;
import com.cs361d.flashpoint.manager.DBHandler;
import com.cs361d.flashpoint.manager.FireFighterTurnManager;
import com.cs361d.flashpoint.manager.User;
import com.cs361d.flashpoint.model.BoardElements.Direction;
import com.cs361d.flashpoint.networking.Commands;
import com.cs361d.flashpoint.networking.NetworkManager;

import java.util.ArrayList;

import static com.cs361d.flashpoint.view.BoardScreen.*;

public class BoardMovesPanel {

    final int DIRECTION_BUTTON_SIZE = 75;

    ScrollPane scrollPaneMoveOptions;
    ScrollPane.ScrollPaneStyle scrollStyle;
    List<String> lstMoveOptions;
    List.ListStyle listStyleMoveOptions;

    Table directionTable;


    Stage stage;
    FireFighterTurnManager fireFighterTurnManager = FireFighterTurnManager.getInstance();

    ArrayList<ScrollPane> movesList = new ArrayList<ScrollPane>();
    ArrayList<Table> directionsTableList = new ArrayList<Table>();

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



    public void createMovesAndDirectionsPanel() {

        if (User.getInstance().isMyTurn()) {

            // list style
            listStyleMoveOptions = new List.ListStyle();
            listStyleMoveOptions.font = Font.get(25); // font size
            listStyleMoveOptions.fontColorUnselected = Color.BLACK;
            listStyleMoveOptions.fontColorSelected = Color.BLACK;
            listStyleMoveOptions.selection = TextureLoader.getDrawable(50, 100, Color.SKY);

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
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                    String moveSelected = lstMoveOptions.getSelected();

                    if (moveSelected.equals("MOVE")) {
                        removeTableDirectionsPanel();
                        createDirectionsPanelTable(moveSelected);
                        stage.addActor(directionTable);

                    } else if (moveSelected.equals("EXTINGUISH")) {
                        removeTableDirectionsPanel();
                        createDirectionsPanelTable(moveSelected);
                        stage.addActor(directionTable);

                    } else if (moveSelected.equals("CHOP")) {
                        removeTableDirectionsPanel();
                        createDirectionsPanelTable(moveSelected);
                        stage.addActor(directionTable);

                    } else if (moveSelected.equals("MOVE WITH VICTIM")) {
                        removeTableDirectionsPanel();
                        createDirectionsPanelTable(moveSelected);
                        stage.addActor(directionTable);

                    } else if (moveSelected.equals("INTERACT WITH DOOR")) {
                        removeTableDirectionsPanel();
                        createDirectionsPanelTable(moveSelected);
                        stage.addActor(directionTable);

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

                    } else if (moveSelected.equals("SAVE")) {
                        DBHandler.saveBoardToDB(BoardManager.getInstance().getGameName());
                        NetworkManager.getInstance().sendCommand(Commands.SAVE, "");
                        createDialog("Save", "Your game has been successfully saved.");
                    } else {
                        //debugLbl.setText("failed action");
                    }

                    return true;
                }
            });

            movesList.add(scrollPaneMoveOptions);

            stage.addActor(scrollPaneMoveOptions);
        }
    }



    private void createDirectionsPanelTable(String moveSelected){

        final String MOVE = moveSelected;

        ImageButton btnDirectionU = new ImageButton(getTextureForDirectionTable(Direction.TOP));
        ImageButton btnDirectionD = new ImageButton(getTextureForDirectionTable(Direction.BOTTOM));
        ImageButton btnDirectionCurr = new ImageButton(getTextureForDirectionTable(Direction.NODIRECTION));
        ImageButton btnDirectionL = new ImageButton(getTextureForDirectionTable(Direction.LEFT));
        ImageButton btnDirectionR = new ImageButton(getTextureForDirectionTable(Direction.RIGHT));

        directionTable = new Table();

        directionTable.add();
        directionTable.add(btnDirectionU).width(DIRECTION_BUTTON_SIZE);
        directionTable.add();

        directionTable.row();

        directionTable.add(btnDirectionL).width(DIRECTION_BUTTON_SIZE);
        if (MOVE.equals("EXTINGUISH")){
            directionTable.add(btnDirectionCurr).width(DIRECTION_BUTTON_SIZE);
        } else {
            directionTable.add();
        }
        directionTable.add(btnDirectionR).width(DIRECTION_BUTTON_SIZE);

        directionTable.row();

        directionTable.add();
        directionTable.add(btnDirectionD).width(DIRECTION_BUTTON_SIZE);
        directionTable.add();

        directionTable.setPosition(
                1000,
                Gdx.graphics.getHeight() - directionTable.getHeight() - 480);

        btnDirectionU.addListener(new InputListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                performDirectionMove(MOVE, Direction.TOP);
                updateGameInfoLabel();
                removeTableDirectionsPanel();
                return true;
            }
        });

        btnDirectionD.addListener(new InputListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                performDirectionMove(MOVE, Direction.BOTTOM);
                updateGameInfoLabel();
                removeTableDirectionsPanel();
                return true;
            }
        });

        btnDirectionCurr.addListener(new InputListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                performDirectionMove(MOVE, Direction.NODIRECTION);
                updateGameInfoLabel();
                removeTableDirectionsPanel();
                return true;
            }
        });

        btnDirectionL.addListener(new InputListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                performDirectionMove(MOVE, Direction.LEFT);
                updateGameInfoLabel();
                removeTableDirectionsPanel();
                return true;
            }
        });

        btnDirectionR.addListener(new InputListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                performDirectionMove(MOVE, Direction.RIGHT);
                updateGameInfoLabel();
                removeTableDirectionsPanel();
                return true;
            }
        });


        directionsTableList.add(directionTable);
    }



    // helper



    private String[] getMovesArrForDisplay(){
        String[] MOVES_ARR = {
                "MOVE", "EXTINGUISH", "CHOP", "MOVE WITH VICTIM", "INTERACT WITH DOOR", "END TURN", "SAVE"
        };

        // TODO: MOVES_ARR = fireFighterTurnManager.getAvailableMoves();

        return MOVES_ARR;
    }

    private TextureRegionDrawable getTextureForDirectionTable(Direction d){

        Texture myTexture = null;

        if (d == Direction.TOP){
            myTexture = new Texture(Gdx.files.internal("icons/arrow_u.png"));
        } else if (d == Direction.BOTTOM){
            myTexture = new Texture(Gdx.files.internal("icons/arrow_d.png"));
        } else if (d == Direction.NODIRECTION){
            myTexture = new Texture(Gdx.files.internal("icons/arrow_current.png"));
        } else if (d == Direction.LEFT){
            myTexture = new Texture(Gdx.files.internal("icons/arrow_l.png"));
        } else if (d == Direction.RIGHT){
            myTexture = new Texture(Gdx.files.internal("icons/arrow_r.png"));
        }

        TextureRegion myTextureRegion = new TextureRegion(myTexture);
        TextureRegionDrawable myTexRegionDrawable = new TextureRegionDrawable(myTextureRegion);

        return myTexRegionDrawable;
    }





    // remove




    private void removeTableDirectionsPanel(){ //
        for (int i = 0; i < directionsTableList.size(); i++) {
            directionsTableList.get(i).remove();
        }
        directionsTableList.clear();
    }

    public void removeMovesAndDirectionsPanel(){
        for (int i = 0; i < movesList.size(); i++) {
            movesList.get(i).remove();
        }

        movesList.clear();

        removeTableDirectionsPanel();
    }
}
