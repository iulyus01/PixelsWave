package com.pixelswave.game.Enemies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.World;
import com.pixelswave.game.Bullets.EnemyBulletManager;
import com.pixelswave.game.Explosions.ExplosionManager;
import com.pixelswave.game.Info;
import com.pixelswave.game.Particles.ParticlesManager;
import com.pixelswave.game.Player;

import java.util.ArrayList;
import java.util.List;

public class EnemyManager {

    private World world;
    private TiledMap map;

    private List<Enemy> enemyList;
    private List<Boolean> particlesGenerated;

    private Player player;

    private ParticlesManager particlesManager;
    private ExplosionManager explosionManager;

    private int wave = 1;
    private int delay = 0;
    private int delayMax = 200;
    private int enemiesNr = 0;
    private int enemiesMaxNr = 5;
    private int enemiesPerWave = 20;
    private int enemiesSpawned = 0;
    private int enemiesKilled = 0;

    private boolean summoned = false;

    public EnemyManager(World world, TiledMap map, Player player, ParticlesManager particlesManager, ExplosionManager explosionManager) {
        this.world = world;
        this.map = map;
        this.player = player;
        this.particlesManager = particlesManager;
        this.explosionManager = explosionManager;

        enemyList = new ArrayList<Enemy>();
        particlesGenerated = new ArrayList<Boolean>();

    }

    private void Add(Enemy enemy) {
        enemyList.add(enemy);
        particlesGenerated.add(false);

    }

    public void Update(float delta, EnemyBulletManager enemyBulletManager) {
        for(Enemy enemy: enemyList) {
            enemy.Update(delta, player, enemyBulletManager);
        }
        if(Info.wave % 5 != 0) {
            SpawnEnemies(delta);
        } else {
            SpawnEnemies(delta);
//            SpawnBoss(delta);
        }

        SweepDeadBodies();
    }

    public void Draw(SpriteBatch batch) {
        for(Enemy enemy: enemyList) {
            enemy.Draw(batch);
        }
    }

    private void SpawnEnemies(float delta) {
        delay += delta * 1000;
        if(enemiesKilled < enemiesPerWave) {
            if(delay >= delayMax && enemiesNr <= enemiesMaxNr && enemiesSpawned < enemiesPerWave) {
                float id2 = (float) Math.random() * Info.wave;
                int id;
                if(id2 < 1.5) {
                    id = 0;
                } else if(id2 < 2.2) {
                    id = 1;
                } else {
                    id = 2;
                }
                Add(new Enemy(world, id, false, map, explosionManager));
//                Add(new Enemy(world, 2, false, map, explosionManager));
                enemiesNr ++;
                enemiesSpawned ++;
                delay = 0;
            }
        } else {
            wave ++;
            Info.wave = wave;
            enemiesKilled = 0;
            enemiesSpawned = 0;
            enemiesMaxNr += 1;
            enemiesPerWave += 5;
        }
    }
    private void SpawnBoss(float delta) {
        delay += delta * 1000;
        if(!summoned) {
            Add(new Enemy(world, Info.wave / 5 - 1, true, map, explosionManager));
            enemiesNr ++;
        }
    }
    
    private void SweepDeadBodies() {
        for(int i = 0; i < enemiesNr; i ++) {
            if(enemyList.get(i).GetHP() <= 0 && !particlesGenerated.get(i)) {
                particlesManager.New(1, enemyList.get(i).getX() + enemyList.get(i).getWidth() / 2, enemyList.get(i).getY() + enemyList.get(i).getHeight() / 2);
                particlesGenerated.set(i, true);
            }
            if(enemyList.get(i).GetDead()) {
                Info.score += enemyList.get(i).GetPointsValue();
                enemyList.get(i).Destroy();
                enemyList.get(i).GetBody().setUserData(null);
                enemiesKilled ++;
                Info.enemiesKilled ++;

                enemiesNr --;
                enemyList.remove(i);
                particlesGenerated.remove(i);

            }
        }
    }

    public void Dispose() {

    }


}
