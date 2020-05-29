package com.pixelswave.game;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;

public class Walls {

    private World world;
    private Body body;
    private Fixture fixture;

    private TiledMap map;

    private int mapLevelWidth;
    private int mapLevelHeight;
    private int levelWidth;
    private int levelHeight;
    private int wave = 1;

    public Walls(World world, TiledMap map) {
        this.world = world;
        this.map = map;

        mapLevelWidth = map.getProperties().get("width", Integer.class) * map.getProperties().get("tilewidth", Integer.class);
        mapLevelHeight = map.getProperties().get("height", Integer.class) * map.getProperties().get("tileheight", Integer.class);
        levelWidth = (int) (map.getProperties().get("width", Integer.class) * Info.tileSize);
        levelHeight = (int) (map.getProperties().get("height", Integer.class) * Info.tileSize);

        CreateBody();

    }
    private void CreateBody() {
        BodyDef bodyDef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();

        for (MapObject object : map.getLayers().get(1).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(rect.getX() * levelWidth / mapLevelWidth + rect.getWidth() * levelWidth / mapLevelWidth / 2,
                    rect.getY() * levelHeight / mapLevelHeight + rect.getHeight() * levelHeight / mapLevelHeight / 2);

            body = world.createBody(bodyDef);

            shape.setAsBox(rect.getWidth() * levelHeight / mapLevelHeight / 2, rect.getHeight() * levelHeight / mapLevelHeight / 2);
            fixtureDef.shape = shape;
            fixture = body.createFixture(fixtureDef);
            fixture.setUserData(this);
            Filter filter = new Filter();
            filter.categoryBits = Filters.CategoryWalls;
            fixture.setFilterData(filter);
        }
    }

    public void Update() {
        if(wave != Info.wave) {
            for (TiledMapTile tile : map.getTileSets().getTileSet(0)) {
                System.out.println("tile id: " + tile.getId());
            }
            map.getTileSets().getTileSet(0).getTile(1).setTextureRegion(map.getTileSets().getTileSet(0).getTile(wave + 2).getTextureRegion());
            wave = Info.wave;
        }
    }

    public void Dispose() {
        System.out.print("test 10.1");
        body.destroyFixture(fixture);
        System.out.print("test 10.2");
        world.destroyBody(body);
        System.out.print("test 10.3");
        map.dispose();

    }
}
