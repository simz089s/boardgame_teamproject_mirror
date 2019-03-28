package com.cs361d.flashpoint.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.cs361d.flashpoint.manager.BoardManager;
import com.cs361d.flashpoint.model.BoardElements.CarrierStatus;
import com.cs361d.flashpoint.networking.Client;
import com.cs361d.flashpoint.networking.ServerCommands;
import com.cs361d.flashpoint.networking.UserResponse;
import org.json.simple.JSONObject;

import java.util.ArrayList;

import static com.cs361d.flashpoint.screen.BoardScreen.removeAllPrevFragments;
import static com.cs361d.flashpoint.screen.BoardScreen.stage;
import static com.cs361d.flashpoint.screen.FlashPointScreen.skinUI;

public class BoardRideVehicleFragment {

    static Table rideVehicleTable;

    static Label label;
    static TextButton btnAcceptRide, btnRefuseRide;

    static ArrayList<Table> rideVehicleTablesList = new ArrayList<Table>();

    public static void drawRideVehicleFragment(final CarrierStatus carrierStatus) {

        removeAllPrevFragments();

        if (!BoardManager.getInstance().isAdvanced()) {
            return;
        }

        String vehicleOfInterest = "";

        switch (carrierStatus) {
            case HASAMBULANCE:
                vehicleOfInterest = "ambulance";
                break;
            case HASFIRETRUCK:
                vehicleOfInterest = "firetruck";
                break;
            default:
        }

        label = new Label("Wanna ride to the new " + vehicleOfInterest + "'s location?", skinUI);
        label.setFontScale(1.0f);
        label.setColor(Color.BLACK);

        btnAcceptRide = new TextButton("Yes", skinUI, "default");
        btnAcceptRide.setWidth(label.getWidth());
        btnAcceptRide.setHeight(35);
        btnAcceptRide.setColor(Color.CHARTREUSE);

        btnAcceptRide.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Client.getInstance().sendCommand(ServerCommands.REPLY_MOVE_WITH_VEHICULE,UserResponse.ACCEPT.toString());
                        removeRideVehiclePanel();
                    }
                });

        btnRefuseRide = new TextButton("No", skinUI, "default");
        btnRefuseRide.setWidth(label.getWidth());
        btnRefuseRide.setHeight(35);
        btnRefuseRide.setColor(Color.FIREBRICK);

        btnRefuseRide.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Client.getInstance().sendCommand(ServerCommands.REPLY_MOVE_WITH_VEHICULE,UserResponse.REJECT.toString());
                        removeRideVehiclePanel();
                    }
                });


        rideVehicleTable = new Table();

        rideVehicleTable.add(label).size(label.getWidth(), label.getHeight());
        rideVehicleTable.row();
        rideVehicleTable.add().size(btnAcceptRide.getWidth(), btnAcceptRide.getHeight()); // just a space
        rideVehicleTable.row();
        rideVehicleTable.add(btnAcceptRide).size(label.getWidth(), btnAcceptRide.getHeight());
        rideVehicleTable.row();
        rideVehicleTable.add().size(btnAcceptRide.getWidth(), btnAcceptRide.getHeight()); // just a space
        rideVehicleTable.row();
        rideVehicleTable.add(btnRefuseRide).size(label.getWidth(), btnRefuseRide.getHeight());

        rideVehicleTable.setPosition(
                1020,
                Gdx.graphics.getHeight() - 350);

        rideVehicleTablesList.add(rideVehicleTable);
        stage.addActor(rideVehicleTable);
    }

    public static void removeRideVehiclePanel(){
        for (int i = 0; i < rideVehicleTablesList.size(); i++) {
            rideVehicleTablesList.get(i).remove();
        }
        rideVehicleTablesList.clear();
    }
}