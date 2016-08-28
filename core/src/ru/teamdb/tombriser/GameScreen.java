package ru.teamdb.tombriser;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by boris_0mrym3f on 16.04.2016.
 */
public class GameScreen implements Screen {

    private static boolean DEBUG = false;

    public static final float   WORLD_WIDTH = 14f;
    public static final float   WORLD_HEIGHT = 5;

    public static final int MIN_VISIBLE_WORLD_WIDTH = 5;
    public static final int MIN_VISIBLE_WORLD_HEIGHT = 5;

    public static int HUMAN_COUNT = 6;

    public static final Vector2 winPoint = new Vector2(WORLD_WIDTH*0.5f, /*Ground.GROUND_HEIGHT*/ WORLD_HEIGHT*0.5f);


    public static final Vector2 WORLD_GRAVITY = new Vector2(0, -10);

    public static final float TIME_STEP = 1/60f;
    public static final int VELOCITY_ITERATIONS = 6;
    public static final int POSITION_ITERATIONS = 2;

    public final TombRiserGame game;

    boolean isLightOn;

    private OrthographicCamera camera;
    private Viewport viewport;

    private final World world;
    private float accumulator = 0;
    private final Box2DDebugRenderer debugRenderer;

    private boolean isPaused = false;

    Tutan tutan;
    Ground ground;
    Ufo ufo;
    Light light;

    List<Human> humans;
    List<Stone> stones;

    Cloud cloudObject, cloudObjectBig;

    private Sprite mapSprite;
    private Sprite winSprite;

    private Sprite finishSprite;


    public GameScreen(final TombRiserGame game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.position.set(new Vector2(WORLD_WIDTH*0.5f,WORLD_HEIGHT*0.5f),0);
        viewport = new ExtendViewport(MIN_VISIBLE_WORLD_WIDTH,MIN_VISIBLE_WORLD_HEIGHT,WORLD_WIDTH,WORLD_HEIGHT,camera);

        world = new World(WORLD_GRAVITY, true);
        debugRenderer = new Box2DDebugRenderer();

        tutan = new Tutan(this, new Vector2(WORLD_WIDTH*0.5f, WORLD_HEIGHT*0.3f));
        //tutan.body.applyLinearImpulse(0,-0.01f,0,0,true);
        ground = new Ground(this);
        ufo = new Ufo(this, new Vector2(WORLD_WIDTH*0.5f, WORLD_HEIGHT*0.8f));
        light = new Light(this, new Vector2(WORLD_WIDTH*0.5f , WORLD_HEIGHT*0.8f - 2),6);

        cloudObject = new Cloud(this, new Vector2(WORLD_WIDTH*0.5f+5, WORLD_HEIGHT*0.5f+2), 1) ;
        cloudObject.getBody().setLinearVelocity(-0.2f, 0.0f);

        cloudObjectBig = new Cloud(this, new Vector2(WORLD_WIDTH*0.5f+5, WORLD_HEIGHT*0.5f+1), 1) ;
        cloudObjectBig.getBody().setLinearVelocity(-0.4f, 0.0f);

        tutan.getBody().applyAngularImpulse(0,false);

        Texture mapTexture = new Texture(Gdx.files.internal("map1.png"));
        mapSprite = new Sprite(mapTexture);
        mapSprite.setPosition(0,0);
        mapSprite.setSize(WORLD_WIDTH,WORLD_HEIGHT);

        Texture winTexture = new Texture(Gdx.files.internal("win.png"));
        winSprite = new Sprite(winTexture);
        winSprite.setPosition(winPoint.x,winPoint.y);
        winSprite.setSize(0.3f,0.39f);

        Texture finishTexture = new Texture(Gdx.files.internal("finish.png"));
        finishSprite = new Sprite(finishTexture);
        finishSprite.setPosition(winPoint.x- 3.45f/2,winPoint.y-3.35f/2 );
        finishSprite.setSize(3.45f,3.35f);

        humans = new ArrayList<Human>(HUMAN_COUNT);
        for(int i = 0; i<HUMAN_COUNT/2; i++){
            humans.add(new Human(this, 0 - (i+1)*2));
        }
        for(int i = HUMAN_COUNT/2; i<HUMAN_COUNT; i++){
            humans.add(new Human(this, WORLD_WIDTH + (i- HUMAN_COUNT/2 +1)*2));
        }

        stones = new LinkedList<Stone>();

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
            winSprite.draw(game.batch);
            cloudObject.draw(game.batch);
            cloudObjectBig.draw(game.batch);
            ground.draw(game.batch);
            tutan.draw(game.batch);

            for(Stone stone: stones){
                stone.draw(game.batch);
            }

            for(Human human: humans){
                human.draw(game.batch);
            }

            if (isLightOn) {
                light.draw(game.batch);
            }


            ufo.draw(game.batch);

            if(isPaused){
                finishSprite.draw(game.batch);
            }

            game.batch.end();
        }
        else {
            debugRenderer.render(world, camera.combined);
        }

