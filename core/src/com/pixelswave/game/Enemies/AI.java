package com.pixelswave.game.Enemies;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pixelswave.game.Info;
import com.pixelswave.game.Player;

class AI {

    private TiledMap map;
    private Enemy enemy;
    private Player player;

    private Polygon polygon;

    private Vector2 velocityDirection;

    private float angle;

    private int id;

    private boolean boss;
    private boolean aimed;


    AI(TiledMap map, int id, boolean boss) {
        this.map = map;
        this.id = id;
        this.boss = boss;

        float mapWidth = map.getProperties().get("width", Integer.class) * map.getProperties().get("tilewidth", Integer.class);
        float mapHeight = map.getProperties().get("height", Integer.class) * map.getProperties().get("tileheight", Integer.class);
        float width = map.getProperties().get("width", Integer.class) * Info.tileSize;
        float height = map.getProperties().get("height", Integer.class) * Info.tileSize;
        velocityDirection = new Vector2();
        if(!boss) {
            switch(id) {
                case 1:
                    polygon = new Polygon();
                    break;
            }
        }
    }

    void Update(Enemy enemy, Player player) {
        this.enemy = enemy;
        this.player = player;
        angle = (float) Math.atan2((player.getY() + player.getHeight() / 2) - (enemy.getY() + enemy.getHeight() * enemy.getScale() / 2), (player.getX() + player.getWidth() / 2) - (enemy.getX() + enemy.getWidth() * enemy.getScale() / 2));
        if(!boss) {
            switch(id) {
                case 0:
                    velocityDirection.x = (float) (Info.CONTROLLER_RADIUS * Math.cos(angle)) / Info.PPM;
                    velocityDirection.y = (float) (Info.CONTROLLER_RADIUS * Math.sin(angle)) / Info.PPM;
                    break;
                case 1:
                    velocityDirection.x = (float) (Info.CONTROLLER_RADIUS * Math.cos(angle)) / Info.PPM;
                    velocityDirection.y = (float) (Info.CONTROLLER_RADIUS * Math.sin(angle)) / Info.PPM;
                    if(Math.sqrt((enemy.getX() - player.getX()) * (enemy.getX() - player.getX()) + (enemy.getY() - player.getY()) * (enemy.getY() - player.getY())) > 6 * Info.tileSize) break;
                    for(MapObject object : map.getLayers().get(1).getObjects().getByType(RectangleMapObject.class)) {
                        Rectangle rect = ((RectangleMapObject) object).getRectangle();
                        polygon.setVertices(new float[]{
                                rect.x, rect.y,
                                rect.x + rect.width, rect.y,
                                rect.x + rect.width, rect.y + rect.height,
                                rect.x, rect.y + rect.height
                        });
                        if(Intersector.intersectSegmentPolygon(new Vector2(enemy.getX(), enemy.getY()), new Vector2(player.getX(), player.getY()), polygon)) {
                            velocityDirection.x = (float) (Info.CONTROLLER_RADIUS * Math.cos(angle)) / Info.PPM;
                            velocityDirection.y = (float) (Info.CONTROLLER_RADIUS * Math.sin(angle)) / Info.PPM;
                            aimed = false;
                            break;
                        } else {
                            velocityDirection.x = (float) (Info.CONTROLLER_RADIUS / 4 * Math.cos(angle + Math.toRadians(180))) / Info.PPM;
                            velocityDirection.y = (float) (Info.CONTROLLER_RADIUS / 4 * Math.sin(angle + Math.toRadians(180))) / Info.PPM;
                            aimed = true;
                        }

                    }
                    break;
                case 2:
                    velocityDirection.x = (float) (Info.CONTROLLER_RADIUS * Math.cos(angle)) / Info.PPM;
                    velocityDirection.y = (float) (Info.CONTROLLER_RADIUS * Math.sin(angle)) / Info.PPM;
                    break;
            }
        }
    }

    Vector2 GetVelocity() {
        return velocityDirection;
    }

    int GetAngle() {
        return (int) Math.toDegrees(angle);
    }

    boolean GetAim() {
        return aimed;
    }

    float GetDistance() {
        return (float) Math.sqrt((player.getX() - enemy.getX()) * (player.getX() - enemy.getX()) + (player.getY() - enemy.getY()) * (player.getY() - enemy.getY()));
    }

}
