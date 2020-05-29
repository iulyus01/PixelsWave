package com.pixelswave.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixelswave.game.Screens.MainMenuScreen;
import com.pixelswave.game.Screens.PlayScreen;

public class Hud {

    private Stage stage;
    private PlayScreen playScreen;
    private Main game;

    private Player player;
    private Texture controllerTexture;

    private BitmapFont font;
    private Label label1;
    private Label waveTextLabel;
    private Label.LabelStyle waveTextLabelStyle;
    private Label backToMenuLabel;
    private TextButton gameOverButton;
    private TextButton backToMenuButton;

    private Sound buttonSound;

    private float waveTestScaleAcceleration;
    private float hpBarX;
    private float hpBarY;
    private float hpBarWidth;
    private float hpBarWidthFull;
    private float hpBarHeight;

    private int W = Gdx.graphics.getWidth();
    private int H = Gdx.graphics.getHeight();
    private int wave = 0;
    private int waveTextDisplayTimer = 0;

    private boolean updateWaveText = true;

    public Hud(final Main game, final PlayScreen playScreen, Player player, final BitmapFont font, SpriteBatch batch) {
        super();
        this.game = game;
        this.playScreen = playScreen;
        this.player = player;
        this.font = font;
        Viewport viewport = new FitViewport(W, H);
        stage = new Stage(viewport, batch);

        hpBarWidth = player.GetHp() * Info.tileSize / 20;
        hpBarHeight = Info.tileSize / 4;
        hpBarWidthFull = player.GetFullHp() * Info.tileSize / 20;
        hpBarX = W - Info.tileSize - player.GetHp() * (Info.tileSize / 20);
        hpBarY = H - Info.tileSize - Info.tileSize / 2;

        controllerTexture = new Texture(Gdx.files.internal("Controller.png"));

        Label.LabelStyle labelStyle1 = new Label.LabelStyle(font, Color.WHITE);
        labelStyle1.font = font;
        label1 = new Label("", labelStyle1);
        label1.setAlignment(Align.topLeft);
        stage.addActor(label1);

        waveTextLabelStyle = new Label.LabelStyle(font, Color.WHITE);
        waveTextLabelStyle.font = font;
        waveTextLabel = new Label("", waveTextLabelStyle);
        waveTextLabel.setAlignment(Align.center);
        waveTextLabel.setPosition(W / 2, H / 2, Align.center);
        waveTestScaleAcceleration = H / font.getLineHeight() / 20;
//        waveTextLabel.setScale(waveTextScale);


        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.downFontColor = Color.valueOf("dd2c2c");

        buttonSound = Gdx.audio.newSound(Gdx.files.internal("ButtonClick.mp3"));

        gameOverButton = new TextButton("GAME OVER", buttonStyle);
        gameOverButton.setPosition(W / 2f, H / 4f * 3, Align.center);
        gameOverButton.setColor(1, 1, 1, 0);
        gameOverButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                buttonSound.play();
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                Info.wave = 1;
                Info.enemiesKilled = 0;
                Info.score = 0;
                playScreen.dispose();
                game.setScreen(new PlayScreen(game, font));
            }
        });
        stage.addActor(gameOverButton);

        backToMenuButton = new TextButton("back to menu", buttonStyle);
        backToMenuButton.setPosition(W / 2f, H / 4f, Align.center);
        backToMenuButton.setColor(1, 1, 1, 0);
        backToMenuButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                buttonSound.play();
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                playScreen.StopMusic();
                playScreen.dispose();
                game.setScreen(new MainMenuScreen(game));
            }
        });
        stage.addActor(backToMenuButton);

    }

    public void UpdateAndDraw(SpriteBatch batch, ShapeRenderer shapeRenderer, OrthographicCamera camera, float delta) {
        label1.setFontScale(.8f);
        label1.setText("Kills: " + Info.enemiesKilled + "\n" +
                "Wave: " + Info.wave + "\n" +
                "Score: " + Info.score);
        label1.setPosition(Info.tileSize, H - 2 * Info.tileSize);
        if(updateWaveText || Info.wave == wave + 1) UpdateWaveText(delta);
        hpBarX = camera.position.x + W / 2f / Info.PPM - hpBarWidthFull - Info.tileSize / 2;
        hpBarY = camera.position.y + H / 2f / Info.PPM - 2 * hpBarHeight;
        hpBarWidth = player.GetHp() * Info.tileSize / 20;
        if(player.GetHp() <= 0) {
            if(stage.getActors().size != 2) {
                Preferences prefs = Gdx.app.getPreferences("MyPreferences");
                Gdx.input.setInputProcessor(stage);
                stage.clear();
                gameOverButton.setText("GAME OVER\n\n" + Info.score);
                stage.addActor(gameOverButton);
                stage.addActor(backToMenuButton);
                if(Info.score > prefs.getInteger("highscore")) prefs.putInteger("highscore", Info.score);
                prefs.flush();
            }
            gameOverButton.setColor(gameOverButton.getColor().r, gameOverButton.getColor().g, gameOverButton.getColor().b, 1);
            backToMenuButton.setColor(backToMenuButton.getColor().r, backToMenuButton.getColor().g, backToMenuButton.getColor().b, backToMenuButton.getColor().a + 0.02f);

            if(batch.getColor().a > 0.05) batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, batch.getColor().a - 0.05f);
            else batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, 0);

        }
        stage.act();

        stage.draw();
        if(batch.getColor().a > 0) {
            batch.begin();
            batch.draw(controllerTexture, Info.CONTROLLER - Info.CONTROLLER_RADIUS, Info.CONTROLLER - Info.CONTROLLER_RADIUS, 0, 0, controllerTexture.getWidth(), controllerTexture.getHeight(), (float) (Info.CONTROLLER_RADIUS * 2) / controllerTexture.getWidth(), (float) (Info.CONTROLLER_RADIUS * 2) / controllerTexture.getHeight(), 0, 0, 0, controllerTexture.getWidth(), controllerTexture.getHeight(), false, false);
            batch.draw(controllerTexture, Gdx.graphics.getWidth() - Info.CONTROLLER - Info.CONTROLLER_RADIUS, Info.CONTROLLER - Info.CONTROLLER_RADIUS, 0, 0, controllerTexture.getWidth(), controllerTexture.getHeight(), (float) (Info.CONTROLLER_RADIUS * 2) / controllerTexture.getWidth(), (float) (Info.CONTROLLER_RADIUS * 2) / controllerTexture.getHeight(), 0, 0, 0, controllerTexture.getWidth(), controllerTexture.getHeight(), false, false);
            IsTouched(batch);

            batch.end();
        }

        if(!updateWaveText) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            PlayerHp(shapeRenderer);
            shapeRenderer.end();
        }




    }

    private void IsTouched(SpriteBatch batch) {

        float touchX;
        float touchY;

        boolean touchDst;
        if(Gdx.input.isTouched()) {
            boolean refByShooter = false;
            int x = 0;
            int y = 0;
            for(int i = 0; i < 2; i ++) {
                if(i == 0 && !Gdx.input.isTouched(0)) continue;
                if(i == 1 && !Gdx.input.isTouched(1)) break;
                x = Gdx.input.getX(i);
                y = Gdx.input.getY(i);

                if (x < W / 2) {
                    player.Touching1(x, H - y);

                    touchDst = Math.sqrt((x - Info.CONTROLLER) * (x - Info.CONTROLLER) + (H - y - Info.CONTROLLER) * (H - y - Info.CONTROLLER)) <= Info.CONTROLLER_RADIUS;
                    if(touchDst) {
                        touchX = x - Info.CONTROLLER;
                        touchY = H - y - Info.CONTROLLER;
                    } else {
                        float touchAngle = (float) Math.atan2(H - y - Info.CONTROLLER, x - Info.CONTROLLER);
                        touchX = (float) (Info.CONTROLLER_RADIUS * Math.cos(touchAngle));
                        touchY = (float) (Info.CONTROLLER_RADIUS * Math.sin(touchAngle));
                    }
                    batch.draw(controllerTexture, Info.CONTROLLER - Info.CONTROLLER_RADIUS / 2 + touchX, Info.CONTROLLER - Info.CONTROLLER_RADIUS / 2 + touchY, 0, 0, controllerTexture.getWidth(), controllerTexture.getHeight(), (float) (Info.CONTROLLER_RADIUS) / controllerTexture.getWidth(), (float) (Info.CONTROLLER_RADIUS) / controllerTexture.getHeight(), 0, 0, 0, controllerTexture.getWidth(), controllerTexture.getHeight(), false, false);
                } else {
                    refByShooter = true;
                    player.Touching2();

                    touchDst = Math.sqrt((x - Info.SHOOTING_CONTROLLER_X) * (x - Info.SHOOTING_CONTROLLER_X) + (H - y - Info.SHOOTING_CONTROLLER_Y) * (H - y - Info.SHOOTING_CONTROLLER_Y)) <= Info.CONTROLLER_RADIUS;
                    if(touchDst) {
                        touchX = x - Info.SHOOTING_CONTROLLER_X;
                        touchY = H - y - Info.SHOOTING_CONTROLLER_Y;
                    } else {
                        float touchAngle = (float) Math.atan2(H - y - Info.SHOOTING_CONTROLLER_Y, x - Info.SHOOTING_CONTROLLER_X);
                        touchX = (float) (Info.CONTROLLER_RADIUS * Math.cos(touchAngle));
                        touchY = (float) (Info.CONTROLLER_RADIUS * Math.sin(touchAngle));
                    }
                    batch.draw(controllerTexture, Info.SHOOTING_CONTROLLER_X - Info.CONTROLLER_RADIUS / 2 + touchX, Info.SHOOTING_CONTROLLER_Y - Info.CONTROLLER_RADIUS / 2 + touchY, 0, 0, controllerTexture.getWidth(), controllerTexture.getHeight(), (float) (Info.CONTROLLER_RADIUS) / controllerTexture.getWidth(), (float) (Info.CONTROLLER_RADIUS) / controllerTexture.getHeight(), 0, 0, 0, controllerTexture.getWidth(), controllerTexture.getHeight(), false, false);
                }
            }

            if(refByShooter){
                if(x < W / 2) {
                    x = Gdx.input.getX(0);
                    y = Gdx.input.getY(0);
                }
                player.SetAngle(x, H - y, true);
            } else {
                player.SetAngle(x, H - y, false);
            }
        }
    }

    private void UpdateWaveText(float delta) {
        if(Info.wave == wave + 1) {
            updateWaveText = true;
            waveTextDisplayTimer = 0;
            stage.clear();
            waveTextLabel.setColor(waveTextLabel.getColor().r, waveTextLabel.getColor().g, waveTextLabel.getColor().b, 0);
            waveTextLabelStyle.font.getData().setScale(H / waveTextLabelStyle.font.getLineHeight());
            waveTextLabel.setText("WAVE " + Info.wave);
            stage.addActor(waveTextLabel);
            wave = Info.wave;
        }
        if(waveTextLabelStyle.font.getScaleY() <= 2.5f && waveTextDisplayTimer < 1500) {
            waveTextDisplayTimer += 1000 * delta;
            if(waveTextDisplayTimer == 0) waveTestScaleAcceleration /= 20f;
            waveTextLabelStyle.font.getData().setScale(2.5f + waveTestScaleAcceleration);
        }
        waveTextLabelStyle.font.getData().setScale(waveTextLabelStyle.font.getScaleY() - waveTestScaleAcceleration);
        waveTextLabel.setColor(
                waveTextLabel.getColor().r,
                waveTextLabel.getColor().g,
                waveTextLabel.getColor().b,
                waveTextLabel.getColor().a + 0.035f);
        waveTextLabel.setStyle(waveTextLabelStyle);
        if(waveTextLabelStyle.font.getScaleY() <= 0) {
            System.out.println("test scaleY < 0");
            stage.clear();
            font.getData().setScale(1);
            stage.addActor(label1);
            updateWaveText = false;
        }
    }

    private void PlayerHp(ShapeRenderer shapes) {
        shapes.setColor(Color.WHITE);
        shapes.set(ShapeRenderer.ShapeType.Line);
        shapes.rect(hpBarX, hpBarY, player.GetFullHp() * Info.tileSize / 20, hpBarHeight);
        shapes.rect(hpBarX - 1 / Info.PPM,
                hpBarY - 1 / Info.PPM,
                player.GetFullHp() * Info.tileSize / 20 + 2 / Info.PPM,
                hpBarHeight + 2 / Info.PPM);
        shapes.set(ShapeRenderer.ShapeType.Filled);
        shapes.rect(hpBarX, hpBarY, hpBarWidth, hpBarHeight);
    }

    public void Dispose() {
        stage.dispose();
        controllerTexture.dispose();
        buttonSound.dispose();
    }
}
