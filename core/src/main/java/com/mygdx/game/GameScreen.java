package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

public class GameScreen implements Screen {

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Viewport viewport;

    public static final int GRID_WIDTH = 20;
    public static final int GRID_HEIGHT = 15;
    public static final int TILE_SIZE = 32;

    private Texture whitePixel;
    private Player player; // We added a Player object!
    private ArrayList<Plant>plants;
    private ArrayList<CrackedWall>walls;
    private static final int BOMB_KEY = Input.Keys.B;
    private ArrayList<String>inventorySeeds;
    private ArrayList<String>inventoryEssences;
    private ArrayList<Essence> essencesInWorld;
    private static final int COMBINE_KEY = Input.Keys.C;

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GRID_WIDTH * TILE_SIZE, GRID_HEIGHT * TILE_SIZE);
        viewport = new FitViewport(GRID_WIDTH * TILE_SIZE, GRID_HEIGHT * TILE_SIZE, camera);
        batch = new SpriteBatch();

        // Create our white pixel texture
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1);
        pixmap.fill();
        whitePixel = new Texture(pixmap);
        pixmap.dispose();

        // Initialize the player at the center of the grid
        player = new Player(GRID_WIDTH / 2, GRID_HEIGHT / 2);
        plants = new ArrayList<>();
        walls = new ArrayList<>();

        walls.add(new CrackedWall(5,5));
        walls.add(new CrackedWall(6,5));
        walls.add(new CrackedWall(7,5));
        walls.add(new CrackedWall(10,5));


        inventorySeeds = new ArrayList<>();
        inventoryEssences = new ArrayList<>();
        essencesInWorld = new ArrayList<>();

        //Give the player a starting seed
        inventorySeeds.add("Vine");
        inventorySeeds.add("Bomb");

        //PLace a Sun Essence in the world
        essencesInWorld.add(new Essence(15,10,"SunEssence"));

    }

    @Override
    public void render(float delta) {
        // --- INPUT HANDLING ---
        // We check for keys being pressed just once per frame
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            if (player.y < GRID_HEIGHT - 1) player.y++; // Move up if not at the top edge
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            if (player.y > 0) player.y--; // Move down if not at the bottom edge
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            if (player.x > 0) player.x--; // Move left if not at the left edge
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            if (player.x < GRID_WIDTH - 1) player.x++; // Move right if not at the right edge
        }

//        if(Gdx.input.isKeyJustPressed(Input.Keys.P)){
//            //Let's plant a vine seed!
//            //First we need to check the tile is occupied already
//            boolean tileOccupied = false;
//            for (Plant plant : plants){
//                if(plant.x == player.x && plant.y == player.y){
//                    tileOccupied = true;
//                    break;
//                }
//            }
//
//            //If the tile is not occupied,plant a new one
//            //for now always the plan is created at player's x,y coordinators
//            if(!tileOccupied){
//                //using the factory method
//                Plant newPlant = PlantFactory.createPlant("Vine",player.x, player.y);
//                if (newPlant !=null){
//                    plants.add(newPlant);
//                }
//            }
//        }

        //Combine items with 'C'
        if (Gdx.input.isKeyJustPressed(COMBINE_KEY)){
            Grimoire.combine(inventorySeeds,inventoryEssences);
        }
/// /*******
//plant a Vine with "P"
        if(Gdx.input.isKeyJustPressed(Input.Keys.P)){
            if(!isTileOccupied(player.x, player.y) && !inventorySeeds.isEmpty()){
                //plants.add(PlantFactory.createPlant("Vine",player.x, player.y));
                String seedToPlant = inventorySeeds.get(0);
                Plant newPlant = PlantFactory.createPlant(seedToPlant,player.x,player.y);
                if (newPlant != null){
                    plants.add(newPlant);
                    //inventorySeeds.remove(0);//optional

                }
            }
        }
/// *************
        if(Gdx.input.isKeyJustPressed(BOMB_KEY)){
            if(!isTileOccupied(player.x, player.y)){
                plants.add(PlantFactory.createPlant("Bomb", player.x, player.y));
            }
        }





        //update all plants
//        for (Plant plant:plants){
//            plant.grow();
//        }

        ArrayList<Plant> plantsToRemove = new ArrayList<>();

        for (Plant plant:plants){
            plant.grow();
            //check if a Bomb plant has just matured
            if (plant instanceof BombPlant && plant.isMature() && !((BombPlant)plant).hasExploded()){
                ((BombPlant)plant).explode((walls));
                plantsToRemove.add(plant);//marking the bomb fot the removal
            }
        }
        //remove all plants that exploded
        plants.removeAll(plantsToRemove);
//pick up logicc
        //check for esssense pickups

        for(int i = essencesInWorld.size()-1;i>=0;i--){
            Essence essence = essencesInWorld.get(i);
            if(essence.x == player.x && essence.y == player.y){
                inventoryEssences.add(essence.type);
                essencesInWorld.remove(i);
                System.out.println("Pick up a "+essence.type+"!");
            }
        }

        // --- RENDERING ---
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Draw the grid
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                batch.setColor(0.2f, 0.3f, 0.2f, 1); // Dark green
                batch.draw(whitePixel, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
        //Draw the cracked walls
        batch.setColor(0.5f,0.4f,0.3f,1);//Brownish color
        for(CrackedWall wall:walls){
            batch.draw(whitePixel,wall.x*TILE_SIZE,wall.y*TILE_SIZE,TILE_SIZE,TILE_SIZE);
        }

        //Draw Essence in the world
        batch.setColor(1,1,0,0.7f);//glowing yellow
        for(Essence essence:essencesInWorld){
            //drawing it a bit smaller and centered
            int offset = TILE_SIZE/4;
            batch.draw(whitePixel,essence.x*TILE_SIZE+offset,essence.y*TILE_SIZE+offset,TILE_SIZE/2,TILE_SIZE/2);
        }

        for(Plant plant : plants){
            if (plant instanceof BombPlant){
                batch.setColor(0.8f,0.1f,0.1f,1);
            }
            else if (plant instanceof SunKissedVinePlant){
                batch.setColor(1,0.9f,0.2f,1);//Golden yellow
                batch.draw(whitePixel,plant.x*TILE_SIZE,plant.y*TILE_SIZE,TILE_SIZE,TILE_SIZE);

            }

            else if(plant.isMature()){
                //let's draw a mature vine as a larger,bright green square
                batch.setColor(0.1f,0.8f,0.2f,1);
                batch.draw( whitePixel,plant.x * TILE_SIZE, plant.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            } else {
                // Draw an immature plant as a small, brown square
                batch.setColor(0.4f, 0.3f, 0.2f, 1);
                // We can make it smaller by drawing it with an offset and a smaller size
                int offset = TILE_SIZE / 4;
                batch.draw(whitePixel,plant.x * TILE_SIZE + offset, plant.y * TILE_SIZE + offset, TILE_SIZE / 2, TILE_SIZE / 2);
            }
        }




        // Draw the player
        batch.setColor(1, 1, 0, 1); // A bright yellow color
        batch.draw(
            whitePixel,
            player.x * TILE_SIZE,
            player.y * TILE_SIZE,
            TILE_SIZE,
            TILE_SIZE
        );

        batch.end();
    }
    private boolean isTileOccupied(int x,int y){
        for (Plant plant:plants){
            if (plant.x == x && plant.y ==y){
                return true;
            }
        }
        return false;

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        whitePixel.dispose();
        batch.dispose();
    }
}
