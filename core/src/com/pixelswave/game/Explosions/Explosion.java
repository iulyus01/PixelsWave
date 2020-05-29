package com.pixelswave.game.Explosions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.pixelswave.game.Info;

public class Explosion extends ExplosionGenerator {

    public Explosion(World world, int id, float x, float y) {
        super(world, id, x, y);
    }

    protected void Init(int id) {
        switch(id) {
            case 0:
                texture = new Texture(Gdx.files.internal("Shrapnel0.png"));

                shrapnelNr = 8;
                speed = 10;
                distanceLimit = 2 * Info.tileSize;
                damage = 10;

                width = texture.getWidth();
                height = texture.getHeight();
                scale = Info.tileSize / 5 / height;
                originX = width / 2;
                originY = height / 2;

                shape = new PolygonShape();
                shape.set(new float[]{
                        - width / 2 * scale, height / 2 * scale,
                        width / 2 * scale, 0,
                        - width / 2 * scale, - height / 2 * scale
                });
                break;
            case 1:
                break;
        }
    }

}
