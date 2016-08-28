package ru.teamdb.tombriser;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by boris_0mrym3f on 27.08.2016.
 */
public class Ufo extends GameObject {

    public static final float UFO_WIDTH = 1.5f;
    public static final float UFO_HEIGHT = 1.5f;

    public static final float UFO_VELOCITY = 2f;


    public Ufo(GameScreen gameScreen, Vector2 position) {
        super(gameScreen, position);
    }

    @Override
    protected void initSprite() {
        Texture texture = new Texture(Gdx.files.internal("ufo.gif"));
        sprite = new Sprite(texture);
        sprite.setSize(UFO_WIDTH, UFO_HEIGHT);
        sprite.setOrigin(UFO_WIDTH*0.5f, UFO_HEIGHT*0.5f );
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
        PolygonShape shape = new PolygonShape();
        // Set the polygon shape as a box which is twice the size of our view port and 20 high
        // (setAsBox takes half-width and half-height as arguments)
        shape.setAsBox(UFO_WIDTH*0.5f, UFO_HEIGHT*0.3f);
        // Create a fixture from our polygon shape and add it to our ground body
        FixtureDef fixtureDef2 = new FixtureDef();
        fixtureDef2.shape = shape;
        fixtureDef2.density = 0.5f;
        fixtureDef2.friction = 0.4f;
        fixtureDef2.restitution = 0f;

        body.createFixture(fixtureDef2);
        // Clean up after ourselves
        shape.dispose();
    }

    protected void move(Vector2 movement){
        body.setLinearVelocity(movement.scl(UFO_VELOCITY));
    }
}
