package com.cs361d.flashpoint.model.BoardElements;

public class Tile {

    private int[][] position;

    private int top_wall;
    private int bottom_wall;
    private int left_wall;
    private int right_wall;

    private String has_firefighter;

    private boolean has_victim;
    private boolean has_false_alarm;

    private boolean has_smoke;
    private boolean has_fire;
    private boolean has_explosion;

    public Tile(){

    }

    public int[][] getPosition() {
        return position;
    }

    public void setPosition(int[][] position) {
        this.position = position;
    }

    public int getTop_wall() {
        return top_wall;
    }

    public void setTop_wall(int top_wall) {
        this.top_wall = top_wall;
    }

    public int getBottom_wall() {
        return bottom_wall;
    }

    public void setBottom_wall(int bottom_wall) {
        this.bottom_wall = bottom_wall;
    }

    public int getLeft_wall() {
        return left_wall;
    }

    public void setLeft_wall(int left_wall) {
        this.left_wall = left_wall;
    }

    public int getRight_wall() {
        return right_wall;
    }

    public void setRight_wall(int right_wall) {
        this.right_wall = right_wall;
    }

    public String getHas_firefighter() {
        return has_firefighter;
    }

    public void setHas_firefighter(String has_firefighter) {
        this.has_firefighter = has_firefighter;
    }

    public boolean isHas_victim() {
        return has_victim;
    }

    public void setHas_victim(boolean has_victim) {
        this.has_victim = has_victim;
    }

    public boolean isHas_false_alarm() {
        return has_false_alarm;
    }

    public void setHas_false_alarm(boolean has_false_alarm) {
        this.has_false_alarm = has_false_alarm;
    }

    public boolean isHas_smoke() {
        return has_smoke;
    }

    public void setHas_smoke(boolean has_smoke) {
        this.has_smoke = has_smoke;
    }

    public boolean isHas_fire() {
        return has_fire;
    }

    public void setHas_fire(boolean has_fire) {
        this.has_fire = has_fire;
    }

    public boolean isHas_explosion() {
        return has_explosion;
    }

    public void setHas_explosion(boolean has_explosion) {
        this.has_explosion = has_explosion;
    }
}
