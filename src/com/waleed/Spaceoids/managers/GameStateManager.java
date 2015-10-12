package com.waleed.Spaceoids.managers;

import com.waleed.Spaceoids.gamestates.ChooseModeState;
import com.waleed.Spaceoids.gamestates.GameOverState;
import com.waleed.Spaceoids.gamestates.GameState;
import com.waleed.Spaceoids.gamestates.HighScoreState;
import com.waleed.Spaceoids.gamestates.MenuState;
import com.waleed.Spaceoids.gamestates.MultiplayerState;
import com.waleed.Spaceoids.gamestates.PlayState;
import com.waleed.Spaceoids.gamestates.UpdateState;

public class GameStateManager {
	
	// current game state
	public GameState gameState;
	public PlayState playState;
	
	public static final int MENU = 0;
	public static final int PLAY = 893746;
	public static final int HIGHSCORE = 3847;
	public static final int GAMEOVER = 928478;
	public static final int MULTIPLAYER = 2269;
	public static final int UPDATE = 1010;

	
	public static final int CHOOSEMODE = 2000;
	
//	public static String ip = "192.168.0.13";
	public static String ip = "104.174.253.235";
	public static int port = 911;
	
	private int state = 0;
	
	public GameStateManager() {
		setState(MENU);
	}
	
	public void setState(int state) {
		if(gameState != null) gameState.dispose();
		if(state == MENU) {
			gameState = new MenuState(this);
		}
		if(state == PLAY) {
			gameState = new PlayState(this);
			playState = new PlayState(this);
			playState.dispose();
		}
		if(state == HIGHSCORE) {
			gameState = new HighScoreState(this);
		}
		if(state == GAMEOVER) {
			gameState = new GameOverState(this);
		}
		if(state == MULTIPLAYER) {
			gameState = new MultiplayerState(this);
		}
		
		if(state == CHOOSEMODE) {
			gameState = new ChooseModeState(this);
		}
		
		if(state == UPDATE) {
			gameState = new UpdateState(this);
		}
		this.state = state;
	}
	
	public void update(float dt) {
		gameState.update(dt);
	}
	
	public void draw() {
		gameState.draw();
	}

	public boolean isSinglePlayer() {
		return this.state == PLAY;
	}
	
	
	
}











