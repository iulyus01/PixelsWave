package com.pixelswave.game.Bullets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.pixelswave.game.Enemies.Enemy;
import com.pixelswave.game.Info;

public class EnemyBullet extends EnemyBulletGenerator {

    EnemyBullet(int id2, Enemy enemy2, World world) {
        super(id2, enemy2, world);

    }

    protected void Init(int id, Enemy enemy) {
        texture = new Texture(Gdx.files.internal("EnemyBullet0.png"));

        float plusX = enemy.getWidth() / 4 * 3 * (float) Math.cos(Math.toRadians(enemy.GetRotation()));
        float plusY = enemy.getHeight() / 4 * 3 * (float) Math.sin(Math.toRadians(enemy.GetRotation()));

        setOriginCenter();
        setScale(Info.tileSize / texture.getWidth());
        setRotation(enemy.GetRotation());
        setPosition(enemy.getX() + plusX, enemy.getY() + plusY);

        switch(id) {
            case 0:
                setTexture(new Texture(Gdx.files.internal("EnemyBullet0.png")));

                deadAtlasString = "EnemyBullet0DeadAtlas.atlas";
                deadAtlasAnimationString = "EnemyBullet0Dead";

                tilesReach = 6;
                speed = 8;
                damage = 5;

                shape = new PolygonShape();
                shape.setAsBox(texture.getWidth() / 2 * getScaleX() / 4, texture.getHeight() / 2 * getScaleY() / 4);
                break;
        }
    }

}
