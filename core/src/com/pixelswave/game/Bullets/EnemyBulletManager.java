package com.pixelswave.game.Bullets;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.pixelswave.game.Enemies.Enemy;
import com.pixelswave.game.Info;

import java.util.ArrayList;
import java.util.List;

public class EnemyBulletManager {

    private World world;
    private RayHandler shadows;

    private List<EnemyBullet> bulletList;
    private List<PointLight> bulletsLight;

    private Sound shotSound;

    private int bulletsNr;

    public EnemyBulletManager(World world, RayHandler shadows) {
        super();
        this.world = world;
        this.shadows = shadows;

        shotSound = Gdx.audio.newSound(Gdx.files.internal("Shot.wav"));
        bulletList = new ArrayList<EnemyBullet>();
        bulletsLight = new ArrayList<PointLight>();
    }

    public void Update() {

        for(EnemyBullet enemyBullet: bulletList) {
            enemyBullet.Update();
        }

        SweepDeadBodies();
    }

    public void Draw(SpriteBatch batch) {

        for(EnemyBullet enemyBullet: bulletList) {
            enemyBullet.Draw(batch);
        }
    }

    public void NewBullet(Enemy enemy) {
        bulletList.add(new EnemyBullet(0, enemy, world));
        shotSound.play((enemy.GetDistanceFromPlayer() > 20 * Info.tileSize) ? 0 : 1 - enemy.GetDistanceFromPlayer() / (20 * Info.tileSize));
        enemy.SetShooting(false);
        bulletsNr ++;
        bulletsLight.add(new PointLight(shadows, 100, Color.valueOf("#dd2c2c"), Info.tileSize, 0, 0));
        bulletsLight.get(bulletsNr - 1).attachToBody(bulletList.get(bulletsNr - 1).GetBody());
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
