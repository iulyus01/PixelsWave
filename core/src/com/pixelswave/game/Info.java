package com.pixelswave.game;

import com.badlogic.gdx.Gdx;

public class Info {
    public static final float PPM = 100;
    public static final int CONTROLLER = Gdx.graphics.getHeight() / 4;
    public static final int CONTROLLER_RADIUS = 120;
    public static final int SHOOTING_CONTROLLER_X = Gdx.graphics.getWidth() - Gdx.graphics.getHeight() / 4;
    public static final int SHOOTING_CONTROLLER_Y = Gdx.graphics.getHeight() / 4;
    public static final int TILES_NR_H = 10;
    public static final float tileSize = Gdx.graphics.getHeight() / TILES_NR_H / PPM;
    public static int wave = 1;
    public static int enemiesKilled = 0;
    public static int score = 0;
}
