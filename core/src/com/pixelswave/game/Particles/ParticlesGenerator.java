package com.pixelswave.game.Particles;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class ParticlesGenerator {

    ParticleEffect particleEffect;

    private int id;

    private boolean dead = false;

    ParticlesGenerator(int id, float x, float y) {
        super();
        this.id = id;

        particleEffect = new ParticleEffect();
        Init(id);

        particleEffect.getEmitters().first().setPosition(x, y);
        particleEffect.start();
    }

    protected void Init(int id) {}

    public void Update(float delta) {
        particleEffect.update(delta);
        switch(id) {
            case 0:
                break;
            case 1:

                if(particleEffect.isComplete()) {
                    particleEffect.dispose();
                    dead = true;
                }
                break;
        }
    }

    public void Draw(SpriteBatch batch) {
        particleEffect.draw(batch);
    }

    public boolean IsDead() {
        return dead;
    }

    public ParticleEffect GetEffect() {
        return particleEffect;
    }
}
