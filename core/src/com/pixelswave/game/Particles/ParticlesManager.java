package com.pixelswave.game.Particles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pixelswave.game.Info;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ParticlesManager {

    private List<Particles> particlesList;

    public ParticlesManager() {
        particlesList = new ArrayList<Particles>();
    }

    public void Update(float delta) {
        for(Iterator<Particles> iterator = particlesList.iterator(); iterator.hasNext();) {
            Particles particles = iterator.next();
            if(particles.IsDead()) {
                iterator.remove();
            } else {
                particles.Update(delta);
            }
        }
    }

    public void Draw(SpriteBatch batch) {
        for(Particles particles: particlesList) {
            particles.Draw(batch);
        }
    }

    public void New(int id, float x, float y) {
        Particles particles = new Particles(id, x, y);
        particles.GetEffect().scaleEffect(1f / Info.PPM);
        particlesList.add(particles);

    }

    public void Dispose() {

    }

}
