package com.cs361d.flashpoint.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.cs361d.flashpoint.manager.BoardManager;
import com.cs361d.flashpoint.networking.Client;
import com.cs361d.flashpoint.networking.ServerCommands;

import java.util.Random;

import static com.cs361d.flashpoint.screen.FlashPointScreen.game;
import static com.cs361d.flashpoint.screen.FlashPointScreen.skinUI;

// TO BE USED ONLY WHEN THE STAGE IS BOARDSCREEN (not for other pages)

public class BoardDialog {

    Color[] colorsForDialog = {Color.PINK, Color.SKY, Color.CYAN, Color.CHARTREUSE, Color.GOLDENROD};

    Stage stage;

    public BoardDialog(){
    }

    public BoardDialog(Stage stage){
        this.stage = stage;
    }

    public void drawDialog(String title, String message) {
        Dialog dialog =
                new Dialog("   " + title + "   ", skinUI, "dialog") {
                    public void result(Object obj) {
                        remove();
                    }
                };

        dialog.text("     " + message + "     ");
        dialog.button("   OK   ", true);
        Random random = new Random();
        Color colorForDialog = colorsForDialog[random.nextInt(colorsForDialog.length)];
        dialog.setColor(colorForDialog);
        dialog.show(stage);
    }

    public void drawEndGameDialog(String title, String message) {
        BoardManager.getInstance().setGameEnded();
        Dialog dialog =
                new Dialog(title, skinUI, "dialog") {
                    public void result(Object obj) {
                        if ((Boolean) obj) {
                            //audioMusic.stop();
                            Client.getInstance().sendCommand(ServerCommands.GET_SAVED_GAMES,"");
                        }
                    }
                };

        dialog.text(message);
        dialog.button("OK", true);
        dialog.show(stage);
    }
}
