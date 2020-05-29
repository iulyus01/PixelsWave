package com.pixelswave.game.Enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.pixelswave.game.Bullets.EnemyBulletManager;
import com.pixelswave.game.Explosions.Explosion;
import com.pixelswave.game.Explosions.ExplosionManager;
import com.pixelswave.game.Filters;
import com.pixelswave.game.Info;
import com.pixelswave.game.Player;
import com.pixelswave.game.SimplexNoise;

public abstract class EnemyGenerator {

    private World world;
    private Body body;
    private Fixture fixture;
    PolygonShape shape;
    CircleShape circleShape;

    private AI ai;

    private TextureRegion textureRegion;

    private ExplosionManager explosionManager;

    private Sound explosionSound;

    private float x;
    private float y;
    private float rotation1 = 0;
    private float rotation2 = 0;
    private float rotation3 = 0;
    private float animationId = 1;
    private float counter = 0;
    private float damagingDelay = 0;
    private float damagingDelayMax = 0.4f;

    private int id;
    private int shotDelay;
    private int shotMaxDelay = 1000;

    private boolean boss;
    private boolean dead = false;
    private boolean hit = false;
    private boolean drawRegion = false;
    private boolean shooting = false;
    private boolean exploded = false;
    private boolean isTouchingPlayer = false;

    private Vector2 spawn;

    TextureAtlas explosionAtlas;

    Texture texture1;
    Texture texture2;
    Texture texture3;

    ParticleEffect particles1;

    String textureRegionString;

    float speed;
    float width1 = 0;
    float width2 = 0;
    float width3 = 0;
    float height1 = 0;
    float height2 = 0;
    float height3 = 0;
    float scale;
    float explosionScale;
    float tilesSizeNr;
    float additionX1;
    float additionY1;
    float additionX2;
    float additionY2;
    float additionX3;
    float additionY3;
    float originX1;
    float originY1;
    float originX2;
    float originY2;
    float originX3;
    float originY3;
    float rotationScale1 = 0;
    float rotationScale2 = 0;
    float rotationScale3 = 0;

    int pointsValue;
    int hitpoints;
    int texturesNr;

    EnemyGenerator(World world, int id, boolean boss, TiledMap map, ExplosionManager explosionManager) {
        super();
        this.world = world;
        this.id = id;
        this.boss = boss;
        this.explosionManager = explosionManager;
        Init(id, boss);

        ai = new AI(map, id, boss);
        spawn = new SpawnPoint(map).Generate();
        x = spawn.x * (map.getProperties().get("width", Integer.class) * Info.tileSize) / (map.getProperties().get("width", Integer.class) * map.getProperties().get("tilewidth", Integer.class));
        y = spawn.y * (map.getProperties().get("height", Integer.class) * Info.tileSize) / (map.getProperties().get("height", Integer.class) * map.getProperties().get("tileheight", Integer.class));

        CreateBody();

        explosionSound = Gdx.audio.newSound(Gdx.files.internal("Explosion.wav"));

    }
    protected void Init(int id, boolean boss) {}

