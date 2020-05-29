package com.pixelswave.game.Bullets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.*;
import com.pixelswave.game.Filters;
import com.pixelswave.game.Info;
import com.pixelswave.game.Player;

public abstract class BulletGenerator extends Sprite {

    private World world;
    private Body body;
    private Fixture fixture;

    private Player player;

    private TextureAtlas bulletDeadAtlas;

    private float distanceTraveled = 0;
    private float initialPosX = 0;
    private float initialPosY = 0;
    private float animationId = 1;

    private int id;

    private boolean dead = false;

    Texture texture;

    BulletGenerator(int id, Player player, World world) {
        super(new Texture(Gdx.files.internal("PlayerBullet0.png")));
        this.world = world;
        this.player = player;
        this.id = id;

        Init(id, player);
        float plusX = player.getWidth() * (float) Math.cos(Math.toRadians(player.getRotation())) + player.getWidth() / 2;
        float plusY = player.getHeight() * (float) Math.sin(Math.toRadians(player.getRotation())) + player.getHeight() / 2;
//        setOrigin(0, 0);
        setOriginCenter();
        setScale(Info.tileSize / texture.getWidth());
        setPosition(player.getX() + plusX, player.getY() + plusY);
        setRotation(player.getRotation());
        CreateBody();
    }

    protected void Init(int id, Player player) {}

    private void CreateBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(getX(), getY());
        bodyDef.linearDamping = 0;
        body = world.createBody(bodyDef);
        body.setBullet(true);
        body.setTransform(getX(), getY(), (float) Math.toRadians(getRotation()));
        body.setLinearVelocity((float) (Info.CONTROLLER_RADIUS * Math.cos(Math.toRadians(getRotation())) / Info.PPM) * 15, (float) (Info.CONTROLLER_RADIUS * Math.sin(Math.toRadians(getRotation())) / Info.PPM) * 15);

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(texture.getWidth() / 2 * getScaleX() / 4, texture.getHeight() / 2 * getScaleY() / 4);
        fixtureDef.shape = shape;
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        Filter filter = new Filter();
        filter.categoryBits = Filters.CategoryProjectile;

        fixture.setFilterData(filter);
        shape.dispose();
    }

    public void Update() {
        if(animationId == 1) setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        if(initialPosX != 0) distanceTraveled += Math.sqrt((getX() - initialPosX) * (getX() - initialPosX) + (getY() - initialPosY) * (getY() - initialPosY));
        if(distanceTraveled > 6 * Info.tileSize) {
            if(animationId == 1) {
                bulletDeadAtlas = new TextureAtlas(Gdx.files.internal("BulletDeadAtlas.atlas"));

                setY(getY() - (float) Math.sin(Math.toRadians(90 - getRotation())) * (getScaleX() * getWidth() / 4));
                setX(getX() + (float) Math.cos(Math.toRadians(90 - getRotation())) * (getScaleX() * getWidth() / 4));
                body.setActive(false);
            }
            setRegion(bulletDeadAtlas.findRegion("PlayerBulletDead" + (int) animationId));
            setSize(getWidth(), getWidth());

            animationId += 0.3;
            if((int) (animationId - 0.6) == 4) {
                SetDead();
            }
        }
        initialPosX = getX();
        initialPosY = getY();
    }

    public void Draw(SpriteBatch batch) {
        draw(batch);
    }

    public Body GetBody() {
        return body;
    }

    public int GetId() {
        return id;
    }

    public int GetDamage() {
        return 10;
    }

    public void SetId(int newId) {
        id = newId;
    }

    public boolean GetDead() {
        return dead;
    }

    public void SetDead() {
        dead = true;
    }

    public void Destroy() {
        body.destroyFixture(fixture);
        world.destroyBody(body);
        if(bulletDeadAtlas != null) bulletDeadAtlas.dispose();
        texture.dispose();

    }



}

