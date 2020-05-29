package com.pixelswave.game.Bonus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.*;
import com.pixelswave.game.Filters;
import com.pixelswave.game.Info;

abstract class BonusGenerator extends Sprite {

    private World world;
    private Body body;
    private Fixture fixture;
    private TextureAtlas atlas;
    Texture texture;

    private Sound pickupSound;

    private float x;
    private float y;
    private float animationId = 1;

    private int id;

    private boolean dead = false;

    BonusGenerator(World world, int id, float x, float y) {
        super(new Texture(Gdx.files.internal("HPBonus.png")));
        this.world = world;
        this.id = id;
        this.x = x;
        this.y = y;
        Init(id);

        atlas = new TextureAtlas(Gdx.files.internal("BonusAnimationAtlas.atlas"));
        setRegion(atlas.findRegion("BonusAnimation1"));
        setScale(Info.tileSize / getRegionWidth());
        setOrigin(0, 0);
        setPosition(x, y);
        CreateBody();

        pickupSound = Gdx.audio.newSound(Gdx.files.internal("Pickup.wav"));
    }

    protected void Init(int id) {}

    private void CreateBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
//        shape.setAsBox(getWidth() / 2 * getScaleX(), getHeight() / 2 * getScaleY());
        shape.set(new float[]{
                0, getHeight() * getScaleY(),
                0, 0,
                getWidth() * getScaleX(), 0,
                getWidth() * getScaleX(), getHeight() * getScaleY()
        });
        fixtureDef.shape = shape;
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        fixture.setSensor(true);
        Filter filter = new Filter();
        filter.categoryBits = Filters.CategoryBonus;
        fixture.setFilterData(filter);
        shape.dispose();

        body.setTransform(x, y, 0);
    }

    public void Update() {
        if(!GetDead()) {
            if(animationId < 6) {
                animationId += 0.2f;
            }
            setRegion(atlas.findRegion("BonusAnimation" + (int) animationId));
        } else {
            if(animationId >= 6) {
                atlas.dispose();
                atlas = new TextureAtlas(Gdx.files.internal("BonusAnimationOut.atlas"));
                System.out.println("test new texture atlas");
            }
            if(animationId >= 3) {
                animationId -= 0.2f;
            }
            if(atlas != null) {
                System.out.println("test " + ("BonusAnimationOut" + (int) (8 - animationId)) + " " + animationId);
                setRegion(atlas.findRegion("BonusAnimationOut" + (int) (7 - animationId)));
            }
        }
    }

    public void Draw(SpriteBatch batch) {
        draw(batch);
        batch.draw(texture, getX() + getWidth() / 4 * getScaleX(), getY() + getHeight() / 4 * getScaleY(), 0, 0, texture.getWidth(), texture.getHeight(), getScaleX() / 2, getScaleY() / 2, 0, 0, 0, texture.getWidth(), texture.getHeight(), false, false);
    }

    public int GetId() {
        return id;

    }

    public int GetBonus() {
        switch(id) {
            case 0:
                return 10;
            case 1:
                return 1;
        }
        return 0;
    }

    public Body GetBody() {
        return body;

    }

    public void SetDead() {
        dead = true;

    }

    public boolean GetDead() {
        return dead;

    }

    public int GetAnimationId() {
        return (int) animationId;
    }

    public void PlaySound() {
        pickupSound.play();
    }

    public void Destroy() {
        body.destroyFixture(fixture);
        world.destroyBody(body);
        atlas.dispose();
        texture.dispose();
        pickupSound.dispose();

    }



}
