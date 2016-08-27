package ru.teamdb.tombriser;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;

/**
 * Created by boris_0mrym3f on 27.08.2016.
 */
public class Ball extends GameObject {

    protected static float RADIUS = 0.3f;

    public Ball(GameScreen gameScreen, Vector2 position) {
        super(gameScreen, position);
    }

    @Override
    protected void initSprite() {
        Texture texture = new Texture(Gdx.files.internal("badlogic.jpg"));
        sprite = new Sprite(texture);
        sprite.setSize(2* RADIUS,2* RADIUS);
        sprite.setOrigin(RADIUS, RADIUS);
    }

    @Override
    protected BodyDef getBodyDef() {
        BodyDef bodyDef = new BodyDef();
        // We set our ballObject to dynamic, for something like ground which doesn't move we would set it to StaticBody
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        return bodyDef;
    }

    @Override
    protected void createFixtures() {
        CircleShape circle = new CircleShape();
        circle.setRadius(RADIUS);

        // Create a fixture definition to apply our shape to
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 1f; // Make it bounce a little bit

        // Create our fixture and attach it to the ballObject
        Fixture fixture = body.createFixture(fixtureDef);

        // Remember to dispose of any shapes after you're done with them!
        // BodyDef and FixtureDef don't need disposing, but shapes do.
        circle.dispose();
    }
}
