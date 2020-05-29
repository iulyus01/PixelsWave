package com.pixelswave.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.pixelswave.game.Info;
import com.pixelswave.game.Main;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iulyus on 21.06.2017.
 */
public class MainMenuScreen implements Screen {

    private MainMenuScreen mainMenuScreen;

    private Stage stage;
    private ShapeRenderer shapes;

    private BitmapFont font;
    private BitmapFont titleFont;

    private GlyphLayout layout;
    private Label highscoreLabel;

    private List<Pixel> pixelList;

    private Texture texture;
    private Texture texture2;
    private Texture highscoreIcon;

    private Sound buttonSound;

    private float W = Gdx.graphics.getWidth();
    private float H = Gdx.graphics.getHeight();

    private int pixelsNr = 1;

    public MainMenuScreen(final Main game) {
        mainMenuScreen = this;

        stage = new Stage();
        shapes = new ShapeRenderer();
        Table table = new Table();

        Gdx.input.setInputProcessor(stage);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Orbitron Light.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = (int) H / 8;
        titleFont = generator.generateFont(parameter);
        layout = new GlyphLayout(titleFont, "Pixels Wave");

        parameter.size = (int) H / 16;
        font = generator.generateFont(parameter);
        generator.dispose();
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.downFontColor = Color.valueOf("dd2c2c");

        TextButton playButton = new TextButton("p l a y", buttonStyle);
        TextButton exitButton = new TextButton("e x i t", buttonStyle);

        Preferences prefs = Gdx.app.getPreferences("MyPreferences");
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        highscoreLabel = new Label(" " + prefs.getInteger("highscore"), labelStyle);
        highscoreIcon = new Texture(Gdx.files.internal("HighscoreIcon.png"));
        Image image = new Image(highscoreIcon);

        table.setBounds(0, 0, W, H / 4 * 3);
        table.add(playButton).padBottom(H / 20);
        table.row();
        table.add(exitButton).padBottom(H / 4);
        table.row();
        table.add(image).padLeft(-W / 10);
        table.add(highscoreLabel).padLeft(-W / 20);

        stage.addActor(table);

        buttonSound = Gdx.audio.newSound(Gdx.files.internal("ButtonClick.mp3"));

        final BitmapFont finalFont = font;
        playButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                buttonSound.play();
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                mainMenuScreen.dispose();
                game.setScreen(new PlayScreen(game, finalFont));
            }
        });
        exitButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                buttonSound.play();
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                mainMenuScreen.dispose();
                Gdx.app.exit();
            }
        });

        pixelList = new ArrayList<Pixel>();
        pixelList.add(new Pixel((float) Math.random() * Gdx.graphics.getWidth(), (float) Math.random() * Gdx.graphics.getHeight()));

        texture = new Texture(Gdx.files.internal("MenuBackground.png"));
        texture2 = new Texture(Gdx.files.internal("TransparentBlack.png"));

        Info.score = 0;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        PixelsUpdate(delta);

        stage.getBatch().begin();
        stage.getBatch().draw(texture, 0, 0, 0, 0,
                texture.getWidth(), texture.getHeight(),
                (float) Gdx.graphics.getWidth() / texture.getWidth(), (float) Gdx.graphics.getWidth() / texture.getWidth(),
                0, 0, 0, texture.getWidth(), texture.getHeight(), false, false);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        for(Pixel pixel: pixelList) {
            pixel.Draw(stage.getBatch());
        }
        shapes.end();
        stage.getBatch().draw(texture2, 0, 0, 0, 0,
                texture.getWidth(), texture.getHeight(),
                (float) Gdx.graphics.getWidth() / texture.getWidth(), (float) Gdx.graphics.getWidth() / texture.getWidth(),
                0, 0, 0, texture.getWidth(), texture.getHeight(), false, false);
        titleFont.draw(stage.getBatch(), layout, W / 2 - layout.width / 2, H / 8 * 7);
        stage.getBatch().end();

        stage.draw();

    }

    private void PixelsUpdate(float delta) {
        for(int i = 0; i < pixelsNr; i ++) {
            if(pixelList.get(i).GetSteps() == 0 && Math.random() < 0.07 && pixelList.get(i).GetHorizontal() && pixelsNr < 10) {
                pixelList.add(new Pixel(pixelList.get(i).GetX(), pixelList.get(i).GetY()));
                pixelsNr ++;
            }
            pixelList.get(i).Update(delta);
        }

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
        stage.dispose();
        titleFont.dispose();
        texture.dispose();
        texture2.dispose();
        highscoreIcon.dispose();
        buttonSound.dispose();
    }
}
