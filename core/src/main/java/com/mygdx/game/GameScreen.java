package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

public class GameScreen implements Screen {
    private final MyGdxGame game;

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Viewport viewport;

    public static final int GRID_WIDTH = 20;
    public static final int GRID_HEIGHT = 15;
    public static final int TILE_SIZE = 32;

    private Texture whitePixel;
    private Player player;
    private Texture tileTexture;
    private Texture characterTexture;
    private Texture objectTexture;
    private Texture sunEssencesTexture;

    private ArrayList<Plant> plants;
    private ArrayList<CrackedWall> walls;
    private static final int BOMB_KEY = Input.Keys.B;
    private ArrayList<String> inventorySeeds;
    private ArrayList<String> inventoryEssences;
    private ArrayList<Essence> essencesInWorld;

    private static final int COMBINE_KEY = Input.Keys.C;
    private HeartseedTree heartseedTree;
    private ArrayList<Essence> heartEssenceInWorld;
    private ArrayList<String> inventoryHeartEssences;
    private enum GameState { PLAYING, WIN }
    private GameState currentState = GameState.PLAYING;

    private Sound plantSound;
    private Sound pickupSound;
    private Sound explosionSound;
    private Sound winSound;

    public GameScreen(final MyGdxGame game) {
        this.game = game;
    }

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

        // Load textures
        tileTexture = new Texture(Gdx.files.internal("img/grass_2.png"));
        characterTexture = new Texture(Gdx.files.internal("img/Wizard_1Attack1.png"));
        objectTexture = new Texture(Gdx.files.internal("img/sprBrick.gif"));
        sunEssencesTexture = new Texture(Gdx.files.internal("img/SunEssence.png"));

        // --- CORRECTED SOUND LOADING ---
        // Using the standard file names from the Kenney asset pack
        //plantSound = Gdx.audio.newSound(Gdx.files.internal("sfx/planting a seed.ogg"));
        //pickupSound = Gdx.audio.newSound(Gdx.files.internal("sfx/planting a seed.ogg"));
        //explosionSound = Gdx.audio.newSound(Gdx.files.internal("sfx/wall_break.ogg"));
        //winSound = Gdx.audio.newSound(Gdx.files.internal("sfx/win.ogg"));

        // Initialize game objects
        player = new Player(GRID_WIDTH / 2, GRID_HEIGHT / 2);
        plants = new ArrayList<>();
        walls = new ArrayList<>();
        inventorySeeds = new ArrayList<>();
        inventoryEssences = new ArrayList<>();
        essencesInWorld = new ArrayList<>();
        heartEssenceInWorld = new ArrayList<>();
        inventoryHeartEssences = new ArrayList<>();

        // Place the Heartseed Tree in the center
        heartseedTree = new HeartseedTree(GRID_WIDTH / 2, GRID_HEIGHT / 2);

        // Give the player a starting seed
        inventorySeeds.add("Vine");
        inventorySeeds.add("Bomb");

        // Place a Sun Essence in the world
        essencesInWorld.add(new Essence(15, 10, "SunEssence"));

        // Puzzle setup
        // Puzzle 1: A wall blocks a path to a Heart Essence
        walls.add(new CrackedWall(5, 5));
        walls.add(new CrackedWall(6, 5));
        walls.add(new CrackedWall(7, 5));
        walls.add(new CrackedWall(10, 5));
        walls.add(new CrackedWall(8, 8));
        walls.add(new CrackedWall(9, 8));
        heartEssenceInWorld.add(new Essence(10, 8, "HeartEssence"));

