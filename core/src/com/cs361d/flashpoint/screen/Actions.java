package com.cs361d.flashpoint.screen;

import java.util.ArrayList;
import java.util.List;

public enum Actions {
  MOVE("MOVE"),
  MOVE_WITH_VICTIM("MOVE WITH VICTIM"),
  CHOP("CHOP"),
  EXTINGUISH("EXTINGUISH"),
  INTERACT_WITH_DOOR("INTERACT WITH DOOR"),
  MOVE_WITH_HAZMAT("MOVE WITH HAZMAT"),
  DRIVE_AMBULANCE("DRIVE AMBULANCE"),
  DRIVE_FIRETRUCK("DRIVE FIRETRUCK"),
  FIRE_DECK_GUN("FIRE DECK GUN"),
  CREW_CHANGE("CREW CHANGE"),
  REMOVE_HAZMAT("REMOVE HAZMAT"),
  COMMAND_FIREMEN("COMMAND FIREMEN"),
  CURE_VICTIM("CURE VICTIM"),
  FLIP_POI("FLIP POI"),
  CLEAR_HOTSPOT("CLEAR HOTSPOT"),
  REPAIR_WALL("REPAIR WALL"),
  END_TURN("END TURN"),
  SPREAD_FIRE("SPREAD FIRE"),
  SAVE("SAVE");

  private String text;

  Actions(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return text;
  }

  public static Actions fromString(String text) {
    for (Actions a : Actions.values()) {
      if (a.text.equalsIgnoreCase(text)) {
        return a;
      }
    }

    throw new IllegalArgumentException("The enum " + text + " does not exist");
  }

  public static List<Actions> basicActions() {
    List<Actions> list = new ArrayList<Actions>();
    for (Actions a : Actions.values()) {
      switch (a) {
        case CHOP:
        case MOVE:
        case END_TURN:
        case EXTINGUISH:
        case MOVE_WITH_VICTIM:
        case SAVE:
        case INTERACT_WITH_DOOR:
          list.add(a);
          break;
        default:
      }
    }
    return list;
  }

  public static List<Actions> advancedActions() {
    List<Actions> list = new ArrayList<Actions>();
    for (Actions a : Actions.values()) {
      switch (a) {
        case CHOP:
        case MOVE:
        case EXTINGUISH:
        case MOVE_WITH_VICTIM:
        case MOVE_WITH_HAZMAT:
        case INTERACT_WITH_DOOR:
        case DRIVE_AMBULANCE:
        case DRIVE_FIRETRUCK:
        case CREW_CHANGE:
        case FIRE_DECK_GUN:
        case END_TURN:
        case SAVE:
          list.add(a);
          break;
        default:
      }
    }
    return list;
  }

  public static List<Actions> hazmatTechnicianActions() {
    List<Actions> list = advancedActions();
    list.add(list.size()-2, REMOVE_HAZMAT);
    return list;
  }

  public static List<Actions> paramedicActions() {
    List<Actions> list = advancedActions();
    list.add(list.size()-2, CURE_VICTIM);
    return list;
  }

  public static List<Actions> imagingTechnicianActions() {
    List<Actions> list = advancedActions();
    list.add(list.size()-2,FLIP_POI);
    return list;
  }

  public static List<Actions> rescueDogActions() {
    List<Actions> list = new ArrayList<Actions>();
    list.add(MOVE);
    list.add(MOVE_WITH_VICTIM);
    list.add(CREW_CHANGE);
    list.add(END_TURN);
    list.add(SAVE);
    return list;
  }

  public static List<Actions> fireCaptainActions() {
    List<Actions> list = advancedActions();
    list.add(list.size()-2, COMMAND_FIREMEN);
    return list;
  }

  public static List<Actions> structuralEngineerActions() {
    List<Actions> list = advancedActions();
    list.add(list.size()-2,REPAIR_WALL);
    list.add(list.size()-2,CLEAR_HOTSPOT);
    list.remove(EXTINGUISH);
    return list;
  }

  public static String[] convertToStringArray(List<Actions> list) {
    List<String> sList = new ArrayList<String>();
    for (Actions a : list) {
      sList.add(a.text);
    }

    return sList.toArray(new String[list.size()]);
  }
}
