package com.pixelswave.game.Explosions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;
import com.pixelswave.game.Filters;
import com.pixelswave.game.Info;

import java.util.Set;

public class Shrapnel {

    private World world;
    private Body body;
    private Fixture fixture;

    private Texture texture;

    private float angle;
    private float x;
    private float y;
    private float distance = 0;
    private float distanceLimit;
    private float additionX;
    private float additionY;
    private int damage;

    private boolean dead = false;

    Shrapnel(World world, PolygonShape shape, Texture texture, float angle, float distanceLimit, float speed, float scale, float x, float y, int damage) {
        this.world = world;
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.distanceLimit = distanceLimit;
        this.damage = damage;
        additionX = - texture.getWidth() / 2;
        additionY = - texture.getHeight() / 2;
        CreateBody(shape, angle, speed, x, y);
    }

    private void CreateBody(PolygonShape shape, float angle, float speed, float x, float y) {

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        body = world.createBody(bodyDef);
        body.setActive(true);


        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        Filter filter = new Filter();
        filter.categoryBits = Filters.CategoryProjectile;

        fixture.setFilterData(filter);

        float impulseX = (float) Math.cos(angle) * speed;
        float impulseY = (float) Math.sin(angle) * speed;
        body.applyLinearImpulse(impulseX, impulseY, body.getWorldCenter().x, body.getWorldCenter().y, true);
        body.setTransform(x, y, (float) Math.toRadians(angle));

    }

    void Update() {
        distance += Math.sqrt((x - body.getPosition().x) * (x - body.getPosition().x) + (y - body.getPosition().y) * (y - body.getPosition().y));
        x = body.getPosition().x;
        y = body.getPosition().y;
        if(distance >= distanceLimit) {
            SetDead();
        }
    }

    void Draw(SpriteBatch batch, float scale) {
        batch.draw(texture, x + additionX, y + additionY, texture.getWidth() / 2, texture.getHeight() / 2, texture.getWidth(), texture.getHeight(), scale, scale, (float) Math.toDegrees(body.getAngle()), 0, 0, texture.getWidth(), texture.getHeight(), false, false);
    }

    public int GetDamage() {
        return damage;

    }

    public Body GetBody() {
        return body;

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
        dead = true;
    }
}
