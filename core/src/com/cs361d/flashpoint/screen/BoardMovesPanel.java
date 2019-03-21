package com.cs361d.flashpoint.screen;

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
import com.cs361d.flashpoint.manager.*;
import com.cs361d.flashpoint.model.BoardElements.Direction;

import java.util.ArrayList;

import static com.cs361d.flashpoint.screen.BoardScreen.*;

public class BoardMovesPanel {

  final int DIRECTION_BUTTON_SIZE = 65;

  ScrollPane scrollPaneMoveOptions;
  ScrollPane.ScrollPaneStyle scrollStyle;
  List<String> lstMoveOptions;
  List.ListStyle listStyleMoveOptions;

  Table directionTable;

  Stage stage;
  BoardDialog boardDialog;
  BoardGameInfoLabel boardGameInfoLabel;
  BoardChooseSpecialtyPanel boardChooseRolePanel;
  FireFighterTurnManager fireFighterTurnManager = FireFighterTurnManager.getInstance();

  ArrayList<ScrollPane> movesList = new ArrayList<ScrollPane>();
  ArrayList<Table> directionsTableList = new ArrayList<Table>();

  // constructor
  public BoardMovesPanel(Stage stage, BoardGameInfoLabel boardGameInfoLabel) {

    this.stage = stage;
    this.boardGameInfoLabel = boardGameInfoLabel;
    boardDialog = new BoardDialog(stage);
    boardChooseRolePanel = new BoardChooseSpecialtyPanel(stage);
  }

  private void performDirectionMove(String move, Direction direction) {
    if (move.equals("MOVE")) {
      clearAllGameUnits();
      fireFighterTurnManager.move(direction);
      drawGameUnitsOnTile();
    } else if (move.equals("EXTINGUISH")) {
      clearAllGameUnits();
      fireFighterTurnManager.extinguishFire(direction);
      drawGameUnitsOnTile();
    } else if (move.equals("CHOP")) {
      clearAllGameUnits();
      fireFighterTurnManager.chopWall(direction);
      drawGameUnitsOnTile();
    } else if (move.equals("MOVE WITH VICTIM")) {
      clearAllGameUnits();
      fireFighterTurnManager.moveWithVictim(direction);
      drawGameUnitsOnTile();
    } else if (move.equals("INTERACT WITH DOOR")) {
      clearAllGameUnits();
      fireFighterTurnManager.interactWithDoor(direction);
      drawGameUnitsOnTile();
    }
  }

  public void drawMovesAndDirectionsPanel() {

    if (User.getInstance().isMyTurn() || true) {

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
      scrollPaneMoveOptions.setWidth(345);
      scrollPaneMoveOptions.setHeight(250);
      // scrollMessage.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() + 100);
      scrollPaneMoveOptions.setPosition(
          850, Gdx.graphics.getHeight() - scrollPaneMoveOptions.getHeight() - 165);

      lstMoveOptions.addListener(
          new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

              String moveSelected = lstMoveOptions.getSelected();

              if (moveSelected.equals("MOVE")) {
                removeTableDirectionsPanel();
                drawDirectionsPanelTable(moveSelected);
                stage.addActor(directionTable);

              } else if (moveSelected.equals("EXTINGUISH")) {
                removeTableDirectionsPanel();
                drawDirectionsPanelTable(moveSelected);
                stage.addActor(directionTable);

              } else if (moveSelected.equals("CHOP")) {
                removeTableDirectionsPanel();
                drawDirectionsPanelTable(moveSelected);
                stage.addActor(directionTable);

              } else if (moveSelected.equals("MOVE WITH VICTIM")) {
                removeTableDirectionsPanel();
                drawDirectionsPanelTable(moveSelected);
                stage.addActor(directionTable);

              } else if (moveSelected.equals("INTERACT WITH DOOR")) {
                removeTableDirectionsPanel();
                drawDirectionsPanelTable(moveSelected);
                stage.addActor(directionTable);

              } else if (moveSelected.equals("END TURN")) {
                clearAllGameUnits();
                try {
                  fireFighterTurnManager.endTurn();
                } catch (IllegalAccessException e) {
                  e.printStackTrace();
                }

                drawGameUnitsOnTile();
                boardGameInfoLabel.drawGameInfoLabel();

              } else if (moveSelected.equals("CREW CHANGE")) {
                removeMovesAndDirectionsPanel();
                boardChooseRolePanel.drawChooseSpecialtyPanel();

              } else if (moveSelected.equals("SAVE")) {
                DBHandler.saveBoardToDB(BoardManager.getInstance().getGameName());
                boardDialog.drawDialog("Save", "Your game has been successfully saved.");
              } else {
                // debugLbl.setText("failed action");
              }

              return true;
            }
          });

      movesList.add(scrollPaneMoveOptions);

