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
public class Ground extends GameObject {

    public static float GROUND_HEIGHT = 0.2f;

    public Ground(GameScreen gameScreen) {
        super(gameScreen, new Vector2(GameScreen.WORLD_WIDTH*0.5f, GROUND_HEIGHT*0.5f));
    }

    @Override
    protected void initSprite() {
        Texture texture = new Texture(Gdx.files.internal("ground.png"));
        sprite = new Sprite(texture);
        sprite.setSize(getGroundWidth(), GROUND_HEIGHT);
        sprite.setOrigin(getGroundWidth()*0.5f, GROUND_HEIGHT*0.5f);
    }

    @Override
    protected BodyDef getBodyDef() {
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.type = BodyDef.BodyType.StaticBody;
        return groundBodyDef;
    }

    @Override
    protected void createFixtures() {
        // Create a polygon shape
        PolygonShape groundBox = new PolygonShape();
        // Set the polygon shape as a box which is twice the size of our view port and 20 high
        // (setAsBox takes half-width and half-height as arguments)
        groundBox.setAsBox(getGroundWidth()*0.5f, GROUND_HEIGHT*0.5f);
        // Create a fixture from our polygon shape and add it to our ground body
        FixtureDef fixtureDef2 = new FixtureDef();
        fixtureDef2.shape = groundBox;
        fixtureDef2.density = 0.5f;
        fixtureDef2.friction = 0.4f;
        fixtureDef2.restitution = 1f;

        body.createFixture(fixtureDef2);
        // Clean up after ourselves
        groundBox.dispose();

    }

    private float getGroundWidth(){
        return GameScreen.WORLD_WIDTH*4;
    }
}