    private void CreateBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.linearDamping = 10;
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        if(shape != null) fixtureDef.shape = shape;
        else fixtureDef.shape = circleShape;
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        Filter filter = new Filter();
        filter.categoryBits = Filters.CategoryEnemy;
        fixture.setFilterData(filter);
        if(shape != null) shape.dispose();
        else circleShape.dispose();
    }

    public void Update(float delta, Player player, EnemyBulletManager enemyBulletManager) {
        if(hitpoints > 0) {
            ai.Update((Enemy) this, player);
            x = body.getPosition().x;
            y = body.getPosition().y;
            switch(id) {
                case 0:
                    body.applyLinearImpulse(ai.GetVelocity().x * speed, ai.GetVelocity().y * speed, body.getWorldCenter().x, body.getWorldCenter().y, true);
                    if (Math.abs(rotation2) >= 360) rotation2 = 0;
                    rotation2 += rotationScale2;
                    if(isTouchingPlayer) {
                        if(damagingDelay >= damagingDelayMax) {
                            TouchPlayer(player);
                            damagingDelay = 0;
                        }
                        damagingDelay += delta;
                    }
                    break;
                case 1:
                    body.setTransform(x, y, (float) Math.toRadians(ai.GetAngle()));
                    body.applyLinearImpulse(ai.GetVelocity().x * speed, ai.GetVelocity().y * speed, body.getWorldCenter().x, body.getWorldCenter().y, true);
                    rotation2 = ai.GetAngle();
                    rotation1 += rotationScale1 * (int) (SimplexNoise.noise(counter, rotationScale1) * 5);
                    counter += 0.01;
                    if(shotDelay > 0) shotDelay -= delta * 1000;
                    if(ai.GetAim() && shotDelay <= 0) {
                        enemyBulletManager.NewBullet((Enemy) this);
                        shotDelay = shotMaxDelay;
                    }
                    break;
                case 2:
                    body.applyForceToCenter(ai.GetVelocity().x * speed, ai.GetVelocity().y * speed, true);
                    body.setLinearDamping(0.5f);
                    rotation2 = ai.GetAngle();
                    particles1.update(delta);
                    particles1.setPosition(getX(), getY());
                    if (Math.abs(rotation3) >= 360) rotation3 = 0;
                    rotation3 += rotationScale3;
                    break;
            }
        } else {
            if(id == 2) if(!exploded) Explode();
            if(animationId == 1) {
                float distance = (float) Math.sqrt((player.getX() - getX()) * (player.getX() - getX()) + (player.getY() - getY()) * (player.getY() - getY()));
                explosionSound.play((distance > 20 * Info.tileSize) ? 0 : 1 - distance / (20 * Info.tileSize));
                width1 = explosionAtlas.findRegion("Enemy0Explosion" +Integer.toString((int) animationId)).packedWidth;
                height1 = explosionAtlas.findRegion("Enemy0Explosion" + Integer.toString((int) animationId)).packedHeight;
                x -= width1 / 2 * scale;
                y -= width1 / 2 * scale;
                body.setActive(false);
                drawRegion = true;
            }
            if(texture2 != null) texture2.dispose();
            if(texture3 != null) texture3.dispose();
            textureRegion = explosionAtlas.findRegion(textureRegionString + Integer.toString((int) animationId));
            if((int) animationId == 8) {
                SetDead();
            }
            animationId += 0.5;
        }

    }

    public void Draw(SpriteBatch batch) {
        if(particles1 != null) particles1.draw(batch);
        if(!drawRegion) {
            batch.draw(texture1, x + additionX1, y + additionY1, originX1, originY1, width1, height1, scale, scale, rotation1, 0, 0, (int) width1, (int) height1, false, false);
            if(texture2 != null)
                batch.draw(texture2, x + additionX2, y + additionY2, originX2, originY2, width2, height2, scale, scale, rotation2, 0, 0, (int) width2, (int) height2, false, false);
            if(texture3 != null)
                batch.draw(texture3, x + additionX3, y + additionY3, originX3, originY3, width3, height3, scale, scale, rotation3, 0, 0, (int) width3, (int) height3, false, false);
        } else {
            batch.draw(textureRegion, x - width1 * explosionScale / 4, y - height1 * explosionScale / 4, 0, 0, width1, height1, explosionScale, explosionScale, 0);
        }
    }

    private void Explode() {
        exploded = true;
        explosionManager.Add(new Explosion(world, 0, getX(), getY()));
        hitpoints = 0;

    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width1 * scale;
    }

    public float getHeight() {
        return height1 * scale;
    }

    public float getWidth2() {
        return width2 * scale;
    }

    public float getHeight2() {
        return height2 * scale;
    }

    public float getScale() {
        return scale;
    }

    public Body GetBody() {
        return body;
    }

    public int GetId() {
        return id;
    }

    public void Hit(int damage) {
        hitpoints -= damage;
    }

    public int GetHP() {
        return hitpoints;
    }

    public void SetDead() {
        dead = true;
    }

    public boolean GetDead() {
        return dead;
    }

    public void TouchPlayer(Player player) {
        switch(id) {
            case 0:
                isTouchingPlayer = true;
                player.Push(getX(), getY());
                player.HitBy(GetDamage());
                break;
            case 2:
                hitpoints = 0;
                break;
        }
    }

    public void NotTouchingPlayer() {
        isTouchingPlayer = false;
    }

    public int GetDamage() {
        switch(id) {
            case 0:
                return 10;
        }
        return 0;
    }

    public boolean GetShooting() {
        return shooting;
    }

    public void SetShooting(boolean shot) {
        shooting = shot;
    }

    public float GetRotation() {
        switch(id) {
            case 0:
                return rotation1;
            case 1:
                return rotation2;
        }
        return 0;
    }

    public float GetDistanceFromPlayer() {
        return ai.GetDistance();
    }

    public int GetPointsValue() {
        return pointsValue;

    }

    public void Destroy() {
        body.destroyFixture(fixture);
        world.destroyBody(body);
        explosionAtlas.dispose();
        texture1.dispose();
        if(texture2 != null) texture2.dispose();
        if(texture3 != null) texture3.dispose();
    }


}
