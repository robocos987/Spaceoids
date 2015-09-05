package com.waleed.Spaceoids.gamestates;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.waleed.Spaceoids.entities.Asteroid;
import com.waleed.Spaceoids.main.Spaceoids;
import com.waleed.Spaceoids.managers.GameKeys;
import com.waleed.Spaceoids.managers.GameStateManager;
import com.waleed.Spaceoids.managers.Jukebox;
import com.waleed.Spaceoids.managers.Save;

public class MenuState extends GameState {
	private final String credit = "By", firstName = "Waleed", lastName = "Ghazal";

	private SpriteBatch sb;
	private ShapeRenderer sr;
	
	private BitmapFont titleFont;
	private BitmapFont font;
	private BitmapFont firstNameFont;
	private BitmapFont lastNameFont;
	
	private final String title = "Spaceoids";
	
	private int currentItem;
	private String[] menuItems;
	
	private ArrayList<Asteroid> asteroids;
	
	
	public MenuState(GameStateManager gsm) {
		super(gsm);
	}
	
	public void init() {
		
		sb = new SpriteBatch();
		sr = new ShapeRenderer();
		
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(
			Gdx.files.internal("fonts/Hyperspace Bold.ttf")
		);
		
		titleFont = gen.generateFont(56);
		titleFont.setColor(Color.WHITE);
		
		font = gen.generateFont(20);
		
		firstNameFont = gen.generateFont(20);
		firstNameFont.setColor(Color.WHITE);
		
		lastNameFont = gen.generateFont(20);
		lastNameFont.setColor(Color.RED);
		
		menuItems = new String[] {
			"Play",
			"Highscores",
			"Quit"
		};
		
		asteroids = new ArrayList<Asteroid>();
		for(int i = 0; i < 8; i++) {
			Random rand = new Random();
			asteroids.add(
				new Asteroid(
					MathUtils.random(Spaceoids.WIDTH),
					MathUtils.random(Spaceoids.HEIGHT),
					rand.nextInt(2) == 1 ? Asteroid.LARGE : Asteroid.SMALL
				)
			);
		}
		
		Save.load();
		
	}
	
	public void update(float dt) {
		
		handleInput();
		
		for(int i = 0 ; i < asteroids.size(); i++) {
			asteroids.get(i).update(dt);
		}
		
	}
	
	public void draw() {
		
		sb.setProjectionMatrix(Spaceoids.cam.combined);
		sr.setProjectionMatrix(Spaceoids.cam.combined);
		
		// draw asteroids
		for(int i = 0; i < asteroids.size(); i++) {
			asteroids.get(i).draw(sr);
		}
		
		sb.begin();
		
		// draw title
		float width = titleFont.getBounds(title).width;
		titleFont.draw(
			sb,
			title,
			(Spaceoids.WIDTH - width) / 2,
			300
		);
		
//		String[] splits = firstName.split("\\s+");
		float creditWidth = firstNameFont.getBounds(credit).width;
		firstNameFont.setColor(Color.RED);
		
		firstNameFont.draw(sb, 
				credit, 
				(Spaceoids.WIDTH - creditWidth) - 770 / 2, 
				180 - 35 * 4);
		
		firstNameFont.setColor(Color.WHITE);
		
		float nameWidth = firstNameFont.getBounds(firstName).width;
		firstNameFont.draw(sb, 
				firstName, 
				(Spaceoids.WIDTH - nameWidth) / 2, 
				180 - 35 * 4);
//		font.setColor(Color.RED);
		float lastWidth = lastNameFont.getBounds(lastName).width;
		lastNameFont.draw(sb, 
				lastName, 
				(Spaceoids.WIDTH - nameWidth + 10) / 2 + lastWidth, 
				180 - 35 * 4);
		
		// draw menu
		for(int i = 0; i < menuItems.length; i++) {
			width = font.getBounds(menuItems[i]).width;
			if(currentItem == i) font.setColor(Color.RED);
			else font.setColor(Color.WHITE);
			font.draw(
				sb,
				menuItems[i],
				(Spaceoids.WIDTH - width) / 2,
				180 - 35 * i
			);
		}
		
		sb.end();
		
	}
	
	public void handleInput() {
		if(GameKeys.isPressed(GameKeys.UP)) {
			Jukebox.play("choose");
			if(currentItem > 0) {
				currentItem--;
			}else
			{
				currentItem = menuItems.length - 1;
			}
		}
		if(GameKeys.isPressed(GameKeys.DOWN)) {
			Jukebox.play("choose");
			if(currentItem < menuItems.length - 1) {
				currentItem++;
			}else
			{
				currentItem = 0;
			}
		}
		if(GameKeys.isPressed(GameKeys.ENTER)) {
			Jukebox.play("select");
			select();
		}
		
	}
	
	private void select() {
		// play
		if(currentItem == 0) {
			gsm.setState(GameStateManager.CHOOSEMODE);
		}
		// high scores
		else if(currentItem == 1) {
			gsm.setState(GameStateManager.HIGHSCORE);
		}
		else if(currentItem == 2) {
			Gdx.app.exit();
		}
	}
	
	public void dispose() {
		sb.dispose();
		sr.dispose();
		titleFont.dispose();
		font.dispose();
	}

}










