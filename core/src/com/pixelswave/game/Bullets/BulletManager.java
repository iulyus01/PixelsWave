package com.pixelswave.game.Bullets;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.pixelswave.game.Info;
import com.pixelswave.game.Player;

import java.util.ArrayList;
import java.util.List;

public class BulletManager {

    private World world;
    private Player player;
    private RayHandler shadows;

    private List<Bullet> bulletList;
    private List<PointLight> bulletsLight;

    private Sound shotSound;

    private int bulletsNr;
    
    public BulletManager(World world, Player player, RayHandler shadows) {
        super();
        this.world = world;
        this.player = player;
        this.shadows = shadows;

        shotSound = Gdx.audio.newSound(Gdx.files.internal("Shot.wav"));
        bulletList = new ArrayList<Bullet>();
        bulletsLight = new ArrayList<PointLight>();
    }
    
    public void Update() {

        if(player.GetShooting()) {
            bulletList.add(new Bullet(bulletsNr, player, world));
            shotSound.play();
            player.SetShooting(false);
            bulletsNr ++;
            bulletsLight.add(new PointLight(shadows, 100, Color.valueOf("#5C5CE5"), Info.tileSize, 0, 0));
            bulletsLight.get(bulletsNr - 1).attachToBody(bulletList.get(bulletsNr - 1).GetBody());
//            new PointLight(shadows, 100, Color.valueOf("#5C5CE5"), Info.tileSize, 0, 0).attachToBody(bulletList.get(bulletsNr - 1).GetBody());
        }

        for(Bullet bullet: bulletList) {
            bullet.Update();
        }

        SweepDeadBodies();
    }
    
    public void Draw(SpriteBatch batch) {

        for(Bullet bullet: bulletList) {
            bullet.Draw(batch);
        }
    }

    private void SweepDeadBodies() {
        for (int i = 0; i < bulletsNr; i ++) {
            if (bulletList.get(i).GetDead()) {
                bulletList.get(i).GetBody().setUserData(null);
                bulletList.get(i).Destroy();

                bulletsNr --;
                bulletList.remove(i);

                bulletsLight.get(i).setActive(false);
                bulletsLight.get(i).dispose();
                bulletsLight.remove(i);

            }
        }
    }

    public void Dispose() {

    }
}
