package com.pixelswave.game;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Player extends Sprite {

    private World world;
    private Body body;
    private Fixture fixture;
    private RayHandler shadows;
    private PointLight light;

    private ParticleEffect particleEffect;
    private TextureAtlas explosionAtlas;

    private Sound hurtSound;

    private float speed = 0.7f;
    private float shotDelay = 0;
    private float shotMaxDelay = 0.3f;
    private float explosionCount = 1;
    private float lightDistance;

    private int hitpoints = 100;

    private boolean shooting = false;
    private boolean dead = false;
    private boolean shakePending = false;

    public Player(World world, RayHandler shadows) {
        super(new Texture(Gdx.files.internal("Player.png")));
        this.world = world;
        this.shadows = shadows;
        setPosition(Gdx.graphics.getWidth() / Info.PPM, Gdx.graphics.getHeight() / Info.PPM);
        setSize(Info.tileSize, Info.tileSize);
        setOriginCenter();

        CreateBody();

        lightDistance = Gdx.graphics.getHeight() * 1.1f / Info.PPM;
        light = new PointLight(shadows, 1000, Color.DARK_GRAY, lightDistance, getX(), getY());
        light.attachToBody(GetBody());
        light.setSoftnessLength(0);
        light.setSoft(true);
//        light.setIgnoreAttachedBody(true);
        Filter filter = new Filter();
        filter.categoryBits = Filters.CategoryPlayer;
        filter.maskBits = Filters.MaskPlayer;
        light.setContactFilter(filter);


        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("PlayerParticles"), Gdx.files.internal(""));
        particleEffect.scaleEffect(1f / Info.PPM);
        particleEffect.getEmitters().first().setPosition(getX(), getY());
        particleEffect.start();

        explosionAtlas = new TextureAtlas(Gdx.files.internal("PlayerExplosion.atlas"));

        hurtSound = Gdx.audio.newSound(Gdx.files.internal("Hurt.wav"));
    }

    private void CreateBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(getX(), getY());
        bodyDef.linearDamping = 10;
        body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(getWidth() / 2f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        Filter filter = new Filter();
        filter.categoryBits = Filters.CategoryPlayer;
//        filter.maskBits = Filters.MaskPlayer;
        fixture.setFilterData(filter);
        shape.dispose();
    }

    public void Update(float delta) {
        if(hitpoints > 0) {
            setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
            setRotation((float) Math.toDegrees(body.getAngle()));
            particleEffect.getEmitters().first().setPosition(getX() + getWidth() / 2, getY() + getHeight() / 2);
            particleEffect.update(delta);
            if(shotDelay > 0) shotDelay -= delta;
        } else {
            if(explosionCount < 9) {
                explosionCount += 0.5f;
            }
            if(explosionCount == 1) {
                setSize(3 * Info.tileSize, 3 * Info.tileSize);
                setPosition(getX() + Info.tileSize, getY() - Info.tileSize);
                light.setPosition(getX(), getY());
                body.destroyFixture(fixture);
            }
            setRegion(explosionAtlas.findRegion("PlayerExplosion" + (int) explosionCount));
            if(light.getDistance() - lightDistance / 25f <= 0) light.setDistance(0);
            if(light.getDistance() > 0.01) light.setDistance(light.getDistance() - lightDistance / 25f);
            else {
                dead = true;
                Dispose();
                world.destroyBody(body);
            }

        }
    }

    public void Touching1(float x, float y) {
        float angle = (float) Math.atan2(y - Info.CONTROLLER, x - Info.CONTROLLER);
        float impulseX;
        float impulseY;
        boolean distance = Math.sqrt((x - Info.CONTROLLER) * (x - Info.CONTROLLER) + (y - Info.CONTROLLER) * (y - Info.CONTROLLER)) <= Info.CONTROLLER_RADIUS;
        if (distance) {
            impulseX = (x - Info.CONTROLLER) * speed;
            impulseY = (y - Info.CONTROLLER) * speed;
        } else {
            impulseX = (float) (Info.CONTROLLER_RADIUS * Math.cos(angle)) * speed;
            impulseY = (float) (Info.CONTROLLER_RADIUS * Math.sin(angle)) * speed;
        }

        body.applyLinearImpulse(new Vector2(impulseX / Info.PPM, impulseY / Info.PPM), body.getWorldCenter(), true);
    }
    public void Touching2() {
        if (shotDelay <= 0) shooting = true;

    }
    public void SetAngle(float x, float y, boolean refByShooter) {
        float angle;
        if(refByShooter) {
            angle = (float) Math.atan2(y - Info.SHOOTING_CONTROLLER_Y, x - Info.SHOOTING_CONTROLLER_X);
        } else {
            angle = (float) Math.atan2(y - Info.CONTROLLER, x - Info.CONTROLLER);
        }
        body.setTransform(body.getPosition().x, body.getPosition().y, angle);
    }

    public void Draw(SpriteBatch batch) {
        particleEffect.draw(batch);
        draw(batch);


    }

    public Body GetBody() {
        return body;

    }

    public boolean GetShooting() {
        return shooting;

    }

    public void SetShooting(boolean state) {
        shooting = state;
        if (!state) shotDelay = shotMaxDelay;
    }

    public void HitBy(int damage) {
        if(hitpoints > 0) hitpoints -= damage;
        if(hitpoints < 0) hitpoints = 0;
        shakePending = true;
        hurtSound.play();


    }

    public int GetHp() {
        return hitpoints;

    }

    public boolean GetLife() {
        return dead;
    }

    public int GetFullHp() {
        return 100;

    }

    public void Heal(int hp) {
        if(hitpoints <= 100 - hp) hitpoints += hp;
        else hitpoints = 100;
    }

    public void Push(float x, float y) {
        float impulseX = (body.getPosition().x - x) * 10 * speed;
        float impulseY = (body.getPosition().y - y) * 10 * speed;
        body.applyLinearImpulse(new Vector2(impulseX, impulseY), body.getWorldCenter(), true);
    }

    public void Dispose() {
        particleEffect.dispose();
        explosionAtlas.dispose();
        hurtSound.dispose();
    }

    public boolean Shaking() {
        return shakePending;

    }

    public void SetShaking(boolean set) {
        shakePending = set;
    }
}
