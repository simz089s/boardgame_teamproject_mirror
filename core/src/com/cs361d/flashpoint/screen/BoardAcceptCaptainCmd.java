package com.cs361d.flashpoint.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.cs361d.flashpoint.manager.BoardManager;
import com.cs361d.flashpoint.manager.FireFighterTurnManagerAdvance;
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

    static ScrollPane scrollPane;
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

        String[] cmdInfoFromCaptain = new String[2];

        cmdInfoFromCaptain[0] = action.toString();
        cmdInfoFromCaptain[1] = direction.toString();

        // list style
        listStyle = new List.ListStyle();
        listStyle.font = Font.get(22); // font size
        listStyle.fontColorUnselected = Color.BLACK;
        listStyle.fontColorSelected = Color.BLACK;
        listStyle.selection = TextureLoader.getDrawable(50, 100, Color.SKY );

        listOptions = new List<String>(listStyle);
        listOptions.setItems(cmdInfoFromCaptain);

        // scrollPane style
        scrollStyle = new ScrollPane.ScrollPaneStyle();
        scrollStyle.vScrollKnob = TextureLoader.getDrawable(15, 15, Color.DARK_GRAY);
        scrollStyle.vScroll = TextureLoader.getDrawable(15, 15, Color.LIGHT_GRAY);

        scrollPane = new ScrollPane(listOptions, scrollStyle);
        scrollPane.setOverscroll(false, false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setTransform(true);
        scrollPane.setScale(1.0f);
        scrollPane.setWidth(300);
        scrollPane.setHeight(300);

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

        specialtiesTable.add(scrollPane).size(scrollPane.getWidth(), scrollPane.getHeight());
        specialtiesTable.row();
        specialtiesTable.add().size(scrollPane.getWidth(), btnConfirmYes.getHeight());
        specialtiesTable.row();
        specialtiesTable.add(btnConfirmYes).size(btnConfirmYes.getWidth(), btnConfirmYes.getHeight());
        specialtiesTable.add(btnConfirmYes).size(btnConfirmNo.getWidth(), btnConfirmNo.getHeight());

        specialtiesTable.setPosition(
                1050,
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
