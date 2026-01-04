package com.mygdx.game;

public class HeartseedTree {
    public int x;
    public int y;
    public boolean isBloomed = false;

    public HeartseedTree(int x,int y){
        this.x = x;
        this.y =y;
    }

    public void bloom(){
        isBloomed = true;
    }
    public boolean isBloomed(){
        return isBloomed;
    }
}
