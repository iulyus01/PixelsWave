package com.pixelswave.game.Bonus;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import javafx.beans.property.MapProperty;
import com.pixelswave.game.Info;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BonusManager {

    private World world;
    private TiledMap map;

    private List<Bonus> bonusList;

    private float delay = 0;
    private float maxDelay = 5;
    private float x;
    private float y;
    private float scale;

    private int spawned = 0;
    private int maxSpawned = 5;

    public BonusManager(World world, TiledMap map) {
        this.world = world;
        this.map = map;
        bonusList = new ArrayList<Bonus>();

        scale = Info.tileSize / map.getProperties().get("tilewidth", Integer.class);
    }

    private void Add() {
        int areaId = (int) (Math.random() * 8);


        for (MapObject object: map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
            if(areaId > 0) {
                areaId --;
                continue;
            }
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            x = (rect.x + Info.tileSize / scale + (float) Math.random() * (rect.width - 2 * Info.tileSize / scale)) * scale;
            y = (rect.y + Info.tileSize / scale + (float) Math.random() * (rect.height - 2 * Info.tileSize / scale)) * scale;
            break;

        }
        bonusList.add(new Bonus(world, 0, x, y));
    }

    public void Update(float delta) {
        delay += delta;
        if(delay >= maxDelay && spawned < maxSpawned) {
            Add();
            spawned ++;
            delay = 0;
        }
        for(Bonus bonus: bonusList) {
            bonus.Update();
        }

        SweepDeadBodies();
    }

    public void Draw(SpriteBatch batch) {
        for(Bonus bonus: bonusList) {
            bonus.Draw(batch);
        }
    }

    private void SweepDeadBodies() {
        for(int i = 0; i < spawned; i ++) {
           if(bonusList.get(i).GetDead() && bonusList.get(i).GetAnimationId() <= 2) {
               bonusList.get(i).Destroy();
               bonusList.get(i).GetBody().setUserData(null);
               bonusList.remove(i);
               spawned --;
           }
        }
    }

}
