package com.cs361d.flashpoint.screen;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.cs361d.flashpoint.manager.BoardManager;

import static com.cs361d.flashpoint.screen.FlashPointScreen.game;
import static com.cs361d.flashpoint.screen.FlashPointScreen.skinUI;

// TO BE USED ONLY WHEN THE STAGE IS BOARDSCREEN (not for other pages)

public class BoardDialog {

    Stage stage;

    public BoardDialog(){
    }

    public BoardDialog(Stage stage){
        this.stage = stage;
    }

    public void drawDialog(String title, String message) {
        Dialog dialog =
                new Dialog(title, skinUI, "dialog") {
                    public void result(Object obj) {
                        remove();
                    }
                };

        dialog.text(message);
        dialog.button("OK", true);
        dialog.show(stage);
    }

    public void drawEndGameDialog(String title, String message) {
        BoardManager.getInstance().setGameEnded();
        Dialog dialog =
                new Dialog(title, skinUI, "dialog") {
                    public void result(Object obj) {
                        if ((Boolean) obj) {
                            //audioMusic.stop();
                            game.setScreen(game.lobbyScreen);
                        }
                    }
                };

        dialog.text(message);
        dialog.button("OK", true);
        dialog.show(stage);
    }
}
