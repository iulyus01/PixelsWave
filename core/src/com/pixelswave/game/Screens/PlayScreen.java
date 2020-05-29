package com.pixelswave.game.Screens;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.pixelswave.game.*;
import com.pixelswave.game.Bonus.BonusManager;
import com.pixelswave.game.Bullets.BulletManager;
import com.pixelswave.game.Bullets.EnemyBulletManager;
import com.pixelswave.game.Enemies.EnemyManager;
import com.pixelswave.game.Explosions.ExplosionManager;
import com.pixelswave.game.Particles.ParticlesManager;

public class PlayScreen implements Screen {

    private Main game;

    private SpriteBatch batch;
    private SpriteBatch batchHud;
    private ShapeRenderer shapeRenderer;

    private OrthographicCamera camera;

    private Box2DDebugRenderer box2dDebug;

    private World world;

    private TiledMap map;
    private TiledMapRenderer mapRenderer;
    private MapProperties mapProperties;

    private Player player;
    private EnemyManager enemyManager;
    private BulletManager bulletManager;
    private EnemyBulletManager enemyBulletManager;
    private ExplosionManager explosionManager;
    private BonusManager bonusManager;

    private Walls walls;
    private Hud hud;

    private RayHandler shadows;

    private ParticlesManager particlesManager;

    private Music backgroundMusic;

    private int W = Gdx.graphics.getWidth();
    private int H = Gdx.graphics.getHeight();

    private float levelWidth;
    private float levelHeight;
    private float shakeStrength = 0;
    private float shakeMaxStrength = Info.tileSize / 4;

    private boolean changeShake = false;

    public PlayScreen(Main game, BitmapFont font) {
        this.game = game;

        Info.wave = 1;
        Info.enemiesKilled = 0;

        batch = new SpriteBatch();
        batchHud = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, W / Info.PPM, H / Info.PPM);

        box2dDebug = new Box2DDebugRenderer();

        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new Contacts());

        map = new TmxMapLoader().load("Map.tmx");

        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f / 32f * Info.tileSize);
        mapProperties = map.getProperties();
        shadows = new RayHandler(world);

        particlesManager = new ParticlesManager();

        player = new Player(world, shadows);

        walls = new Walls(world, map);

        hud = new Hud(game, this, player, font, batch);

        bulletManager = new BulletManager(world, player, shadows);
        enemyBulletManager = new EnemyBulletManager(world, shadows);
        explosionManager = new ExplosionManager(world);
        enemyManager = new EnemyManager(world, map, player, particlesManager, explosionManager);
        bonusManager = new BonusManager(world, map);

        levelWidth = mapProperties.get("width", Integer.class) * Info.tileSize;
        levelHeight = mapProperties.get("height", Integer.class) * Info.tileSize;

        System.out.println("test before music");
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Steamtech Mayhem-Looping.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.play();

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
//        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        walls.Update();
        if(!player.GetLife()) player.Update(delta);
        bonusManager.Update(delta);
        enemyManager.Update(delta, enemyBulletManager);
        bulletManager.Update();
        enemyBulletManager.Update();
        explosionManager.Update();

        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        camera.position.x = Math.min(Math.max(player.getX(), W / Info.PPM / 2), levelWidth - W / Info.PPM / 2);
        camera.position.y = Math.min(Math.max(player.getY(), H / Info.PPM / 2), levelHeight - H / Info.PPM / 2);
        if(player.Shaking()) ShakeScreen();
        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();
        shadows.setCombinedMatrix(camera.combined, player.getX(), player.getY(), W, H);
        shadows.update();
        particlesManager.Update(delta);


        // <==========================================================================================>
        // <====================================== D R A W I N G =====================================>
        // <==========================================================================================>


        batch.begin();
        bonusManager.Draw(batch);
        particlesManager.Draw(batch);
        if(!player.GetLife()) player.Draw(batch);
        enemyManager.Draw(batch);
        bulletManager.Draw(batch);
        enemyBulletManager.Draw(batch);
        explosionManager.Draw(batch);
        batch.end();

        shadows.render();
        hud.UpdateAndDraw(batchHud, shapeRenderer, camera, delta);

//        box2dDebug.render(world, camera.combined);
        world.step(delta, 8, 3);
    }

    private void ShakeScreen() {
        camera.position.x += (Math.random() * shakeStrength - shakeMaxStrength / 2);
        camera.position.y += (Math.random() * shakeStrength - shakeMaxStrength / 2);
        if(!changeShake) {
            if(shakeStrength < shakeMaxStrength) shakeStrength += shakeMaxStrength / 8f;
            else changeShake = true;
        } else {
            if(shakeStrength > 0) shakeStrength -= shakeMaxStrength / 10f;
            else changeShake = false;
            player.SetShaking(false);
        }
    }

    public void StopMusic() {
        backgroundMusic.stop();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        batchHud.dispose();
        shapeRenderer.dispose();
        walls.Dispose();
        box2dDebug.dispose();
        world.dispose();
        map.dispose();
        player.Dispose();
        enemyManager.Dispose();
        bulletManager.Dispose();
        enemyBulletManager.Dispose();
        hud.Dispose();
        particlesManager.Dispose();
        backgroundMusic.dispose();
    }

}
