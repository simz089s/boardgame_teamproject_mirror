package com.cs361d.flashpoint.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.cs361d.flashpoint.manager.BoardManager;
import com.cs361d.flashpoint.manager.BoardManagerAdvanced;
import com.cs361d.flashpoint.manager.FireFighterTurnManager;
import com.cs361d.flashpoint.manager.FireFighterTurnManagerAdvance;
import com.cs361d.flashpoint.model.BoardElements.FireFighter;
import com.cs361d.flashpoint.model.FireFighterSpecialities.FireFighterAdvanceSpecialities;
import com.cs361d.flashpoint.model.FireFighterSpecialities.FireFighterAdvanced;

import java.util.ArrayList;

public class BoardChooseRolePanel {

    String [] INITIAL_ROLE;

    ScrollPane scrollPaneRoles;
    ScrollPane.ScrollPaneStyle scrollStyleRoles;
    List<String> lstRoles;
    List.ListStyle listStyleRoles;

    Stage stage;

    ArrayList<ScrollPane> rolesList = new ArrayList<ScrollPane>();

    // constructor
    public BoardChooseRolePanel(Stage stage){
        this.stage = stage;
    }

    public void drawChooseRolePanel() {

        if (!(BoardManager.getInstance() instanceof BoardManagerAdvanced))
        {
            return;
        }
        setSpecialities();
        // list style
        listStyleRoles = new List.ListStyle();
        listStyleRoles.font = Font.get(22); // font size
        listStyleRoles.fontColorUnselected = Color.BLACK;
        listStyleRoles.fontColorSelected = Color.BLACK;
        listStyleRoles.selection = TextureLoader.getDrawable(50, 100, Color.SKY );

        lstRoles = new List<String>(listStyleRoles);
        lstRoles.setItems(INITIAL_ROLE);

        // scrollPane style
        scrollStyleRoles = new ScrollPane.ScrollPaneStyle();
        scrollStyleRoles.vScrollKnob = TextureLoader.getDrawable(15, 15, Color.DARK_GRAY);
        scrollStyleRoles.vScroll = TextureLoader.getDrawable(15, 15, Color.LIGHT_GRAY);

        scrollPaneRoles = new ScrollPane(lstRoles, scrollStyleRoles);
        scrollPaneRoles.setOverscroll(false, false);
        scrollPaneRoles.setFadeScrollBars(false);
        scrollPaneRoles.setScrollingDisabled(true, false);
        scrollPaneRoles.setTransform(true);
        scrollPaneRoles.setScale(1.0f);
        scrollPaneRoles.setWidth(300);
        scrollPaneRoles.setHeight(380);
        //scrollMessage.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() + 100);
        scrollPaneRoles.setPosition(
                850,
                Gdx.graphics.getHeight() - scrollPaneRoles.getHeight() - 150);

        lstRoles.addListener(new InputListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {

                String moveSelected = lstRoles.getSelected();
                removeChooseRolePanel();
                return true;
            }
        });

        rolesList.add(scrollPaneRoles);

        stage.addActor(scrollPaneRoles);
    }


    public void setSpecialities() {
        INITIAL_ROLE = ((FireFighterTurnManagerAdvance) FireFighterTurnManagerAdvance.getInstance()).getAvailableSpecialities();
    }

    public void removeChooseRolePanel(){
        for (int i = 0; i < rolesList.size(); i++) {
            rolesList.get(i).remove();
        }

        rolesList.clear();
    }

}
