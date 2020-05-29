package com.pixelswave.game.Enemies;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

class SpawnPoint {

    private TiledMap map;
    private float x;
    private float y;
    private int i;

    SpawnPoint(TiledMap map) {
        this.map = map;
        i = (int) (Math.random() * 4);
        x = 0;
        y = 0;
    }

    Vector2 Generate() {

        for (MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            if (i == 0) {
//                x = (float) (Math.random() * rect.getWidth() + rect.getX());
//                y = (float) (Math.random() * rect.getHeight() + rect.getY());
                x = rect.getX() + rect.getWidth() / 2;
                y = rect.getY() + rect.getHeight() / 2;
                break;
            }
            i --;
        }
        return new Vector2(x, y);
    }

}
