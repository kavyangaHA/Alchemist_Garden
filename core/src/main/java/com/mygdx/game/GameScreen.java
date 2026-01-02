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

        if(Gdx.input.isKeyJustPressed(Input.Keys.P)){
            //Let's plant a vine seed!
            //First we need to check the tile is occupied already
            boolean tileOccupied = false;
            for (Plant plant : plants){
                if(plant.x == player.x && plant.y == player.y){
                    tileOccupied = true;
                    break;
                }
            }

            //If the tile is not occupied,plant a new one
            if(!tileOccupied){
                plants.add(new VinePlant(player.x,player.y));
            }
        }

        //update all plants
        for (Plant plant:plants){
            plant.grow();
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

        for(Plant plant : plants){
            if(plant.isMature()){
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
