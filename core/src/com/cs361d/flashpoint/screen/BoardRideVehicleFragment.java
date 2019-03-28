package com.cs361d.flashpoint.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.cs361d.flashpoint.manager.BoardManager;
import com.cs361d.flashpoint.model.BoardElements.Direction;

import java.util.ArrayList;

import static com.cs361d.flashpoint.screen.BoardScreen.removeAllPrevFragments;
import static com.cs361d.flashpoint.screen.BoardScreen.stage;
import static com.cs361d.flashpoint.screen.FlashPointScreen.skinUI;

public class BoardRideVehicleFragment {

    static Label label;
    static TextButton btnAcceptRide, btnRefuseRide;

    public static void drawRideVehicleFragment() {

        removeAllPrevFragments();

        if (!BoardManager.getInstance().isAdvanced()) {
            return;
        }

        label = new Label("Do you want to ride to the new vehicle's location?", skinUI);
        label.setFontScale(1.5f);
        label.setColor(Color.BLACK);
        btnAcceptRide.setPosition(
                1000,
                Gdx.graphics.getHeight() - 300);

        btnAcceptRide = new TextButton("Yes", skinUI, "default");
        btnAcceptRide.setWidth(label.getWidth());
        btnAcceptRide.setHeight(25);
        btnAcceptRide.setColor(Color.GREEN);
        btnAcceptRide.setPosition(
                1000,
                Gdx.graphics.getHeight() - 350);

        btnAcceptRide.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        label.remove();
                        btnAcceptRide.remove();
                        btnRefuseRide.remove();
                    }
                });

        btnRefuseRide = new TextButton("No", skinUI, "default");
        btnRefuseRide.setWidth(label.getWidth());
        btnRefuseRide.setHeight(25);
        btnRefuseRide.setColor(Color.PINK);
        btnRefuseRide.setPosition(
                1000,
                Gdx.graphics.getHeight() - 400);

        btnRefuseRide.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        label.remove();
                        btnAcceptRide.remove();
                        btnRefuseRide.remove();
                    }
                });

        stage.addActor(label);
        stage.addActor(btnAcceptRide);
        stage.addActor(btnRefuseRide);
    }
}