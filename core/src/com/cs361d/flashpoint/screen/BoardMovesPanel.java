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
import com.cs361d.flashpoint.model.FireFighterSpecialities.FireFighterAdvanced;
import com.cs361d.flashpoint.networking.Commands;
import com.cs361d.flashpoint.networking.NetworkManager;

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

    boolean actionDone = false; //TODO

    FireFighterTurnManager fireFighterTurnManager = FireFighterTurnManager.getInstance();
    switch(Actions.fromString(move)) {
      case MOVE:
        fireFighterTurnManager.move(direction);
        break;
      case MOVE_WITH_VICTIM:
        fireFighterTurnManager.moveWithVictim(direction);
        break;
      case CHOP:
        fireFighterTurnManager.chopWall(direction);
        break;
      case EXTINGUISH:
        fireFighterTurnManager.extinguishFire(direction);
        break;
      case INTERACT_WITH_DOOR:
        fireFighterTurnManager.interactWithDoor(direction);
        break;
      default:
    }

    if ((FireFighterTurnManager.getInstance() instanceof FireFighterTurnManagerAdvance)) {
      FireFighterTurnManagerAdvance fireFighterTurnManagerAdvance = (FireFighterTurnManagerAdvance) fireFighterTurnManager;
      switch(Actions.fromString(move)) {
        case MOVE_WITH_HAZMAT:
          fireFighterTurnManagerAdvance.moveWithHazmat(direction);
          break;
        case DRIVE_FIRETRUCK:
        case DRIVE_AMBULANCE:
        default:
      }
    }
    clearAllGameUnits();
    drawGameUnitsOnTile();
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

              String move = lstMoveOptions.getSelected();

              FireFighterTurnManager fireFighterTurnManager = FireFighterTurnManager.getInstance();
              switch(Actions.fromString(move)) {
                case MOVE:
                  drawDirectionsPanelTable(move);
                  break;
                case EXTINGUISH:
                  drawDirectionsPanelTable(move);
                  break;
                case CHOP:
                  drawDirectionsPanelTable(move);
                  break;
                case MOVE_WITH_VICTIM:
                  drawDirectionsPanelTable(move);
                  break;
                case INTERACT_WITH_DOOR:
                  drawDirectionsPanelTable(move);
                  break;
                case END_TURN:
                  try {
                    fireFighterTurnManager.endTurn();
                  } catch (IllegalAccessException e) {
                    e.printStackTrace();
                  }

                  clearAllGameUnits();
                  drawGameUnitsOnTile();
                  boardGameInfoLabel.drawGameInfoLabel();
                  break;
                case SAVE:
                  DBHandler.saveBoardToDB(BoardManager.getInstance().getGameName());
                  boardDialog.drawDialog("Save", "Your game has been successfully saved.");
                  break;
                default:
              }

              if ((FireFighterTurnManager.getInstance() instanceof FireFighterTurnManagerAdvance)) {
                FireFighterTurnManagerAdvance fireFighterTurnManagerAdvance = (FireFighterTurnManagerAdvance) fireFighterTurnManager;
                switch(Actions.fromString(move)) {
                  case FIRE_DECK_GUN:
                    break;
                  case MOVE_WITH_HAZMAT:
                    drawDirectionsPanelTable(move);
                    break;
                  case DRIVE_AMBULANCE:
                  case DRIVE_FIRETRUCK:
                  case REMOVE_HAZMAT:
                  case FLIP_POI:
                  case CURE_VICTIM:
                  case CREW_CHANGE:
                    removeMovesAndDirectionsPanel();
                    boardChooseRolePanel.drawChooseSpecialtyPanel();
                    break;
                  default:
                }
              }
              return true;
            }
          });

      movesList.add(scrollPaneMoveOptions);

      stage.addActor(scrollPaneMoveOptions);
    }
  }

  private void drawDirectionsPanelTable(String moveSelected) {

    removeTableDirectionsPanel();

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
    if (MOVE.equals(Actions.EXTINGUISH.toString())) {
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
    stage.addActor(directionTable);
  }

  // helper

  private String[] getMovesArrForDisplay() {

    if (BoardManager.getInstance().isAdvanced()) {
      return Actions.convertToStringArray(
          FireFighterTurnManagerAdvance.getInstance().getCurrentFireFighter().getActions());
    }
    return Actions.convertToStringArray(Actions.basicActions());
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
