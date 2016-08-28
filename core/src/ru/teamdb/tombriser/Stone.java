package ru.teamdb.tombriser;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by boris_0mrym3f on 27.08.2016.
 */
public class Stone extends GameObject {


    public static final float STONE_WIDTH = 2 * 0.7f;
    public static final float STONE_HEIGHT = 0.7f;

    public Stone(GameScreen gameScreen, float positionX) {
        super(gameScreen, new Vector2(positionX, Ground.GROUND_HEIGHT+ STONE_HEIGHT*0.5f));
    }

    @Override
    protected void initSprite() {
        Texture texture = new Texture(Gdx.files.internal("stone.png"));
        sprite = new Sprite(texture);
        sprite.setSize(STONE_WIDTH, STONE_HEIGHT);
        sprite.setOrigin(STONE_WIDTH*0.5f, STONE_HEIGHT*0.5f );
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
        groundBox.setAsBox(STONE_WIDTH*0.5f, STONE_HEIGHT*0.5f );
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