      stage.addActor(scrollPaneMoveOptions);
    }
  }

  private void drawDirectionsPanelTable(String moveSelected) {

    final String MOVE = moveSelected;

    ImageButton btnDirectionU = new ImageButton(getTextureForDirectionTable(Direction.TOP));
    ImageButton btnDirectionD = new ImageButton(getTextureForDirectionTable(Direction.BOTTOM));
    ImageButton btnDirectionCurr = new ImageButton(getTextureForDirectionTable(Direction.NODIRECTION));
    ImageButton btnDirectionL = new ImageButton(getTextureForDirectionTable(Direction.LEFT));
    ImageButton btnDirectionR = new ImageButton(getTextureForDirectionTable(Direction.RIGHT));

    directionTable = new Table();

    directionTable.add();
    directionTable.add(btnDirectionU).size(DIRECTION_BUTTON_SIZE, DIRECTION_BUTTON_SIZE);
    directionTable.add();

    directionTable.row();

    directionTable.add(btnDirectionL).size(DIRECTION_BUTTON_SIZE, DIRECTION_BUTTON_SIZE);
    if (MOVE.equals("EXTINGUISH")) {
      directionTable.add(btnDirectionCurr).size(DIRECTION_BUTTON_SIZE, DIRECTION_BUTTON_SIZE);
    } else {
      directionTable.add();
    }
    directionTable.add(btnDirectionR).size(DIRECTION_BUTTON_SIZE, DIRECTION_BUTTON_SIZE);

    directionTable.row();

    directionTable.add();
    directionTable.add(btnDirectionD).size(DIRECTION_BUTTON_SIZE, DIRECTION_BUTTON_SIZE);
    directionTable.add();

    directionTable.setPosition(1000, Gdx.graphics.getHeight() - directionTable.getHeight() - 520);

    btnDirectionU.addListener(
        new InputListener() {
          @Override
          public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            performDirectionMove(MOVE, Direction.TOP);
            boardGameInfoLabel.drawGameInfoLabel();
            removeTableDirectionsPanel();
            return true;
          }
        });

    btnDirectionD.addListener(
        new InputListener() {
          @Override
          public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            performDirectionMove(MOVE, Direction.BOTTOM);
            boardGameInfoLabel.drawGameInfoLabel();
            removeTableDirectionsPanel();
            return true;
          }
        });

    btnDirectionCurr.addListener(
        new InputListener() {
          @Override
          public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            performDirectionMove(MOVE, Direction.NODIRECTION);
            boardGameInfoLabel.drawGameInfoLabel();
            removeTableDirectionsPanel();
            return true;
          }
        });

    btnDirectionL.addListener(
        new InputListener() {
          @Override
          public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            performDirectionMove(MOVE, Direction.LEFT);
            boardGameInfoLabel.drawGameInfoLabel();
            removeTableDirectionsPanel();
            return true;
          }
        });

    btnDirectionR.addListener(
        new InputListener() {
          @Override
          public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            performDirectionMove(MOVE, Direction.RIGHT);
            boardGameInfoLabel.drawGameInfoLabel();
            removeTableDirectionsPanel();
            return true;
          }
        });

    directionsTableList.add(directionTable);
  }

  // helper

  private String[] getMovesArrForDisplay() {

    String[] MOVES_ARR = {
      "MOVE", "EXTINGUISH", "CHOP", "MOVE WITH VICTIM", "INTERACT WITH DOOR", "END TURN", "SAVE"
    };

    String[] MOVES_ARR_EXP = {
      "MOVE",
      "EXTINGUISH",
      "CHOP",
      "MOVE WITH VICTIM",
      "INTERACT WITH DOOR",
      "END TURN",
      "DRIVE",
      "RIDE",
      "FIRE DECK GUN",
      "CREW CHANGE",
      "SAVE"
    };

    if (BoardManager.getInstance() instanceof BoardManagerAdvanced) {
      return MOVES_ARR_EXP;
    }
    return MOVES_ARR;
  }

  private TextureRegionDrawable getTextureForDirectionTable(Direction d) {

    Texture myTexture = null;

    if (d == Direction.TOP) {
      myTexture = new Texture(Gdx.files.internal("icons/arrow_u.png"));
    } else if (d == Direction.BOTTOM) {
      myTexture = new Texture(Gdx.files.internal("icons/arrow_d.png"));
    } else if (d == Direction.NODIRECTION) {
      myTexture = new Texture(Gdx.files.internal("icons/arrow_current.png"));
    } else if (d == Direction.LEFT) {
      myTexture = new Texture(Gdx.files.internal("icons/arrow_l.png"));
    } else if (d == Direction.RIGHT) {
      myTexture = new Texture(Gdx.files.internal("icons/arrow_r.png"));
    }

    TextureRegion myTextureRegion = new TextureRegion(myTexture);
    TextureRegionDrawable myTexRegionDrawable = new TextureRegionDrawable(myTextureRegion);

    return myTexRegionDrawable;
  }

  // remove

  private void removeTableDirectionsPanel() { //
    for (int i = 0; i < directionsTableList.size(); i++) {
      directionsTableList.get(i).remove();
    }
    directionsTableList.clear();
  }

  public void removeMovesAndDirectionsPanel() {
    for (int i = 0; i < movesList.size(); i++) {
      movesList.get(i).remove();
    }

    movesList.clear();

    removeTableDirectionsPanel();

    boardChooseRolePanel.removeChooseSpecialtyPanel();
  }
}
