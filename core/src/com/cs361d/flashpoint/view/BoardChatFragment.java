package com.cs361d.flashpoint.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import java.util.ArrayList;
import java.util.Arrays;

import static com.cs361d.flashpoint.view.FlashPointScreen.skinUI;

public class BoardChatFragment {

    Stage stage;

    // chat message list
    List<String> lstMsg;
    List.ListStyle listStyle;
    ScrollPane scrollPaneMsg;
    ScrollPane.ScrollPaneStyle scrollStyle;

    // input message field
    TextField textFieldMsg;

    // messages list
    String[] messages = {"Jacques:  Ready guys?", "Simon:  let's start!", "Elvric: wait, just a sec"};
    final ArrayList<String> msgs = new ArrayList<String>(Arrays.asList(messages));

    ArrayList<ScrollPane> msgList = new ArrayList<ScrollPane>();
    ArrayList<TextField> msgFieldList = new ArrayList<TextField>();

    // constructor
    public BoardChatFragment(Stage stage){
        this.stage = stage;
    }

    public void createChatFragment() {
        // list style
        listStyle = new List.ListStyle();
        listStyle.font = Font.get(25); // font size
        listStyle.fontColorUnselected = Color.BLACK;
        listStyle.fontColorSelected = Color.BLACK;
        listStyle.selection = TextureLoader.getDrawable(100, 100, Color.CLEAR );

        lstMsg = new List<String>(listStyle);
        lstMsg.setItems(messages);

        // scrollPane style
        scrollStyle = new ScrollPane.ScrollPaneStyle();
        scrollStyle.vScrollKnob = TextureLoader.getDrawable(15, 15, Color.DARK_GRAY);
        scrollStyle.vScroll = TextureLoader.getDrawable(15, 15, Color.LIGHT_GRAY);

        scrollPaneMsg = new ScrollPane(lstMsg, scrollStyle);
        scrollPaneMsg.setOverscroll(false, false);
        scrollPaneMsg.setFadeScrollBars(false);
        scrollPaneMsg.setScrollingDisabled(true, false);
        scrollPaneMsg.setTransform(true);
        scrollPaneMsg.setScale(1.0f);
        scrollPaneMsg.setWidth(300);
        scrollPaneMsg.setHeight(450);
        //scrollMessage.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() + 100);
        scrollPaneMsg.setPosition(
                850,
                Gdx.graphics.getHeight() - scrollPaneMsg.getHeight() - 150);

        createMessageInputText();

        msgList.add(scrollPaneMsg);
        msgFieldList.add(textFieldMsg);

        stage.addActor(scrollPaneMsg);
        stage.addActor(textFieldMsg);
    }

    private void createMessageInputText() {
        textFieldMsg = new TextField("", skinUI, "default");
        textFieldMsg.setMessageText("Message + [Enter]");
        textFieldMsg.setWidth(300);
        textFieldMsg.setHeight(25);
        textFieldMsg.setPosition(
                850,
                20);

        textFieldMsg.setTextFieldListener(new TextField.TextFieldListener(){
            @Override
            public void keyTyped(TextField textField, char c){

                if((int)c == 13 || (int)c == 10) {
                    String messageInputed = textFieldMsg.getText();

                    if(!messageInputed.equals("") && !messageInputed.equals(" ")){
                        textFieldMsg.setText("");
                        msgs.add("Jacques:  " + messageInputed);
                        String[] newMsg = msgs.toArray(new String[msgs.size()]);
                        lstMsg.setItems(newMsg);

                        scrollPaneMsg.setActor(lstMsg);
                        scrollPaneMsg.layout();
                        scrollPaneMsg.scrollTo(0, 0, 0, 0);
                    }
                }
            }
        });
    }

    public void removeChatFragment() {
        for (int i = 0; i < msgList.size(); i++) {
            msgList.get(i).remove();
            msgFieldList.get(i).remove();
        }

        msgList.clear();
        msgFieldList.clear();
    }

}
