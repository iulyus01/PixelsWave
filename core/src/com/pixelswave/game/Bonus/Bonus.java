package com.pixelswave.game.Bonus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;

public class Bonus extends BonusGenerator {

    Bonus(World world, int id, float x, float y) {
        super(world, id, x, y);
    }

    protected void Init(int id) {
        switch(id) {
            case 0:
                texture = new Texture(Gdx.files.internal("HPBonus.png"));
                break;
        }
    }

}
