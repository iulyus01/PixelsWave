package com.pixelswave.game.Enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.pixelswave.game.Explosions.ExplosionManager;
import com.pixelswave.game.Info;

public class Enemy extends EnemyGenerator {

    Enemy(World world, int id, boolean boss, TiledMap map, ExplosionManager explosionManager) {
        super(world, id, boss, map, explosionManager);

    }

    protected void Init(int id, boolean boss) {
        if(!boss) {
            switch(id) {
                case 0:
                    InitEnemy0();
                    break;
                case 1:
                    InitEnemy1();
                    break;
                case 2:
                    InitEnemy2();
                    break;
            }
        } else {
            switch(id) {
                case 0:
                    break;
            }
        }
    }

    private void InitEnemy0() {
        texture1 = new Texture(Gdx.files.internal("Enemy0.png"));
        texture2 = new Texture(Gdx.files.internal("SquareRing.png"));
        texturesNr = 2;

        explosionAtlas = new TextureAtlas(Gdx.files.internal("Enemy0ExplosionAtlas.atlas"));
        textureRegionString = "Enemy0Explosion";

        tilesSizeNr = 1;
        speed = 0.5f;
        rotationScale2 = (float) (Math.random() * 2 + 3) * ((Math.random() > 0.5) ? 1 : - 1);
        hitpoints = 10;

        width1 = texture1.getWidth();
        height1 = texture1.getHeight();
        width2 = texture2.getWidth();
        height2 = texture2.getHeight();
        scale = Info.tileSize * tilesSizeNr / width2;
        explosionScale = 2 * Info.tileSize / explosionAtlas.findRegion(textureRegionString + "1").packedWidth;
        additionX1 = - width1 / 2;
        additionY1 = - height1 / 2;
        additionX2 = - width2 / 2;
        additionY2 = - height2 / 2;
        originX1 = width1 / 2;
        originY1 = height1 / 2;
        originX2 = width2 / 2;
        originY2 = height2 / 2;

        shape = new PolygonShape();
        shape.set(new float[]{
                - width1 / 2 * scale, height1 / 2 * scale,
                - width1 / 2 * scale, - height1 / 2 * scale,
                width1 / 2 * scale, - height1 / 2 * scale,
                width1 / 2 * scale, height1 / 2 * scale
        });

        pointsValue = 5;
    }
    private void InitEnemy1() {

        texture1 = new Texture(Gdx.files.internal("SquareRing1.png"));
        texture2 = new Texture(Gdx.files.internal("Enemy1Turret.png"));
        texturesNr = 2;
        explosionAtlas = new TextureAtlas(Gdx.files.internal("Enemy0ExplosionAtlas.atlas"));
        textureRegionString = "Enemy0Explosion";

        tilesSizeNr = 1.2f;
        speed = 0.4f;
        rotationScale1 = (float) (Math.random() * 4);
        hitpoints = 20;

        width1 = texture1.getWidth();
        height1 = texture1.getHeight();
        width2 = texture2.getWidth();
        height2 = texture2.getHeight();
        scale = Info.tileSize * tilesSizeNr / width1;
        explosionScale = 1.5f * Info.tileSize / explosionAtlas.findRegion(textureRegionString + "1").packedWidth;
        additionX1 = - width1 / 2;
        additionY1 = - height1 / 2;
        additionX2 = - width2 / 4;
        additionY2 = - height2 / 2;
        originX1 = width1 / 2;
        originY1 = height1 / 2;
        originX2 = width2 / 4;
        originY2 = height2 / 2;

        shape = new PolygonShape();
        shape.set(new float[]{
                - width2 / 2 * scale, height2 / 4 * scale,
                - width2 / 2 * scale, - height2 / 4 * scale,
                width2 / 2 * scale, - height2 / 4 * scale,
                width2 / 2 * scale, height2 / 4 * scale
        });

        pointsValue = 10;
    }
    private void InitEnemy2() {

        texture1 = new Texture(Gdx.files.internal("Enemy2Ring.png"));
        texture2 = new Texture(Gdx.files.internal("Enemy2Head.png"));
        texture3 = new Texture(Gdx.files.internal("Enemy2Core.png"));
        texturesNr = 3;
        explosionAtlas = new TextureAtlas(Gdx.files.internal("Enemy0ExplosionAtlas.atlas"));
        textureRegionString = "Enemy0Explosion";

        tilesSizeNr = 0.6f;
        speed = 1f * 5;
        rotationScale3 = (float) (Math.random() * 2 + 3) * ((Math.random() > 0.5) ? 1 : - 1);
        hitpoints = 5;

        width1 = texture1.getWidth();
        height1 = texture1.getHeight();
        width2 = texture2.getWidth();
        height2 = texture2.getHeight();
        width3 = texture3.getWidth();
        height3 = texture3.getHeight();
        scale = Info.tileSize * tilesSizeNr / width1;
        explosionScale = 0.8f * Info.tileSize / explosionAtlas.findRegion(textureRegionString + "1").packedWidth;
        additionX1 = - width1 / 2;
        additionY1 = - height1 / 2;
        additionX2 = 0;
        additionY2 = - height1 / 2;
        additionX3 = - width3 / 2;
        additionY3 = - height3 / 2;
        originX1 = width1 / 2;
        originY1 = height1 / 2;
        originX2 = 0;
        originY2 = height1 / 2;
        originX3 = width3 / 2;
        originY3 = height3 / 2;

        circleShape = new CircleShape();
        circleShape.setRadius(width1 / 2 * scale);

        particles1 = new ParticleEffect();
        particles1.load(Gdx.files.internal("Enemy2Particles"), Gdx.files.internal(""));
        particles1.scaleEffect(scale * 1.5f);
        particles1.start();

        pointsValue = 20;
    }

}
