package com.cs361d.flashpoint.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.cs361d.flashpoint.manager.User;
import com.cs361d.flashpoint.networking.Client;
import com.cs361d.flashpoint.networking.ServerCommands;

import java.util.ArrayList;

import static com.cs361d.flashpoint.screen.BoardScreen.removeAllPrevFragments;
import static com.cs361d.flashpoint.screen.FlashPointScreen.skinUI;

public class BoardChatFragment {

    Stage stage;

    // chat message list
    static List<String> lstMsg;
    List.ListStyle listStyle;
    ScrollPane scrollPaneMsg;
    ScrollPane.ScrollPaneStyle scrollStyle;

    // input message field
    TextField textFieldMsg;

    // messages list
    static ArrayList<String> messagesArrList = new ArrayList<String>();


    ArrayList<ScrollPane> msgListSP = new ArrayList<ScrollPane>();
    ArrayList<TextField> msgFieldListTF = new ArrayList<TextField>();

    public static void addMessageToChat(String msg) {
        messagesArrList.add(msg);
        lstMsg.clearItems();
        String[] msgArr = messagesArrList.toArray(new String[messagesArrList.size()]);
        lstMsg.setItems(msgArr);
    }

    // constructor
    public BoardChatFragment(Stage stage){
        this.stage = stage;
//        messagesArrList.add("R: yo, ready to set fire to the house?");
//        messagesArrList.add("B: let's do it. it's gonna be lit!");
//        messagesArrList.add("G: wait, can't find the explosives");
    }

    public void drawChatFragment() {

        if(BoardScreen.isCallForActionFragment()){
            return;
        }

        removeAllPrevFragments();

        // list style
        listStyle = new List.ListStyle();
        listStyle.font = Font.get(18); // font size
        listStyle.fontColorUnselected = Color.BLACK;
        listStyle.fontColorSelected = Color.BLACK;
        listStyle.selection = TextureLoader.getDrawable(100, 100, Color.CLEAR );

        lstMsg = new List<String>(listStyle);
        messagesArrList.clear();
        String[] messagesStrArr = messagesArrList.toArray(new String[messagesArrList.size()]);
        lstMsg.setItems(messagesStrArr);

        // scrollPane style
        scrollStyle = new ScrollPane.ScrollPaneStyle();
        scrollStyle.vScrollKnob = TextureLoader.getDrawable(10, 10, Color.DARK_GRAY);
        scrollStyle.vScroll = TextureLoader.getDrawable(10, 10, Color.LIGHT_GRAY);

        scrollPaneMsg = new ScrollPane(lstMsg, scrollStyle);
        scrollPaneMsg.setOverscroll(false, false);
        scrollPaneMsg.setFadeScrollBars(false);
        scrollPaneMsg.setTransform(true);
        scrollPaneMsg.setWidth(360);
        scrollPaneMsg.setHeight(420);
        //scrollMessage.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() + 100);
        scrollPaneMsg.setPosition(
                845,
                Gdx.graphics.getHeight() - scrollPaneMsg.getHeight() - 170);

        createMessageInputText();

        msgListSP.add(scrollPaneMsg);
        msgFieldListTF.add(textFieldMsg);

        stage.addActor(scrollPaneMsg);
        stage.addActor(textFieldMsg);
    }

    private void createMessageInputText() {
        textFieldMsg = new TextField("", skinUI, "default");
        textFieldMsg.setMessageText("Message + [Enter]");
        textFieldMsg.setMaxLength(25); // max number of characters allowed
        textFieldMsg.setWidth(360);
        textFieldMsg.setHeight(25);
        textFieldMsg.setPosition(
                845,
                30);

        textFieldMsg.setTextFieldListener(new TextField.TextFieldListener(){
            @Override
            public void keyTyped(TextField textField, char c){

                if((int)c == 13 || (int)c == 10) {
                    String messageInputed = textFieldMsg.getText();

                    if(!messageInputed.equals("") && !messageInputed.equals(" ")){
                        textFieldMsg.setText("");
                        String fullMessage = User.getInstance().getName() + ": " + messageInputed;
                        Client.getInstance().sendCommand(ServerCommands.ADD_CHAT_MESSAGE, fullMessage);
                        scrollPaneMsg.setActor(lstMsg);
                        scrollPaneMsg.layout();
                        scrollPaneMsg.scrollTo(0, 0, 0, 0);
                    }
                }
            }
        });
    }

    public void removeChatFragment() {
        for (int i = 0; i < msgListSP.size(); i++) {
            msgListSP.get(i).remove();
            msgFieldListTF.get(i).remove();
        }

        msgListSP.clear();
        msgFieldListTF.clear();
    }

}