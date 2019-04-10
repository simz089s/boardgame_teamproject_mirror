package com.cs361d.flashpoint.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.cs361d.flashpoint.manager.BoardManager;
import com.cs361d.flashpoint.manager.FireFighterTurnManagerAdvance;
import com.cs361d.flashpoint.model.FireFighterSpecialities.FireFighterAdvanceSpecialties;
import com.cs361d.flashpoint.networking.Client;
import com.cs361d.flashpoint.networking.ServerCommands;

import java.util.ArrayList;

import static com.cs361d.flashpoint.screen.FlashPointScreen.skinUI;

public class BoardChooseSpecialtyPanel {

    String [] INITIAL_SPECIALTY;

    Stage stage;

    Table specialtiesTable;

    ScrollPane scrollPaneSpecialties;
    ScrollPane.ScrollPaneStyle scrollStyleSpecialties;
    List<String> lstSpecialties;
    List.ListStyle listStyleSpecialties;

    TextButton btnConfirm;

    ArrayList<Table> specialtiesTablesList = new ArrayList<Table>();

    // constructor
    public BoardChooseSpecialtyPanel(Stage stage){
        this.stage = stage;
    }

    public void drawChooseSpecialtyPanel() {

        if (!BoardManager.getInstance().isAdvanced()) {
            return;
        }

        setSpecialities();

        // list style
        listStyleSpecialties = new List.ListStyle();
        listStyleSpecialties.font = Font.get(22); // font size
        listStyleSpecialties.fontColorUnselected = Color.BLACK;
        listStyleSpecialties.fontColorSelected = Color.BLACK;
        listStyleSpecialties.selection = TextureLoader.getDrawable(50, 100, Color.SKY );

        lstSpecialties = new List<String>(listStyleSpecialties);
        lstSpecialties.setItems(INITIAL_SPECIALTY);

        // scrollPane style
        scrollStyleSpecialties = new ScrollPane.ScrollPaneStyle();
        scrollStyleSpecialties.vScrollKnob = TextureLoader.getDrawable(15, 15, Color.DARK_GRAY);
        scrollStyleSpecialties.vScroll = TextureLoader.getDrawable(15, 15, Color.LIGHT_GRAY);

        scrollPaneSpecialties = new ScrollPane(lstSpecialties, scrollStyleSpecialties);
        scrollPaneSpecialties.setOverscroll(false, false);
        scrollPaneSpecialties.setFadeScrollBars(false);
        scrollPaneSpecialties.setScrollingDisabled(true, false);
        scrollPaneSpecialties.setTransform(true);
        scrollPaneSpecialties.setScale(1.0f);
        scrollPaneSpecialties.setWidth(300);
        scrollPaneSpecialties.setHeight(300);

        //TODO : add image of specialty card on hover
//        lstSpecialties.addListener(new ClickListener() {
//            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
//                String selected = lstSpecialties.getSelected();
//            }
//            public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
//                String selected = lstSpecialties.getSelected();
//            }
//        });


        // confirm button creation

        btnConfirm = new TextButton("Confirm", skinUI, "default");
        btnConfirm.setWidth(150);
        btnConfirm.setHeight(25);
        btnConfirm.setColor(Color.CORAL);
        btnConfirm.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        String specialtySelected = lstSpecialties.getSelected();

                        // set init specialty
                        if (! FireFighterTurnManagerAdvance.getInstance().currentHasSpecialty()){
                            Client.getInstance().sendCommand(ServerCommands.SET_INITIAL_SPECIALTY,specialtySelected);
                        } else { // crew change
                            Client.getInstance().sendCommand(Actions.CREW_CHANGE,specialtySelected);

                        }

                        removeChooseSpecialtyPanel();
                    }
                });

        lstSpecialties.addListener(
                new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        BoardDialog bd = new BoardDialog(stage);
                        bd.drawOnSpecialtyClickDialog(FireFighterAdvanceSpecialties.fromString(lstSpecialties.getSelected()));
                        return true;
                    }
                });

        specialtiesTable = new Table();

        specialtiesTable.add(scrollPaneSpecialties).size(scrollPaneSpecialties.getWidth(), scrollPaneSpecialties.getHeight());
        specialtiesTable.row();
        specialtiesTable.add().size(scrollPaneSpecialties.getWidth(), btnConfirm.getHeight()); // just a space
        specialtiesTable.row();
        specialtiesTable.add(btnConfirm).size(scrollPaneSpecialties.getWidth(), btnConfirm.getHeight());

        specialtiesTable.setPosition(
                1000,
                Gdx.graphics.getHeight() - 350);

        specialtiesTablesList.add(specialtiesTable);
        stage.addActor(specialtiesTable);
    }


    public void setSpecialities() {
        INITIAL_SPECIALTY = ((FireFighterTurnManagerAdvance) FireFighterTurnManagerAdvance.getInstance()).getAvailableSpecialities();
    }

    public void removeChooseSpecialtyPanel(){
        for (int i = 0; i < specialtiesTablesList.size(); i++) {
            specialtiesTablesList.get(i).remove();
        }
        specialtiesTablesList.clear();
    }


}
