package com.pixelswave.game.Bullets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;
import com.pixelswave.game.Player;

public class Bullet extends BulletGenerator {

    Bullet(int id2, Player player2, World world) {
        super(id2, player2, world);

    }

    protected void Init(int id, Player player) {
        if (player.getTexture() != null) {
            texture = new Texture(Gdx.files.internal("PlayerBullet0.png"));
        } else {
            switch (id) {
                case 0:
                    break;
                case 1:
                    break;
            }
        }
    }

}
