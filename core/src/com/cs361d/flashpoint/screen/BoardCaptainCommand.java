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
import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;
import com.cs361d.flashpoint.networking.Client;
import com.cs361d.flashpoint.networking.Server;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class BoardCaptainCommand {

    final int DIRECTION_BUTTON_SIZE = 50;

    ScrollPane scrollPane;
    ScrollPane.ScrollPaneStyle scrollStyle;
    List<String> lstOptions;
    List.ListStyle listStyle;

    Table directionTable;

    Stage stage;
    BoardDialog boardDialog;

    ArrayList<ScrollPane> colorList = new ArrayList<ScrollPane>();
    ArrayList<ScrollPane> movesList = new ArrayList<ScrollPane>();
    ArrayList<Table> directionsTableList = new ArrayList<Table>();

    // constructor
    public BoardCaptainCommand(Stage stage) {
        this.stage = stage;
        boardDialog = new BoardDialog(stage);
    }

    public void drawColorsPanel() {

        if (User.getInstance().isMyTurn() || true) {

            // list style
            listStyle = new List.ListStyle();
            listStyle.font = Font.get(25); // font size
            listStyle.fontColorUnselected = Color.BLACK;
            listStyle.fontColorSelected = Color.BLACK;
            listStyle.selection = TextureLoader.getDrawable(50, 100, Color.SKY);

            lstOptions = new List<String>(listStyle);
            lstOptions.setItems(getColorsForPanel());

            // scrollPane style
            scrollStyle = new ScrollPane.ScrollPaneStyle();
            scrollStyle.vScrollKnob = TextureLoader.getDrawable(15, 15, Color.DARK_GRAY);
            scrollStyle.vScroll = TextureLoader.getDrawable(15, 15, Color.LIGHT_GRAY);

            scrollPane = new ScrollPane(lstOptions, scrollStyle);
            scrollPane.setOverscroll(false, false);
            scrollPane.setFadeScrollBars(false);
            scrollPane.setScrollingDisabled(true, false);
            scrollPane.setTransform(true);
            scrollPane.setScale(1.0f);
            scrollPane.setWidth(345);
            scrollPane.setHeight(275);
            // scrollMessage.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() + 100);
            scrollPane.setPosition(
                    850, Gdx.graphics.getHeight() - scrollPane.getHeight() - 165);

            lstOptions.addListener(
                    new InputListener() {
                        @Override
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                            FireFighterColor color = FireFighterColor.fromString(lstOptions.getSelected());
                            drawMovePanelTable(color);

                            return true;
                        }
                    });

            colorList.add(scrollPane);
            stage.addActor(scrollPane);
        }
    }

    private void drawMovePanelTable(final FireFighterColor color) {

        removeColorPanel();

        // list style
        listStyle = new List.ListStyle();
        listStyle.font = Font.get(25); // font size
        listStyle.fontColorUnselected = Color.BLACK;
        listStyle.fontColorSelected = Color.BLACK;
        listStyle.selection = TextureLoader.getDrawable(50, 100, Color.SKY);

        lstOptions = new List<String>(listStyle);
        lstOptions.setItems(getMovesForPanel(color));

        // scrollPane style
        scrollStyle = new ScrollPane.ScrollPaneStyle();
        scrollStyle.vScrollKnob = TextureLoader.getDrawable(15, 15, Color.DARK_GRAY);
        scrollStyle.vScroll = TextureLoader.getDrawable(15, 15, Color.LIGHT_GRAY);

        scrollPane = new ScrollPane(lstOptions, scrollStyle);
        scrollPane.setOverscroll(false, false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setTransform(true);
        scrollPane.setScale(1.0f);
        scrollPane.setWidth(345);
        scrollPane.setHeight(275);
        // scrollMessage.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() + 100);
        scrollPane.setPosition(
                850, Gdx.graphics.getHeight() - scrollPane.getHeight() - 165);

        lstOptions.addListener(
                new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                        Actions action = Actions.fromString(lstOptions.getSelected());
                        drawDirectionPanelTable(color, action);

                        return true;
                    }
                });

        movesList.add(scrollPane);
        stage.addActor(scrollPane);
    }

    private void drawDirectionPanelTable(final FireFighterColor color, final Actions moveSelected) {

        ImageButton btnDirectionU = new ImageButton(getTextureForDirectionsTable(Direction.TOP));
        ImageButton btnDirectionD = new ImageButton(getTextureForDirectionsTable(Direction.BOTTOM));
        ImageButton btnDirectionL = new ImageButton(getTextureForDirectionsTable(Direction.LEFT));
        ImageButton btnDirectionR = new ImageButton(getTextureForDirectionsTable(Direction.RIGHT));

        directionTable = new Table();

        directionTable.add();
        directionTable.add(btnDirectionU).size(DIRECTION_BUTTON_SIZE, DIRECTION_BUTTON_SIZE);
        directionTable.add();

        directionTable.row();
        directionTable.add(btnDirectionL).size(DIRECTION_BUTTON_SIZE, DIRECTION_BUTTON_SIZE);
        directionTable.add();
        directionTable.add(btnDirectionR).size(DIRECTION_BUTTON_SIZE, DIRECTION_BUTTON_SIZE);


        directionTable.row();

        directionTable.add();

        directionTable.add(btnDirectionD).size(DIRECTION_BUTTON_SIZE, DIRECTION_BUTTON_SIZE);

        directionTable.add();

        directionTable.setPosition(1000, Gdx.graphics.getHeight() - directionTable.getHeight() - 550);

        final BoardMovesPanel boardMovesPanel = new BoardMovesPanel(stage);

                btnDirectionU.addListener(
                new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                        JSONObject obj = new JSONObject();
                        obj.put("color", color.toString());
                        obj.put("action", moveSelected.toString());
                        obj.put("direction", Direction.TOP);

                        Client.getInstance().sendCommand(Actions.COMMAND_OTHER_FIREFIGHTER, obj.toJSONString());
                        removeTableDirectionsPanel();
                        boardMovesPanel.drawMovesAndDirectionsPanel();
                        return true;
                    }
                });

        btnDirectionD.addListener(
                new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                        JSONObject obj = new JSONObject();
                        obj.put("color", color.toString());
                        obj.put("action", moveSelected.toString());
                        obj.put("direction", Direction.BOTTOM);

                        Client.getInstance().sendCommand(Actions.COMMAND_OTHER_FIREFIGHTER, obj.toJSONString());
                        removeTableDirectionsPanel();
                        boardMovesPanel.drawMovesAndDirectionsPanel();
                        return true;
                    }
                });

        btnDirectionL.addListener(
                new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        JSONObject obj = new JSONObject();
                        obj.put("color", color.toString());
                        obj.put("action", moveSelected.toString());
                        obj.put("direction", Direction.LEFT);

                        Client.getInstance().sendCommand(Actions.COMMAND_OTHER_FIREFIGHTER, obj.toJSONString());
                        removeTableDirectionsPanel();
                        boardMovesPanel.drawMovesAndDirectionsPanel();
                        return true;
                    }
                });

        btnDirectionR.addListener(
                new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        JSONObject obj = new JSONObject();
                        obj.put("color", color.toString());
                        obj.put("action", moveSelected.toString());
                        obj.put("direction", Direction.RIGHT);

                        Client.getInstance().sendCommand(Actions.COMMAND_OTHER_FIREFIGHTER, obj.toJSONString());
                        removeTableDirectionsPanel();
                        boardMovesPanel.drawMovesAndDirectionsPanel();
                        return true;
                    }
                });

        directionsTableList.add(directionTable);
        stage.addActor(directionTable);
    }

    // helper

    private String[] getColorsForPanel() {

        java.util.List<FireFighterColor> arr = FireFighterTurnManagerAdvance.getInstance().getColorForFireCaptain();

        String[] retArr = new String[arr.size()];

        int i = 0;
        for (FireFighterColor color :
                arr) {
            retArr[i] = color.toString();
            i++;
        }

        return retArr;

    }

    private String[] getMovesForPanel(FireFighterColor color) {

        java.util.List<Actions> arr = FireFighterTurnManagerAdvance.getInstance().getFireFighterPossibleActions(color);

        String[] retArr = new String[arr.size()];

        int i = 0;
        for (Actions actions :
                arr) {
            retArr[i] = actions.toString();
            i++;
        }

        return retArr;

    }

    private TextureRegionDrawable getTextureForDirectionsTable(Direction d) {

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

    private void removeColorPanel() {
        for (int i = 0; i < colorList.size(); i++) {
            colorList.get(i).remove();
        }

        colorList.clear();
    }

    private void removeMovePanel() {
        for (int i = 0; i < movesList.size(); i++) {
            movesList.get(i).remove();
        }

        movesList.clear();
    }

    private void removeTableDirectionsPanel() {
        removeMovePanel();
        for (int i = 0; i < directionsTableList.size(); i++) {
            directionsTableList.get(i).remove();
        }
        directionsTableList.clear();
    }
}