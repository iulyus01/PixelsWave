package com.pixelswave.game.Explosions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;

public abstract class ExplosionGenerator {

    private World world;
    private ArrayList<Shrapnel> shrapnelList;
    PolygonShape shape;

    private float x;
    private float y;

    private int id;

    private boolean dead = false;

    Texture texture;

    float width;
    float height;
    float scale;
    float originX;
    float originY;
    float speed;
    float distanceLimit;

    int shrapnelNr = 0;
    int damage;

    ExplosionGenerator(World world, int id, float x, float y) {
        super();
        this.world = world;
        this.id = id;
        Init(id);

        shrapnelList = new ArrayList<Shrapnel>();
        float angle = 0;
        for(int i = 0; i < shrapnelNr; i ++) {
            float x2 = x + (float) Math.cos(Math.toRadians(angle)) * 2 * texture.getWidth() * scale;
            float y2 = y + (float) Math.sin(Math.toRadians(angle)) * 2 * texture.getHeight() * scale;
            shrapnelList.add(new Shrapnel(world, shape, texture, angle, distanceLimit, speed, scale, x2, y2, damage));
            angle += 360f / shrapnelNr;
        }
    }

    protected void Init(int id) {}

    public void Update() {
        for(int i = 0; i < shrapnelNr; i ++) {
            shrapnelList.get(i).Update();
        }
        System.out.println("dead before");
        SweepDeadBodies();
        System.out.println("dead after");
    }

    public void Draw(SpriteBatch batch) {
        for(int i = 0; i < shrapnelNr; i ++) {
            shrapnelList.get(i).Draw(batch, scale);

        }
    }

    public boolean GetDead() {
        return dead;

    }

    private void SweepDeadBodies() {
        for(int i = 0; i < shrapnelNr; i ++) {
            if(shrapnelList.get(i).GetDead()) {
                shrapnelList.get(i).GetBody().setUserData(null);
                shrapnelList.get(i).Destroy();
                shrapnelList.remove(i);
                shrapnelNr --;
            }
            if(shrapnelNr == 0) {
                dead = true;
            }
        }
    }

}
