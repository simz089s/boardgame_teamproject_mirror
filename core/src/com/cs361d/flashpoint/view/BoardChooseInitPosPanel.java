package com.cs361d.flashpoint.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.cs361d.flashpoint.manager.BoardManager;
import com.cs361d.flashpoint.manager.DBHandler;
import com.cs361d.flashpoint.manager.FireFighterTurnManager;
import com.cs361d.flashpoint.model.BoardElements.Direction;

import java.util.ArrayList;

import static com.cs361d.flashpoint.view.BoardScreen.*;

public class BoardChooseInitPosPanel {

    String [] INITIAL_POS = {"0,0", "0,1", "0,2", "0,3", "0,4", "0,5", "0,6", "0,7", "0,8", "0,9",
            "7,0", "7,1", "7,2", "7,3", "7,4", "7,5", "7,6", "7,7", "7,8", "7,9",
            "1,0", "2,0", "3,0", "4,0", "5,0", "6,0", "7,0",
            "1,9", "2,9", "3,9", "4,9", "5,9", "6,9", "7,9"
    };

    ScrollPane scrollPanePos;
    ScrollPane.ScrollPaneStyle scrollStylePos;
    List<String> lstPos;
    List.ListStyle listStylePos;

    Stage stage;

    ArrayList<ScrollPane> posList = new ArrayList<ScrollPane>();

    // constructor
    public BoardChooseInitPosPanel(Stage stage){
        this.stage = stage;
    }

    public void createChooseInitPosPanel() {
        // list style
        listStylePos = new List.ListStyle();
        listStylePos.font = Font.get(22); // font size
        listStylePos.fontColorUnselected = Color.BLACK;
        listStylePos.fontColorSelected = Color.BLACK;
        listStylePos.selection = TextureLoader.getDrawable(50, 100, Color.SKY );

        lstPos = new List<String>(listStylePos);
        lstPos.setItems(INITIAL_POS);

        // scrollPane style
        scrollStylePos = new ScrollPane.ScrollPaneStyle();
        scrollStylePos.vScrollKnob = TextureLoader.getDrawable(15, 15, Color.DARK_GRAY);
        scrollStylePos.vScroll = TextureLoader.getDrawable(15, 15, Color.LIGHT_GRAY);

        scrollPanePos = new ScrollPane(lstPos, scrollStylePos);
        scrollPanePos.setOverscroll(false, false);
        scrollPanePos.setFadeScrollBars(false);
        scrollPanePos.setScrollingDisabled(true, false);
        scrollPanePos.setTransform(true);
        scrollPanePos.setScale(1.0f);
        scrollPanePos.setWidth(300);
        scrollPanePos.setHeight(380);
        //scrollMessage.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() + 100);
        scrollPanePos.setPosition(
                850,
                Gdx.graphics.getHeight() - scrollPanePos.getHeight() - 150);

        lstPos.addListener(new InputListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {

                String moveSelected = lstPos.getSelected();

                return true;
            }
        });

        posList.add(scrollPanePos);

        stage.addActor(scrollPanePos);
    }

    public void removeChooseInitPosPanel(){
        for (int i = 0; i < posList.size(); i++) {
            posList.get(i).remove();
        }

        posList.clear();
    }
}
