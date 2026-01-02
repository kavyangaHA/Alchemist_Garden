package com.mygdx.game;

import java.util.ArrayList;

public class BombPlant extends Plant{
    //we need to add a flag to see if the bomb has already exploded
    private boolean hasExploded = false;
    public BombPlant(int x,int y){
        super(x,y);
    }

    @Override
    public void grow() {
        if (age < MAX_AGE){
            age++;
        }
    }
    //this is the special action for the bomb plant
    public void explode(ArrayList<CrackedWall>walls){
        if (hasExploded) return;//only explode once
        hasExploded =true;
        System.out.println("BOOM! @(" + x+", "+y+")");

        //check all four adjacent titles(up,down,left,right)
        int[] dx = {0,0,-1,-1};
        int[] dy = {1,-1,0,0};

        for(int i =0;i<4;i++){
            int checkX = this.x +dx[i];
            int checkY = this.y + dy[i];
            //Iterate through the walls list backwards to safely remove items while iterating
            for(int j = walls.size()-1;j>=0;j--){
                CrackedWall wall = walls.get(j);
                if(wall.x == checkX && wall.y == checkY){
                    walls.remove(j);//Destroy the wall
                    System.out.println("Wall destroyed at (" + checkX +","+checkY+")");
                }

            }
        }

    }

    public boolean hasExploded() {
        return hasExploded;
    }
}
