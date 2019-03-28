package com.cs361d.flashpoint.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.cs361d.flashpoint.manager.BoardManager;
import com.cs361d.flashpoint.networking.Client;
import com.cs361d.flashpoint.networking.UserResponse;
import com.cs361d.flashpoint.networking.ServerCommands;

import java.util.ArrayList;

import static com.cs361d.flashpoint.screen.BoardScreen.removeAllPrevFragments;
import static com.cs361d.flashpoint.screen.BoardScreen.setCurrentFragment;
import static com.cs361d.flashpoint.screen.BoardScreen.stage;
import static com.cs361d.flashpoint.screen.FlashPointScreen.skinUI;

public class BoardFireDeckGunPanel {

    static Table fireDeckGunTable;
    static TextButton button;
    static ArrayList<Table> fireDeckGunTablesList = new ArrayList<Table>();

    public static void drawFireDeckGunPanel() {

        if (!BoardManager.getInstance().isAdvanced()) {
            return;
        }

        removeAllPrevFragments();

        setCurrentFragment(Fragment.CALL_FOR_ACTION);

        Label label = new Label("What do you want to do?", skinUI);
        label.setFontScale(1.5f);
        label.setColor(Color.BLACK);

        // confirm button creation
        fireDeckGunTable = new Table();
        fireDeckGunTable.add(label).size(label.getWidth(), label.getHeight());
        for (final UserResponse response : UserResponse.driverResponse()) {

        button = new TextButton(response.toString(), skinUI, "default");
        button.setWidth(label.getWidth());
        button.setHeight(25);
        button.setColor(Color.GRAY);
        button.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Client.getInstance().sendCommand(ServerCommands.ASK_DRIVER_MSG, response.toString());
                        removeFireDeckGunPanel();
                    }
                });
        fireDeckGunTable.row();
        fireDeckGunTable.add().size(button.getWidth(), button.getHeight()); // just a space
        fireDeckGunTable.row();
        fireDeckGunTable.add(button).size(label.getWidth(), button.getHeight());
        }
        fireDeckGunTable.row();
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

        setCurrentFragment(Fragment.EMPTY);
    }


}
