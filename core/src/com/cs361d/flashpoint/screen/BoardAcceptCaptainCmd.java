package com.cs361d.flashpoint.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.cs361d.flashpoint.manager.BoardManager;
import com.cs361d.flashpoint.manager.FireFighterTurnManagerAdvance;
import com.cs361d.flashpoint.manager.User;
import com.cs361d.flashpoint.model.BoardElements.Direction;
import com.cs361d.flashpoint.networking.Client;
import com.cs361d.flashpoint.networking.ServerCommands;

import java.util.ArrayList;

import static com.cs361d.flashpoint.screen.BoardScreen.boardMovesPanel;
import static com.cs361d.flashpoint.screen.BoardScreen.removeAllPrevFragments;
import static com.cs361d.flashpoint.screen.BoardScreen.stage;
import static com.cs361d.flashpoint.screen.FlashPointScreen.skinUI;

public class BoardAcceptCaptainCmd {

    static Table specialtiesTable;

    static ScrollPane.ScrollPaneStyle scrollStyle;
    static List<String> listOptions;
    static List.ListStyle listStyle;

    static TextButton btnConfirmYes;
    static TextButton btnConfirmNo;

    static ArrayList<Table> specialtiesTablesList = new ArrayList<Table>();

    public static void drawAcceptCaptainCmdPanel(Actions action, Direction direction) {

        removeAllPrevFragments();

        if (!BoardManager.getInstance().isAdvanced()) {
            return;
        }

        Label label = new Label(action.toString(), skinUI);
        label.setFontScale(1.5f);
        label.setColor(Color.BLACK);

        ImageButton directionImg = new ImageButton(BoardMovesPanel.getTextureForDirectionsTable(direction));

        // confirm button creation

        btnConfirmYes = new TextButton("YES", skinUI, "default");
        btnConfirmYes.setWidth(70);
        btnConfirmYes.setHeight(25);
        btnConfirmYes.setColor(Color.GREEN);
        btnConfirmYes.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {

                        Client.getInstance().sendCommand(ServerCommands.ACCEPT_MOVE_BY_CAPTAIN, "" + true);

                        removeAcceptCaptainCmdPanel();
                        boardMovesPanel.drawMovesAndDirectionsPanel();
                    }
                });

        btnConfirmNo = new TextButton("NO", skinUI, "default");
        btnConfirmNo.setWidth(70);
        btnConfirmNo.setHeight(25);
        btnConfirmNo.setColor(Color.RED);
        btnConfirmNo.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {

                        Client.getInstance().sendCommand(ServerCommands.ACCEPT_MOVE_BY_CAPTAIN, "" + false);

                        removeAcceptCaptainCmdPanel();
                        boardMovesPanel.drawMovesAndDirectionsPanel();
                    }
                });

        specialtiesTable = new Table();

        specialtiesTable.add(label).size(label.getWidth(), label.getHeight());
        specialtiesTable.row();
        specialtiesTable.add(directionImg).size(100, 100);
        specialtiesTable.row();
        specialtiesTable.add(btnConfirmYes).size(btnConfirmYes.getWidth(), btnConfirmYes.getHeight());
        specialtiesTable.row().size(btnConfirmYes.getWidth(), btnConfirmYes.getHeight());
        specialtiesTable.add(btnConfirmNo).size(btnConfirmNo.getWidth(), btnConfirmNo.getHeight());

        specialtiesTable.setPosition(
                1000,
                Gdx.graphics.getHeight() - 350);

        specialtiesTablesList.add(specialtiesTable);
        stage.addActor(specialtiesTable);
    }

    public static void removeAcceptCaptainCmdPanel(){
        for (int i = 0; i < specialtiesTablesList.size(); i++) {
            specialtiesTablesList.get(i).remove();
        }
        specialtiesTablesList.clear();
    }


}
