package com.pixelswave.game.Explosions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExplosionManager {

    private World world;
    private List<Explosion> explosionList;

    public ExplosionManager(World world) {
        this.world = world;
        explosionList = new ArrayList<Explosion>();
    }

    public void Add(Explosion explosion) {
        explosionList.add(explosion);
    }

    public void Update() {
        for(Iterator<Explosion> iterator = explosionList.iterator(); iterator.hasNext();) {
            Explosion explosion = iterator.next();
            if(explosion.GetDead()) {
                iterator.remove();
                continue;
            }
            explosion.Update();
        }
    }

    public void Draw(SpriteBatch batch) {
        for(Explosion explosion: explosionList) {
            explosion.Draw(batch);
        }
    }

    public void Dispose() {

    }
}
