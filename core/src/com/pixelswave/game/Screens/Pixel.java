package com.pixelswave.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;

class Pixel {

    private Texture texture;

    private ParticleEffect particleEffect;

    private float x;
    private float y;
    private float scale;
    private float velocity;

    private int steps;

    private boolean horizontal = true;

    Pixel(float x, float y) {
        this.x = x;
        this.y = y;
        texture = new Texture(Gdx.files.internal("Particle.png"));
        scale = Gdx.graphics.getHeight() / 20f / texture.getWidth();
        velocity = (float) Math.random() * Gdx.graphics.getHeight() / 130 + Gdx.graphics.getHeight() / 130;
        steps = (int) (Math.random() * 40f + 20);

        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("PixelsTrack"), Gdx.files.internal(""));
        particleEffect.scaleEffect(scale);
        particleEffect.start();

    }
    void Update(float delta) {

        if(horizontal) {
            x += velocity;
            if(x + texture.getWidth() * scale >= Gdx.graphics.getWidth() || x <= 0) {
                x = (x <= 0) ? 0 : Gdx.graphics.getWidth() - texture.getWidth() * scale;
                velocity *= -1;
            }
            if(steps == 0) {
                horizontal = false;
                steps = (int) (Math.random() * 30 + 20);
                if(!(x + texture.getWidth() * scale >= Gdx.graphics.getWidth() || x <= 0)) velocity *= (Math.random() < 0.5) ? -1 : 1;
            }
        } else {
            y += velocity;
            if(y + texture.getHeight() * scale >= Gdx.graphics.getHeight() || y <= 0) {
                y = (y <= 0) ? 0 : Gdx.graphics.getHeight() - texture.getHeight() * scale;
                velocity *= -1;
            }
            if(steps == 0) {
                horizontal = true;
                steps = (int) (Math.random() * 40f + 20);
                if(!(y + texture.getHeight() * scale >= Gdx.graphics.getHeight() || y <= 0)) velocity *= (Math.random() < 0.5) ? -1 : 1;
            }
        }
        particleEffect.setPosition(x + texture.getWidth() / 2 * scale, y + texture.getHeight() / 2 * scale);
        particleEffect.update(delta);
        steps --;
    }

    void Draw(Batch batch) {
        particleEffect.draw(batch);
        batch.draw(texture, x, y, 0, 0, texture.getWidth(), texture.getHeight(), scale, scale, 0, 0, 0, texture.getWidth(), texture.getHeight(), false, false);
    }

    boolean GetHorizontal() {
        return horizontal;

    }

    float GetX() {
        return x;

    }

    float GetY() {
        return y;

    }

    float GetSteps() {
        return steps;

    }
}
