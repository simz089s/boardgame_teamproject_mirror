package com.cs361d.flashpoint.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.cs361d.flashpoint.manager.BoardManager;
import com.cs361d.flashpoint.manager.FireFighterTurnManager;
import com.cs361d.flashpoint.manager.User;
import com.cs361d.flashpoint.model.BoardElements.FireFighterColor;
import com.cs361d.flashpoint.model.FireFighterSpecialities.FireFighterAdvanced;

import java.util.ArrayList;

import static com.cs361d.flashpoint.screen.FlashPointScreen.skinUI;

public class BoardGameInfoLabel {

    Stage stage;
    Label gameInfoLabel;

    ArrayList<Label> infoLabels = new ArrayList<Label>();

    public BoardGameInfoLabel(Stage stage) {
        this.stage = stage;
    }

    public void drawGameInfoLabel() {

        removeInfoLabel();

        if (FireFighterTurnManager.getInstance().getCurrentFireFighter() == null) {
            return;
        }
        int numAP = FireFighterTurnManager.getInstance().getCurrentFireFighter().getActionPointsLeft();
        FireFighterColor color =
                FireFighterTurnManager.getInstance().getCurrentFireFighter().getColor();

        String specialty = "\n";
        if(BoardManager.getInstance().isAdvanced()) {
            specialty = "\n[ " + ((FireFighterAdvanced) FireFighterTurnManager.getInstance().getCurrentFireFighter()).getSpecialty() + " ]";
        }

        String specialAP = "";
        if(BoardManager.getInstance().isAdvanced()) {
            specialAP = "\nSpecial AP: " + ((FireFighterAdvanced) FireFighterTurnManager.getInstance().getCurrentFireFighter()).getSpecialActionPoints();
        }

        gameInfoLabel =
                new Label(

                        User.getInstance().getName().toUpperCase()
                                + "\nCurrent turn: "
                                + color
                                + "\nAP left: "
                                + numAP
                                + specialAP
                                + specialty,
                        skinUI);
        gameInfoLabel.setFontScale(1.2f);

        if (numAP == 0) {
            gameInfoLabel.setColor(Color.RED);
        } else {
            gameInfoLabel.setColor(Color.BLACK);
        }
        gameInfoLabel.setPosition(850, Gdx.graphics.getHeight() - 135);

        infoLabels.add(gameInfoLabel);
        stage.addActor(gameInfoLabel);
    }

    private void removeInfoLabel() { //
        for (int i = 0; i < infoLabels.size(); i++) {
            infoLabels.get(i).remove();
        }
        infoLabels.clear();
    }

}
