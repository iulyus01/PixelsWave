package com.pixelswave.game;

import com.badlogic.gdx.physics.box2d.*;
import com.pixelswave.game.Bonus.Bonus;
import com.pixelswave.game.Bullets.Bullet;
import com.pixelswave.game.Bullets.EnemyBullet;
import com.pixelswave.game.Enemies.Enemy;
import com.pixelswave.game.Explosions.Shrapnel;

public class Contacts implements ContactListener {

    /*
    bullet - enemy
    bullet - walls
    bullet - shrapnel
    bullet - enemy bullet
    enemy bullet - player
    enemy bullet - enemy
    enemy bullet - shrapnel
    enemy - player
    shrapnel - wall
    shrapnel - enemy
    shrapnel - player
    bonus - player
     */

    @Override
    public void beginContact(Contact contact) {
        Fixture A = contact.getFixtureA();
        Fixture B = contact.getFixtureB();

        if(A == null || B == null) return;
        if(A.getUserData() == null || B.getUserData() == null) return;

        // player bullet
        if(A.getUserData() instanceof Bullet || B.getUserData() instanceof Bullet) {
            Bullet bullet = (A.getUserData() instanceof Bullet) ? (Bullet) A.getUserData() : (Bullet) B.getUserData();
            Object other = (A.getUserData() instanceof Bullet) ? B.getUserData() : A.getUserData();

            if(other instanceof Enemy) {
                ((Enemy) other).Hit(bullet.GetDamage());
                bullet.SetDead();
            } else if(other instanceof Walls) {
                bullet.SetDead();
            } else if(other instanceof Shrapnel) {
                ((Shrapnel) other).SetDead();
                bullet.SetDead();
            } else if(other instanceof EnemyBullet) {
                ((EnemyBullet) other).SetDead();
                bullet.SetDead();
            }

        }

        // enemy bullet
        if(A.getUserData() instanceof EnemyBullet || B.getUserData() instanceof EnemyBullet) {
            EnemyBullet enemyBullet = (A.getUserData() instanceof EnemyBullet) ? (EnemyBullet) A.getUserData() : (EnemyBullet) B.getUserData();
            Object other = (A.getUserData() instanceof EnemyBullet) ? B.getUserData() : A.getUserData();

            if(other instanceof Player) {
                ((Player) other).HitBy(enemyBullet.GetDamage());
                enemyBullet.SetDead();
            } else if(other instanceof Enemy || other instanceof Walls) {
                enemyBullet.SetDead();
            } else if(other instanceof Shrapnel) {
                ((Shrapnel) other).SetDead();
                enemyBullet.SetDead();
            } else if(other instanceof EnemyBullet) {
                ((EnemyBullet) other).SetDead();
                enemyBullet.SetDead();
            }

        }

        // enemy
        if(A.getUserData() instanceof Enemy || B.getUserData() instanceof Enemy) {
            Enemy enemy = (A.getUserData() instanceof Enemy) ? (Enemy) A.getUserData() : (Enemy) B.getUserData();
            Object other = (A.getUserData() instanceof Enemy) ? B.getUserData() : A.getUserData();

            if(other instanceof Player) {
                enemy.TouchPlayer((Player) other);

            }
        }

        // shrapnel
        if(A.getUserData() instanceof Shrapnel || B.getUserData() instanceof Shrapnel) {
            Shrapnel shrapnel = (A.getUserData() instanceof Shrapnel) ? (Shrapnel) A.getUserData() : (Shrapnel) B.getUserData();
            Object other = (A.getUserData() instanceof Shrapnel) ? B.getUserData() : A.getUserData();

            if(other instanceof Walls) {
                shrapnel.SetDead();
            } else if(other instanceof Enemy) {
                ((Enemy) other).Hit(shrapnel.GetDamage());
                shrapnel.SetDead();
            } else if(other instanceof Player) {
                ((Player) other).HitBy(shrapnel.GetDamage());
                shrapnel.SetDead();
            } else if(other instanceof Shrapnel) {
                ((Shrapnel) other).SetDead();
                shrapnel.SetDead();
            }

        }

        // bonus
        if(A.getUserData() instanceof Bonus || B.getUserData() instanceof Bonus) {
            Bonus bonus = (A.getUserData() instanceof Bonus) ? (Bonus) A.getUserData() : (Bonus) B.getUserData();
            Object other = (A.getUserData() instanceof Bonus) ? B.getUserData() : A.getUserData();

            if(other instanceof Player) {
                bonus.PlaySound();
                switch(bonus.GetId()) {
                    case 0:
                        ((Player) other).Heal(bonus.GetBonus());
                        break;
                }
                bonus.SetDead();
            }
        }


    }

    @Override
    public void endContact(Contact contact) {
        Fixture A = contact.getFixtureA();
        Fixture B = contact.getFixtureB();

        if(A == null || B == null) return;
        if(A.getUserData() == null || B.getUserData() == null) return;

        // enemy
        if(A.getUserData() instanceof Enemy || B.getUserData() instanceof Enemy) {
            Enemy enemy = (A.getUserData() instanceof Enemy) ? (Enemy) A.getUserData() : (Enemy) B.getUserData();
            Object other = (A.getUserData() instanceof Enemy) ? B.getUserData() : A.getUserData();

            if(other instanceof Player) {
                enemy.NotTouchingPlayer();

            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
