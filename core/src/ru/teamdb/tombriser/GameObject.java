package ru.teamdb.tombriser;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

/**
 * Created by boris_0mrym3f on 27.08.2016.
 */
public abstract class GameObject {

    protected GameScreen gameScreen;

    protected Sprite sprite;
    protected Body body;

    GameObject(GameScreen gameScreen, Vector2 position){
        this.gameScreen = gameScreen;

        initSprite();

        BodyDef bodyDef = getBodyDef();
        bodyDef.position.set(position);

        body = gameScreen.getWorld().createBody(bodyDef);

        createFixtures();
        update();
    }

    public void draw (Batch batch) {
        update();
        sprite.draw(batch);
    }

    protected abstract void initSprite();

    protected abstract BodyDef getBodyDef();

    protected abstract void createFixtures();

    public void setSpriteOriginPosition(float x, float y){
        sprite.setPosition(x-sprite.getOriginX(),y-sprite.getOriginY());
    }

    public void setSpriteOriginPosition(Vector2 position){
        setSpriteOriginPosition(position.x,position.y);
    }

    public void setSpriteRotation(float radians){
        float degrees = (float)((body.getAngle()/Math.PI)*180d);
        sprite.setRotation(degrees);
    }

    public Vector2 getSpriteOriginPosition(){
        return new Vector2(sprite.getX()+sprite.getOriginX(),sprite.getY()+sprite.getOriginY());
    }

    protected void update(){
        setSpriteOriginPosition(body.getPosition());
        setSpriteRotation(body.getAngle());
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public Body getBody() {
        return body;
    }
}
