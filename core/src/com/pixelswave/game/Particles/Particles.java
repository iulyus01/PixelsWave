package com.pixelswave.game.Particles;

import com.badlogic.gdx.Gdx;

class Particles extends ParticlesGenerator {

    Particles(int id, float x, float y) {
        super(id, x, y);

    }

    protected void Init(int id) {
        switch(id) {
            case 0:

                break;
            case 1:
                particleEffect.load(Gdx.files.internal("DestroyedParticles"), Gdx.files.internal(""));
                break;
        }
    }

}
