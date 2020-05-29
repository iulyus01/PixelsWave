package com.pixelswave.game;

import com.badlogic.gdx.Game;
import com.pixelswave.game.Screens.MainMenuScreen;

public class Main extends Game {

	@Override
	public void create () {

		setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render () {

		super.render();
	}

	@Override
	public void dispose () {
	}
}
