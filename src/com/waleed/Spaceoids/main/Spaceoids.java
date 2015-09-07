package com.waleed.Spaceoids.main;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.waleed.Spaceoids.gamestates.MultiplayerState;
import com.waleed.Spaceoids.gamestates.PlayState;
import com.waleed.Spaceoids.managers.GameInputProcessor;
import com.waleed.Spaceoids.managers.GameKeys;
import com.waleed.Spaceoids.managers.GameStateManager;
import com.waleed.Spaceoids.managers.Jukebox;
import com.waleed.Spaceoids.network.Network;

public class Spaceoids implements ApplicationListener {

	public static int WIDTH;
	public static int HEIGHT;

	public static OrthographicCamera cam;
	public static boolean debug = true;

	public static Spaceoids INSTANCE = new Spaceoids();


	//	will be further impelemented
	public static double version = 0.1;


	private GameStateManager gsm;
	
	public static int getWidth()
	{
		return WIDTH;
	}
	public static int getHeight()
	{
		return HEIGHT;
	}

	public void create() {

		WIDTH = Gdx.graphics.getWidth() / 2;
		HEIGHT = Gdx.graphics.getHeight() / 2;
		
		cam = new OrthographicCamera(WIDTH, HEIGHT);
//		cam.setToOrtho(false, WIDTH, HEIGHT);
		cam.translate(WIDTH / 2, HEIGHT / 2);
	

		cam.update();

		Gdx.input.setInputProcessor(
				new GameInputProcessor()
				);

		Gdx.graphics.setVSync(true);


		cam.zoom = 0.005F;

		Jukebox.load("sounds/explode.ogg", "explode");
		Jukebox.load("sounds/extralife.ogg", "extralife");
		Jukebox.load("sounds/largesaucer.ogg", "largesaucer");
		Jukebox.load("sounds/pulsehigh.ogg", "pulsehigh");
		Jukebox.load("sounds/pulselow.ogg", "pulselow");
		Jukebox.load("sounds/saucershoot.ogg", "saucershoot");
		Jukebox.load("sounds/shoot.ogg", "shoot");
		Jukebox.load("sounds/smallsaucer.ogg", "smallsaucer");
		Jukebox.load("sounds/thruster.ogg", "thruster");
		Jukebox.load("sounds/select.wav", "select");
		Jukebox.load("sounds/true.wav", "choose");

		gsm = new GameStateManager();

	}

	@SuppressWarnings("null")
	public void render() {

		PlayState state = gsm.playState;
		// clear screen to black
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		

		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.draw();

		GameKeys.update();

	}

	public void resize(int width, int height) {}
	public void pause() {}
	public void resume() {}
	public void dispose() {}
	
	public static Network getClient() {
		// TODO Auto-generated method stub
		return MultiplayerState.INSTANCE != null && MultiplayerState.INSTANCE.client.network != null ? MultiplayerState.INSTANCE.client.network : null;
	}

}
