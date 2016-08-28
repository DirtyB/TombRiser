package ru.teamdb.tombriser;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by boris_0mrym3f on 28.08.2016.
 */
public abstract class PullableObject extends GameObject {

    public static final float PULLING_FORCE = 8f;

    public PullableObject(GameScreen gameScreen, Vector2 position) {
        super(gameScreen, position);
    }

    public void pull(Vector2 ufoCenter){
        Vector2 uc = ufoCenter.cpy();
        Vector2 center = body.getWorldCenter();
        Vector2 direction = uc.sub(center);
        float angle = direction.angle();
        if(angle >= 70 && angle <= 110){
            body.applyForceToCenter(direction.nor().scl(PULLING_FORCE),true);
        }
    }


}
