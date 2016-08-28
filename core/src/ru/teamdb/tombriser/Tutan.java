package ru.teamdb.tombriser;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

/**
 * Created by boris_0mrym3f on 27.08.2016.
 */
public class Tutan extends PullableObject {

    public static final float TUTAN_WIDTH = 20 * 0.02f;
    public static final float TUTAN_HEIGHT = 51 * 0.02f;

    public Tutan(GameScreen gameScreen, Vector2 position) {
        super(gameScreen, position);
    }

    @Override
    protected void initSprite() {
        Texture texture = new Texture(Gdx.files.internal("sarcophagus.png"));
        sprite = new Sprite(texture);
        sprite.setSize(TUTAN_WIDTH, TUTAN_HEIGHT);
        sprite.setOrigin(TUTAN_WIDTH *0.5f, TUTAN_HEIGHT *0.5f );
    }

    @Override
    protected BodyDef getBodyDef() {
        BodyDef bodyDef = new BodyDef();
        // We set our ball to dynamic, for something like ground which doesn't move we would set it to StaticBody
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        return bodyDef;
    }

    @Override
    protected void createFixtures() {
        // Create a polygon shape
        PolygonShape groundBox = new PolygonShape();
        // Set the polygon shape as a box which is twice the size of our view port and 20 high
        // (setAsBox takes half-width and half-height as arguments)
        groundBox.setAsBox(TUTAN_WIDTH *0.5f, TUTAN_HEIGHT *0.5f );
        // Create a fixture from our polygon shape and add it to our ground body
        FixtureDef fixtureDef2 = new FixtureDef();
        fixtureDef2.shape = groundBox;
        fixtureDef2.density = 0.5f;
        fixtureDef2.friction = 0.4f;
        fixtureDef2.restitution = 0f;

        body.createFixture(fixtureDef2);
        // Clean up after ourselves
        groundBox.dispose();

    }


}
