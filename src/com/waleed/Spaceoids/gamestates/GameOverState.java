package com.waleed.Spaceoids.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.waleed.Spaceoids.main.Spaceoids;
import com.waleed.Spaceoids.managers.GameKeys;
import com.waleed.Spaceoids.managers.GameStateManager;
import com.waleed.Spaceoids.managers.Save;

public class GameOverState extends GameState {
	
	private SpriteBatch sb;
	private ShapeRenderer sr;
	
	private boolean newHighScore;
	private char[] newName;
	private int currentChar;
	
	private BitmapFont gameOverFont;
	private BitmapFont font;
	
	public GameOverState(GameStateManager gsm) {
		super(gsm);
	}
	
	public void init() {
		
		sb = new SpriteBatch();
		sr = new ShapeRenderer();
		
		newHighScore = Save.gd.isHighScore(Save.gd.getTentativeScore());
		if(newHighScore) {
			newName = new char[] {'A', 'A', 'A', 'A', 'A', 'A'};
			currentChar = 0;
		}
		
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(
			Gdx.files.internal("fonts/Hyperspace Bold.ttf")
		);
		gameOverFont = gen.generateFont(32);
		font = gen.generateFont(20);
		
	}
	
	public void update(float dt) {
		handleInput();
	}
	
	public void draw() {
		
		sb.setProjectionMatrix(Spaceoids.cam.combined);
		
		sb.begin();
		
		String s;
		float w;
		
		s = "Game Over";
		w = gameOverFont.getBounds(s).width;
		gameOverFont.draw(sb, s, (Spaceoids.WIDTH - w) / 2, 220);
		
		if(!newHighScore) {
			sb.end();
			return;
		}
		
		s = "New High Score: " + Save.gd.getTentativeScore();
		w = font.getBounds(s).width;
		font.draw(sb, s, (Spaceoids.WIDTH - w) / 2, 180);
		
		for(int i = 0; i < newName.length; i++) {
			font.draw(
				sb,
				Character.toString(newName[i]),
				230 + 14 * i,
				120
			);
		}
		
		sb.end();
		
		sr.begin(ShapeType.Line);
		sr.line(
			230 + 14 * currentChar,
			100,
			244 + 14 * currentChar,
			100
		);
		sr.end();
		
	}
	
	public void handleInput() {
		
		if(GameKeys.isPressed(GameKeys.ENTER)) {
			if(newHighScore) {
				Save.gd.addHighScore(
					Save.gd.getTentativeScore(),
					new String(newName)
				);
				Save.save();
			}
			gsm.setState(GameStateManager.MENU);
		}
		
		if(GameKeys.isPressed(GameKeys.UP)) {
			if(newName[currentChar] == ' ') {
				newName[currentChar] = 'Z';
			}
			else {
				newName[currentChar]--;
				if(newName[currentChar] < 'A') {
					newName[currentChar] = ' ';
				}
			}
		}
		
		if(GameKeys.isPressed(GameKeys.DOWN)) {
			if(newName[currentChar] == ' ') {
				newName[currentChar] = 'A';
			}
			else {
				newName[currentChar]++;
				if(newName[currentChar] > 'Z') {
					newName[currentChar] = ' ';
				}
			}
		}
		
		if(GameKeys.isPressed(GameKeys.RIGHT)) {
			if(currentChar < newName.length - 1) {
				currentChar++;
			}
		}
		
		if(GameKeys.isPressed(GameKeys.LEFT)) {
			if(currentChar > 0) {
				currentChar--;
			}
		}
		
	}
	
	public void dispose() {
		sb.dispose();
		sr.dispose();
		gameOverFont.dispose();
		font.dispose();
	}
	
}









