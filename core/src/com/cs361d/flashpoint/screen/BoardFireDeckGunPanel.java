package com.cs361d.flashpoint.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.cs361d.flashpoint.manager.BoardManager;
import com.cs361d.flashpoint.networking.Client;
import com.cs361d.flashpoint.networking.DriverResponse;
import com.cs361d.flashpoint.networking.ServerCommands;

import java.util.ArrayList;

import static com.cs361d.flashpoint.screen.BoardScreen.removeAllPrevFragments;
import static com.cs361d.flashpoint.screen.BoardScreen.stage;
import static com.cs361d.flashpoint.screen.FlashPointScreen.skinUI;

public class BoardFireDeckGunPanel {

    static Table fireDeckGunTable;

    static TextButton btnAccept;
    static TextButton btnFireRow;
    static TextButton btnFireCol;

    static ArrayList<Table> fireDeckGunTablesList = new ArrayList<Table>();

    public static void drawFireDeckGunPanel() {

        removeAllPrevFragments();

        if (!BoardManager.getInstance().isAdvanced()) {
            return;
        }

        Label label = new Label("What do you want to do?", skinUI);
        label.setFontScale(1.5f);
        label.setColor(Color.BLACK);

        // confirm button creation

        btnAccept = new TextButton(DriverResponse.ACCEPT.toString(), skinUI, "default");
        btnAccept.setWidth(200);
        btnAccept.setHeight(25);
        btnAccept.setColor(Color.GREEN);
        btnAccept.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Client.getInstance().sendCommand(ServerCommands.ASK_DRIVER_MSG, DriverResponse.ACCEPT.toString());
                        removeFireDeckGunPanel();
                    }
                });

        btnFireRow = new TextButton(DriverResponse.THROW_ROW_DIE.toString(), skinUI, "default");
        btnFireRow.setWidth(200);
        btnFireRow.setHeight(25);
        btnFireRow.setColor(Color.SKY);
        btnFireRow.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Client.getInstance().sendCommand(ServerCommands.ASK_DRIVER_MSG, DriverResponse.THROW_ROW_DIE.toString());
                        removeFireDeckGunPanel();
                    }
                });

        btnFireCol = new TextButton(DriverResponse.THROW_COLUMN_DIE.toString(), skinUI, "default");
        btnFireCol.setWidth(200);
        btnFireCol.setHeight(25);
        btnFireCol.setColor(Color.SKY);
        btnFireCol.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Client.getInstance().sendCommand(ServerCommands.ASK_DRIVER_MSG, DriverResponse.THROW_COLUMN_DIE.toString());
                        removeFireDeckGunPanel();
                    }
                });

        fireDeckGunTable = new Table();

        fireDeckGunTable.add(label).size(label.getWidth(), label.getHeight());
        fireDeckGunTable.row();
        fireDeckGunTable.add().size(btnAccept.getWidth(), btnAccept.getHeight()); // just a space
        fireDeckGunTable.row();
        fireDeckGunTable.add(btnAccept).size(btnAccept.getWidth(), btnAccept.getHeight());
        fireDeckGunTable.row();
        fireDeckGunTable.add().size(btnAccept.getWidth(), btnAccept.getHeight()); // just a space
        fireDeckGunTable.row();
        fireDeckGunTable.add(btnFireRow).size(btnFireRow.getWidth(), btnFireRow.getHeight());
        fireDeckGunTable.row();
        fireDeckGunTable.add().size(btnAccept.getWidth(), btnAccept.getHeight()); // just a space
        fireDeckGunTable.row();
        fireDeckGunTable.add(btnFireCol).size(btnFireRow.getWidth(), btnFireRow.getHeight());

        fireDeckGunTable.setPosition(
                1000,
                Gdx.graphics.getHeight() - 350);

        fireDeckGunTablesList.add(fireDeckGunTable);
        stage.addActor(fireDeckGunTable);
    }

    public static void removeFireDeckGunPanel(){
        for (int i = 0; i < fireDeckGunTablesList.size(); i++) {
            fireDeckGunTablesList.get(i).remove();
        }
        fireDeckGunTablesList.clear();
    }


}