        // Puzzle 2: A gap blocks a path to another Heart Essence.
        heartEssenceInWorld.add(new Essence(3, 12, "HeartEssence"));
    }

    @Override
    public void render(float delta) {
        // --- INPUT HANDLING ---
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            if (player.y < GRID_HEIGHT - 1) player.y++;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            if (player.y > 0) player.y--;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            if (player.x > 0) player.x--;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            if (player.x < GRID_WIDTH - 1) player.x++;
        }

        if (Gdx.input.isKeyJustPressed(COMBINE_KEY)) {
            Grimoire.combine(inventorySeeds, inventoryEssences);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            if (!isTileOccupied(player.x, player.y) && !inventorySeeds.isEmpty()) {
                String seedToPlant = inventorySeeds.get(0);
                Plant newPlant = PlantFactory.createPlant(seedToPlant, player.x, player.y);
                if (newPlant != null) {
                    plants.add(newPlant);
                    plantSound.play();
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(BOMB_KEY)) {
            if (!isTileOccupied(player.x, player.y)) {
                plants.add(PlantFactory.createPlant("Bomb", player.x, player.y));
            }
        }

        // --- GAME LOGIC UPDATES ---
        ArrayList<Plant> plantsToRemove = new ArrayList<>();
        for (Plant plant : plants) {
            plant.grow();
            if (plant instanceof BombPlant && plant.isMature() && !((BombPlant) plant).hasExploded()) {
                ((BombPlant) plant).explode(walls);
                plantsToRemove.add(plant);
                //explosionSound.play();
            }
        }
        plants.removeAll(plantsToRemove);

        // Check for Sun Essence pickups
        for (int i = essencesInWorld.size() - 1; i >= 0; i--) {
            Essence essence = essencesInWorld.get(i);
            if (essence.x == player.x && essence.y == player.y) {
                inventoryEssences.add(essence.type);
                essencesInWorld.remove(i);
                //pickupSound.play();
                System.out.println("Pick up a " + essence.type + "!");
            }
        }

        // Check for Heart Essence pickups
        for (int i = heartEssenceInWorld.size() - 1; i >= 0; i--) {
            Essence essence = heartEssenceInWorld.get(i);
            if (essence.x == player.x && essence.y == player.y) {
                inventoryHeartEssences.add(essence.type);
                heartEssenceInWorld.remove(i);
                //pickupSound.play(); // --- ADDED MISSING SOUND ---
                System.out.println("You found a " + essence.type + "!");
            }
        }

        // Check for the win condition
        if (player.x == heartseedTree.x && player.y == heartseedTree.y && inventoryHeartEssences.size() == 2) {
            if (currentState == GameState.PLAYING) {
                System.out.println("The Heartseed Tree has been restored!");
                heartseedTree.bloom();
                currentState = GameState.WIN;
                //winSound.play();
                System.out.println("=================================");
                System.out.println("   YOU DID NOT JUST TEND A GARDEN;   ");
                System.out.println("     YOU GAVE IT BACK ITS HEART.     ");
                System.out.println("              THANK YOU.              ");
                System.out.println("=================================");
            }
        }

        // --- RENDERING ---
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        if (currentState == GameState.PLAYING) {
            // Draw the grid
            for (int x = 0; x < GRID_WIDTH; x++) {
                for (int y = 0; y < GRID_HEIGHT; y++) {
                    batch.draw(tileTexture, x * TILE_SIZE, y * TILE_SIZE);
                }
            }

            // Draw the cracked walls
            for (CrackedWall wall : walls) {
                batch.draw(objectTexture, wall.x * TILE_SIZE, wall.y * TILE_SIZE);
            }

            // Draw Sun Essences
            for (Essence essence : essencesInWorld) {
                batch.draw(sunEssencesTexture, essence.x * TILE_SIZE, essence.y * TILE_SIZE);
            }

            // Draw Heart Essences
            for (Essence essence : heartEssenceInWorld) {
                batch.setColor(0.8f, 0.2f, 0.8f, 1); // Magenta color
                batch.draw(sunEssencesTexture, essence.x * TILE_SIZE, essence.y * TILE_SIZE);
                batch.setColor(1, 1, 1, 1); // Reset color
            }

            // Draw plants
            for (Plant plant : plants) {
                if (plant instanceof BombPlant) {
                    batch.setColor(0.8f, 0.1f, 0.1f, 1);
                    batch.draw(whitePixel, plant.x * TILE_SIZE, plant.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    batch.setColor(1, 1, 1, 1);
                } else if (plant instanceof SunKissedVinePlant) {
                    batch.setColor(1, 0.9f, 0.2f, 1);
                    batch.draw(whitePixel, plant.x * TILE_SIZE, plant.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    batch.setColor(1, 1, 1, 1);
                } else if (plant.isMature()) {
                    batch.setColor(0.1f, 0.8f, 0.2f, 1);
                    batch.draw(whitePixel, plant.x * TILE_SIZE, plant.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    batch.setColor(1, 1, 1, 1);
                } else {
                    batch.setColor(0.4f, 0.3f, 0.2f, 1);
                    int offset = TILE_SIZE / 4;
                    batch.draw(whitePixel, plant.x * TILE_SIZE + offset, plant.y * TILE_SIZE + offset, TILE_SIZE / 2, TILE_SIZE / 2);
                    batch.setColor(1, 1, 1, 1);
                }
            }

            // Draw the Heartseed Tree
            if (heartseedTree.isBloomed()) {
                batch.setColor(0.2f, 1, 0.5f, 1); // Vibrant green
            } else {
                batch.setColor(0.4f, 0.4f, 0.4f, 1); // Grey, petrified
            }
            batch.draw(whitePixel, heartseedTree.x * TILE_SIZE, heartseedTree.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            batch.setColor(1, 1, 1, 1); // Reset color

            // Draw the player
            batch.draw(characterTexture, player.x * TILE_SIZE, player.y * TILE_SIZE - 16);
        } else if (currentState == GameState.WIN) {
            // Draw the win screen
            batch.setColor(0, 0, 0, 0.7f); // Semi-transparent black background
            batch.draw(whitePixel, 0, 0, GRID_WIDTH * TILE_SIZE, GRID_HEIGHT * TILE_SIZE);

            // --- CORRECTED SYNTAX ---
            if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
                game.setScreen(new GameScreen(game));
                dispose();
            }
        }

        batch.end();
    }

    private boolean isTileOccupied(int x, int y) {
        for (Plant plant : plants) {
            if (plant.x == x && plant.y == y) {
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
        tileTexture.dispose();
        characterTexture.dispose();
        objectTexture.dispose();
        sunEssencesTexture.dispose();

        // Dispose of sounds
       // plantSound.dispose();
        //pickupSound.dispose();
        //explosionSound.dispose();
        //winSound.dispose();

        batch.dispose();
    }
}
