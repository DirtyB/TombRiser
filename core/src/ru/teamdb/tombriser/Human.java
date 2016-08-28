package ru.teamdb.tombriser;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by boris_0mrym3f on 27.08.2016.
 */
public class Human extends GameObject {

    public static final float HUMAN_WIDTH = 19 * 0.02f;
    public static final float HUMAN_HEIGHT = 28 * 0.02f;

    public static final float HUMAN_VELOCITY = 1f;

    Map<String,Texture> textures;

    HumanState state = HumanState.SEEKING;
    Direction direction = Direction.RIGHT;

    Stone pulledStone;
    Joint stoneJoint;


    public Human(GameScreen gameScreen, float positionX) {
        super(gameScreen, new Vector2(positionX,Ground.GROUND_HEIGHT));
        seekForStone();
    }

    @Override
    protected void initSprite() {
        textures = new HashMap<String, Texture>();
        textures.put("pulling", new Texture(Gdx.files.internal("man.png")));
        textures.put("seeking", new Texture(Gdx.files.internal("man2.png")));
        sprite = new Sprite(textures.get("seeking"));
        sprite.setSize(HUMAN_WIDTH, HUMAN_HEIGHT);
        sprite.setOrigin(HUMAN_WIDTH *0.5f, 0 );
    }

    @Override
    protected BodyDef getBodyDef() {
        BodyDef bodyDef = new BodyDef();
        // We set our ball to dynamic, for something like ground which doesn't move we would set it to StaticBody
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        return bodyDef;
    }

    @Override
    protected void createFixtures() {
        // Create a polygon shape
        /*PolygonShape shape = new PolygonShape();
        // Set the polygon shape as a box which is twice the size of our view port and 20 high
        // (setAsBox takes half-width and half-height as arguments)
        //shape.setAsBox(HUMAN_WIDTH *0.5f, HUMAN_HEIGHT *0.3f,new Vector2(0, HUMAN_HEIGHT*0.5f),0);
        Vector2[] vertices = new Vector2[4];
        vertices[0] = new Vector2(HUMAN_WIDTH *0.5f,0);
        vertices[1] = new Vector2(HUMAN_WIDTH *0.5f,HUMAN_HEIGHT);
        vertices[2] = new Vector2(-HUMAN_WIDTH *0.5f,HUMAN_HEIGHT);
        vertices[3] = new Vector2(-HUMAN_WIDTH *0.5f,0);
        shape.set(vertices);
        // Create a fixture from our polygon shape and add it to our ground body
        FixtureDef fixtureDef2 = new FixtureDef();
        fixtureDef2.shape = shape;
        fixtureDef2.density = 0.5f;
        fixtureDef2.friction = 0.4f;
        fixtureDef2.restitution = 1f;

        body.createFixture(fixtureDef2);
        // Clean up after ourselves
        shape.dispose();*/
    }

    public void makeStep(){
        switch (state){
            case SEEKING:
                makeStepSeeking();
                break;
            case PULLING:
                makeStepPulling();
        }
    }

    protected void makeStepSeeking(){
        float x = body.getTransform().getPosition().x;
        //System.out.println(x);
        float offset = 1f;
        if(x > - offset && x < GameScreen.WORLD_WIDTH + offset){
            float movementX = direction.equals(Direction.RIGHT) ? HUMAN_VELOCITY : -HUMAN_VELOCITY;
            move(new Vector2(movementX,0f));
        }
        else {
            waitForStone();
        }
    }

    protected void makeStepPulling(){
        float x = body.getTransform().getPosition().x;
        //System.out.println(x);
        float offset = 3f;
        float halfWordWidth =  GameScreen.WORLD_WIDTH * 0.5f;
        if(x < halfWordWidth  - offset || x > halfWordWidth + offset){
            float movementX = direction.equals(Direction.RIGHT) ? HUMAN_VELOCITY : -HUMAN_VELOCITY;
            move(new Vector2(movementX * 0.5f,0f));
        }
        else {
            releaseStone();
        }
    }

    protected void seekForStone(){
        state = HumanState.SEEKING;
        direction = (body.getTransform().getPosition().x < GameScreen.WORLD_WIDTH*0.5) ? Direction.LEFT : Direction.RIGHT;
        sprite.setTexture(textures.get("seeking"));
        sprite.setFlip( direction.equals(Direction.RIGHT), false );
    }

    protected void waitForStone(){
        state = HumanState.WAITING;
        stop();
        pulledStone = null;
        //// TODO: 28.08.2016
        pullStone();
    }

    protected void pullStone(){
        state = HumanState.PULLING;
        direction = (body.getTransform().getPosition().x < GameScreen.WORLD_WIDTH*0.5) ? Direction.RIGHT : Direction.LEFT;

        float stoneOffset = (HUMAN_WIDTH + Stone.STONE_WIDTH)*0.5f + 0.1f;
        float stonePosition = body.getTransform().getPosition().x;
        stonePosition += direction.equals(Direction.RIGHT) ? -stoneOffset : stoneOffset;
        pulledStone = new Stone(gameScreen, stonePosition);
        gameScreen.getStones().add(pulledStone);
        pulledStone.setPullingHuman(this);

        sprite.setTexture(textures.get("pulling"));
        sprite.setFlip( direction.equals(Direction.RIGHT), false );

        RopeJointDef jointDef = new RopeJointDef ();
        jointDef.bodyA = body;
        jointDef.bodyB = pulledStone.getBody();

        Vector2 point1 = new Vector2(0,HUMAN_HEIGHT*0.7f);
        Vector2 point2 = new Vector2( Stone.STONE_WIDTH*0.5f* (direction.equals(Direction.RIGHT) ? 1 : -1) ,0);

        jointDef.localAnchorA.set(point1);
        jointDef.localAnchorB.set(point2);

        jointDef.maxLength = point1.add(body.getTransform().getPosition()).dst(point2.add(pulledStone.getBody().getTransform().getPosition()));
        stoneJoint = gameScreen.getWorld().createJoint(jointDef);
    }

    public void releaseStone(){
        if(stoneJoint == null){
            return;
        }
        gameScreen.getWorld().destroyJoint(stoneJoint);
        stoneJoint = null;
        pulledStone.setPullingHuman(null);
        pulledStone = null;
        seekForStone();
    }


    protected void move(Vector2 movement){
        body.setLinearVelocity(movement.scl(HUMAN_VELOCITY));
    }

    protected void stop(){
        body.setLinearVelocity(new Vector2(0,0));
    }
}
