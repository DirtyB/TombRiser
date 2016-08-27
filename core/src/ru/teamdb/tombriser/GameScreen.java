package ru.teamdb.tombriser;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by boris_0mrym3f on 16.04.2016.
 */
public class GameScreen implements Screen {

    private static boolean DEBUG = false;

    public static final float   WORLD_WIDTH = 20.84f;
    public static final float   WORLD_HEIGHT = 5;

    public static final int MIN_VISIBLE_WORLD_WIDTH = 5;
    public static final int MIN_VISIBLE_WORLD_HEIGHT = 5;


    public static final Vector2 WORLD_GRAVITY = new Vector2(0, -10);

    public static final float TIME_STEP = 1/60f;
    public static final int VELOCITY_ITERATIONS = 6;
    public static final int POSITION_ITERATIONS = 2;

    public final TombRiserGame game;

    private OrthographicCamera camera;
    private Viewport viewport;

    private final World world;
    private float accumulator = 0;
    private final Box2DDebugRenderer debugRenderer;

    Ball ball;
    Ground ground;

    private Sprite mapSprite;

    public GameScreen(final TombRiserGame game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.position.set(new Vector2(WORLD_WIDTH*0.5f,WORLD_HEIGHT*0.5f),0);
        viewport = new ExtendViewport(MIN_VISIBLE_WORLD_WIDTH,MIN_VISIBLE_WORLD_HEIGHT,WORLD_WIDTH,WORLD_HEIGHT,camera);

        world = new World(WORLD_GRAVITY, true);
        debugRenderer = new Box2DDebugRenderer();

        ball = new Ball(this, new Vector2(WORLD_WIDTH*0.5f, WORLD_HEIGHT*0.5f));
        ground = new Ground(this);

        ball.getBody().applyAngularImpulse(0.05f,true);

        Texture mapTexture = new Texture(Gdx.files.internal("map.png"));
        mapSprite = new Sprite(mapTexture);
        mapSprite.setPosition(0,0);
        mapSprite.setSize(WORLD_WIDTH,WORLD_HEIGHT);

    }

    @Override
    public void render(float deltaTime) {
        // clear the screen with a dark blue color. The
        // arguments to glClearColor are the red, green
        // blue and alpha component in the range [0,1]
        // of the color to be used to clear the screen.
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //lookOnCharacter(character);
        // tell the camera to update its matrices.
        camera.update();

        if(!DEBUG) {

            // tell the SpriteBatch to render in the
            // coordinate system specified by the camera.
            game.batch.setProjectionMatrix(camera.combined);

            // begin a new batch and draw the bucket and
            // all drops
            game.batch.begin();

            mapSprite.draw(game.batch);
            ground.draw(game.batch);
            ball.draw(game.batch);

            game.batch.end();
        }
        else {
            debugRenderer.render(world, camera.combined);
        }

        /*game.shapeRenderer.setProjectionMatrix(camera.combined);

        game.shapeRenderer.begin();
        game.shapeRenderer.circle(character.getX(),character.getY(),10);
        game.shapeRenderer.end();*/

       /* int direction = 1;
        if(Gdx.input.isKeyPressed(Input.Keys.UP)){
            character.stepForward(delta);
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            character.stepForward(-delta);
            direction = -1;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            character.rotateLeft(direction*delta);
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            character.rotateLeft(direction*-delta);
        }*/

        doPhysicsStep(deltaTime);

    }

    private void doPhysicsStep(float deltaTime) {
        // fixed time step
        // max frame time to avoid spiral of death (on slow devices)

        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        while (accumulator >= TIME_STEP) {
            world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            //float x1 = ball.getBody().getPosition().x;
            //float y1 = ball.getBody().getPosition().y;
            //System.out.println("" + x1 + " " + y1 + " " + x2 + " " + y2);
            accumulator -= TIME_STEP;
        }
    }

    /*public void lookOnCharacter(Character character){

        float characterX = character.getOriginPositionX();
        float characterY = character.getOriginPositionY();

        float viewportWidth = viewport.getWorldWidth();
        float viewportHeight = viewport.getWorldHeight();

        float camX = MathUtils.clamp(characterX,viewportWidth*0.5f,WORLD_WIDTH-viewportWidth*0.5f);
        float camY = MathUtils.clamp(characterY,viewportHeight*0.5f,WORLD_HEIGHT-viewportHeight*0.5f);

        camera.position.set(new Vector2(camX,camY),0);

    }*/

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        world.dispose();
        debugRenderer.dispose();
    }

    public World getWorld() {
        return world;
    }

}