        if(!isPaused) {

            Transform cloudTransform = cloudObject.getBody().getTransform();
            Vector2 cloudCenter = cloudTransform.getPosition();
            if (cloudCenter.x < -2) {
                cloudObject.getBody().setTransform(WORLD_WIDTH + 1, cloudCenter.y, cloudTransform.getRotation());

            }

            cloudTransform = cloudObjectBig.getBody().getTransform();
            cloudCenter = cloudTransform.getPosition();
            if (cloudCenter.x < -2) {
                cloudObjectBig.getBody().setTransform(WORLD_WIDTH + 1, cloudCenter.y, cloudTransform.getRotation());

            }

            for (Human human : humans) {
                human.makeStep();
            }


            moveUfo();
            light();
            lookOnUfo();

            if (checkCompleted()) {
                win();
            }

            doPhysicsStep(deltaTime);
        }

    }

    private void doPhysicsStep(float deltaTime) {
        // fixed time step
        // max frame time to avoid spiral of death (on slow devices)

        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        while (accumulator >= TIME_STEP) {
            world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            accumulator -= TIME_STEP;
        }
    }

    protected void win(){
        pause();
    }


    protected void light(){

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)){

            Vector2 ufoCenter = ufo.getBody().getWorldCenter();
            tutan.pull(ufoCenter);
            for(Stone stone: stones){
                stone.pull(ufoCenter);
            }

            isLightOn = true;

        } else {
            isLightOn = false;
        }

    }


    protected void moveUfo() {


        Vector2 movement = new Vector2(0, 0);


        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            movement.y = 1;


        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            movement.y = -1;

        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            movement.x = -1;

        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            movement.x = 1;


        }

        /* Проверка на экран */
        if (ufo.getBody().getPosition().y <= WORLD_HEIGHT - 0.4f && ufo.getBody().getPosition().x <= WORLD_WIDTH - 0.7f &&
                ufo.getBody().getPosition().y >= 0.4f &&   ufo.getBody().getPosition().x >= 0.7f  ) {


            ufo.move(movement);
        } else if(ufo.getBody().getPosition().y >= WORLD_HEIGHT -0.4f){
            movement.y = -0.05f;
            ufo.move(movement);
        }
        else if(ufo.getBody().getPosition().y <= 0.4f){
            movement.y = 0.05f;
            ufo.move(movement);

        }
        else if(ufo.getBody().getPosition().x <=  0.7f){
            movement.x = 0.05f;
            ufo.move(movement);
        }
        else if(ufo.getBody().getPosition().x >= WORLD_WIDTH-0.7f){
            movement.x = -0.05f;
            ufo.move(movement);
        }
        light.getBody().setTransform(ufo.getBody().getPosition().x,ufo.getBody().getPosition().y,0);


    }

    protected boolean checkCompleted(){
        return  tutan.sprite.getBoundingRectangle().overlaps( winSprite.getBoundingRectangle() )
                && (tutan.body.getLinearVelocity().isZero())
                && !isLightOn;
    }


    protected void lookOnUfo(){

        Vector2 position = ufo.getSpriteOriginPosition();

        float viewportWidth = viewport.getWorldWidth();

        float camX = MathUtils.clamp(position.x,viewportWidth*0.5f,WORLD_WIDTH-viewportWidth*0.5f);

        camera.position.set(new Vector2(camX,camera.position.y),0);

    }

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
        isPaused = true;
    }

    @Override
    public void resume() {
        isPaused = false;
    }

    @Override
    public void dispose() {
        world.dispose();
        debugRenderer.dispose();
    }

    public World getWorld() {
        return world;
    }

    public List<Stone> getStones(){
        return stones;
    }

}