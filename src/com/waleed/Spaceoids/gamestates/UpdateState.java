package com.waleed.Spaceoids.gamestates;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
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
import com.waleed.Spaceoids.managers.Downloader;
import com.waleed.Spaceoids.managers.GameKeys;
import com.waleed.Spaceoids.managers.GameStateManager;
import com.waleed.Spaceoids.managers.Jukebox;

public class UpdateState extends GameState{
	
	private SpriteBatch sb;
	private ShapeRenderer sr;
	
	private BitmapFont titleFont;
	private BitmapFont font;
	private BitmapFont firstNameFont;
	private BitmapFont lastNameFont;
	
	private final String title = "Would you like to update?";
	
	
	private final String credit = "By", firstName = "Waleed", lastName = "Ghazal";
	private int currentItem;
	private String[] menuItems;
	
	private ArrayList<Asteroid> asteroids;
	
	private Downloader downloader;
	private Thread downloaderThread;

	
	private boolean error;

	
	public UpdateState(GameStateManager gsm) {
		super(gsm);
	}
	
	@Override
	public void init()
	{
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
			"Yes",
			"No",
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

	}

	@Override
	public void update(float dt) {
		
		handleInput();
		
		for(int i = 0; i < asteroids.size(); i++)
		{
			asteroids.get(i).update(dt);
		}
		
	}

	@Override
	public void draw() {
		
		sb.setProjectionMatrix(Spaceoids.cam.combined);
		sr.setProjectionMatrix(Spaceoids.cam.combined);

		for(int i = 0; i < asteroids.size(); i++)
		{
			asteroids.get(i).draw(sr);
		}
		
		sb.begin();
		float width = titleFont.getBounds(title).width;
		titleFont.draw(
				sb,
				title,
				(Spaceoids.WIDTH - width) / 2,
				300
			);
		
		for(int i = 0; i < menuItems.length; i++)
		{
			width = font.getBounds(menuItems[i]).width;
			if(currentItem == i) 
				font.setColor(Color.RED);
			else 
				font.setColor(Color.WHITE);
			
			font.draw(sb, 
					menuItems[i], 
					((Spaceoids.WIDTH) / 2) - width, 
					180 - 35 * i);
		}
		
		sb.end();
		
	}

	@Override
	public void handleInput() {
		if(GameKeys.isPressed(GameKeys.UP))
		{
			Jukebox.play("choose");
			if(currentItem > 0)
			{
				currentItem--;
			}else
			{
				currentItem = menuItems.length - 1;
			}
		}
		
		if(GameKeys.isPressed(GameKeys.DOWN))
		{
			Jukebox.play("choose");
			if(currentItem < menuItems.length - 1)
			{
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
	
	private void select()
	{
		if(currentItem == 0)
		{
			try {
				beginDownload(false);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(currentItem == 1)
		{
			gsm.setState(GameStateManager.MENU);
		}else if(currentItem == 2)
		{
			Gdx.app.exit();
		}
	}
	
	private static boolean isDownloadAvaliable() {                                                                                                                                                                                                 
		try {                                                                                                                                                                                                                                 
			URL url = new URL(Spaceoids.INSTANCE.downloadURL);                                                                                                                                                                                 
			URLConnection conn = url.openConnection();                                                                                                                                                                                  
			conn.connect();
			System.out.println("Download Server is stable");
			return true;                                                                                                                                                                                                                      
		} catch (MalformedURLException e) {  
			String error = "Download Server is unstable" + e.toString();
			System.err.println(error);
			return false;
		} catch (IOException e) {   
			String error = "Download Server is unstable: " + e.toString();
			System.err.println(error);
			return false;                            
		}                                                  

	}    
	



	public void beginDownload(boolean isCancelled) throws InterruptedException
	{
		if(isDownloadAvaliable())
		{
			downloader = new Downloader();
			boolean error = downloader.isError();
			this.error = error;
			if(isCancelled)
			{

				downloaderThread = new Thread(downloader);
				downloaderThread.start();

			}else
			{         
				downloaderThread = new Thread(downloader);
				downloaderThread.start();

			}
		}
	}

	
	@Override
	public void dispose() {
		
	}
	
	

}
