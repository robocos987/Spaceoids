package com.waleed.Spaceoids.gamestates;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;








import org.lwjgl.input.Keyboard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.jcraft.jogg.Packet;
import com.waleed.Spaceoids.entities.Asteroid;
import com.waleed.Spaceoids.entities.Bullet;
import com.waleed.Spaceoids.entities.Particle;
import com.waleed.Spaceoids.entities.Player;
import com.waleed.Spaceoids.entities.PlayerMP;
import com.waleed.Spaceoids.main.Spaceoids;
import com.waleed.Spaceoids.managers.GameKeys;
import com.waleed.Spaceoids.managers.GameStateManager;
import com.waleed.Spaceoids.managers.Jukebox;
import com.waleed.Spaceoids.network.SpaceoidsClient;
import com.waleed.Spaceoids.network.packets.MPPlayer;

public class MultiplayerState extends GameState {

	private GameStateManager gsm;

	public SpriteBatch sb;
	private ShapeRenderer sr;

	public SpaceoidsClient client;

	private BitmapFont titleFont;
	private BitmapFont font;
	private BitmapFont idFont;
	private BitmapFont firstNameFont;
	private BitmapFont lastNameFont;

	public static int state = -1;
	private Player hudPlayer;

	private ArrayList<Bullet> yourBullets;
	private ArrayList<Particle> particles;
	public  ArrayList<Asteroid> asteroids;


	public static MultiplayerState INSTANCE;

	private Player you;

	private String ip;
	int port;

	public int totalPlayers;
	public boolean inGame;
	
	public boolean kicked;

	public MultiplayerState(GameStateManager gsm) {
		super(gsm);
		this.gsm = gsm;
		INSTANCE = this;
		hudPlayer = new Player(null);
		kicked = false;
	}		

	@Override
	public void init() {
		sb = new SpriteBatch();
		sr = new ShapeRenderer();
		yourBullets = new ArrayList<Bullet>();
		particles = new ArrayList<Particle>();
		asteroids = new ArrayList<Asteroid>();
		you = new Player(yourBullets);
		client = new SpaceoidsClient(this.ip, this.port, you);

		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(
				Gdx.files.internal("fonts/Hyperspace Bold.ttf")
				);

		titleFont = gen.generateFont(40);
		titleFont.setColor(Color.GREEN);

		font = gen.generateFont(20);

		idFont = gen.generateFont(10);
		idFont.setColor(Color.WHITE);

		firstNameFont = gen.generateFont(40);
		firstNameFont.setColor(Color.WHITE);

		lastNameFont = gen.generateFont(40);
		lastNameFont.setColor(Color.RED);

		ip = GameStateManager.ip;
		port = GameStateManager.port;



	}

	private void createParticles(float x, float y) {
		for(int i = 0; i < 30; i++) {
			particles.add(new Particle(x, y));
		}
	}

	@Override
	public void update(float dt) {

		handleInput();

		if(state == 3)
		{
			you.updateMP(dt);

			for(MPPlayer mpPlayer: client.players.values())
			{
				PlayerMP player = mpPlayer.getPlayer();
				player.update(dt);

				// update player bullets
				for(int i = 0; i < player.bullets.size(); i++) {
					player.bullets.get(i).update(dt);
					if(player.bullets.get(i).shouldRemove()) {
						player.bullets.remove(i);
						i--;
					}
				}
			}

			for(int i = 0; i < particles.size(); i++)
			{
				particles.get(i).update(dt);
			}

			// update my bullets
			for(int i = 0; i < you.bullets.size(); i++) {
				you.bullets.get(i).update(dt);
				if(you.bullets.get(i).shouldRemove()) {
					you.bullets.remove(i);
					i--;
				}
			}

			for(int i = 0; i < asteroids.size(); i++)
			{
				asteroids.get(i).update(dt);
			}

			if(you.isDead())
			{
				if(you.getLives() > 0)
				{
					you.reset();
					you.loseLife();
				}
			}

			checkCollisions();
		}
	}





	public String getIP()
	{		
		String ipAddr = "";

		try {

			InetAddress inetAddr = InetAddress.getLocalHost();

			byte[] addr = inetAddr.getAddress();

			// Convert to dot representation
			for (int i = 0; i < addr.length; i++) {
				if (i > 0) {
					ipAddr += ".";
				}
				ipAddr += addr[i] & 0xFF;
			}

			String hostname = inetAddr.getHostName();

			System.out.println("IP Address: " + ipAddr);
			System.out.println("Hostname: " + hostname);

		}
		catch (UnknownHostException e) {
			System.out.println("Host not found: " + e.getMessage());
		}
		return ipAddr;
	}



	@Override
	public void draw() {
		sb.setProjectionMatrix(Spaceoids.cam.combined);
		sr.setProjectionMatrix(Spaceoids.cam.combined);

		if(client.isConnected())
		{
			if(inGame)
			{
				state = 3;
			}else
			{
				state = 0;
			}
		}else if(client.isKicked())
		{
			state = 2;
		}else
		{
			state = 1;
		}


		if(state == 0)
		{
			sb.begin();
			float width = titleFont.getBounds("Connected to: " + ip).width;
			titleFont.draw(
					sb,
					"Connected to: " + ip ,
					(Spaceoids.WIDTH - width) / 2,
					300
					);	
			sb.end();


		}else if(state == 1)
		{
			sb.begin();
			float width = lastNameFont.getBounds("FAILED TO CONNECT").width;
			lastNameFont.draw(sb, "FAILED TO CONNECT", (Spaceoids.WIDTH - width) / 2, 300);
			firstNameFont.draw(sb, "PRESS ESCAPE TO GO BACK", (Spaceoids.WIDTH - ((width * 2) - 200)) / 2, 200);
			sb.end();


		}else if(state == 2)
		{
			String message = "KICKED FOR \"" + client.network.getReason().toUpperCase() + "\"";
			
			sb.begin();
			float width = lastNameFont.getBounds(message).width;
			lastNameFont.draw(sb, 
					message.toUpperCase(), 
					(Spaceoids.WIDTH - width) / 2, 
					300);
			firstNameFont.draw(sb, "PRESS ESCAPE TO GO BACK", (Spaceoids.WIDTH - ((350 * 2) - 200)) / 2, 200);
			sb.end();

		}else if(state == 3)
		{
			totalPlayers = client.players.size();
			for(MPPlayer mpPlayer: client.players.values())
			{
				PlayerMP player = mpPlayer.getPlayer();
				player.draw(sr);

				for(int i = 0; i < player.bullets.size(); i++)
				{
					player.bullets.get(i).draw(sr);
				}
			}

			// draw bullets
			for(int i = 0; i < you.bullets.size(); i++) {
				you.bullets.get(i).draw(sr);
			}

			// draw particles
			for(int i = 0; i < particles.size(); i++)
			{
				particles.get(i).draw(sr);
			}

			for(int i = 0; i < asteroids.size(); i++)
			{
				asteroids.get(i).draw(sr);
			}

			you.draw(sr);

			// draw lives
			for(int i = 0; i < you.getLives(); i++) {
				hudPlayer.setPosition(600 + i * 10, 60);
				hudPlayer.draw(sr);
			}

			sb.setColor(1, 1, 1, 1);
			sb.begin();

			idFont.draw(sb, "You", you.getX() - 20, you.getY() + 10);

			for(MPPlayer mpPlayer: client.players.values())
			{
				PlayerMP player = mpPlayer.getPlayer();
				idFont.draw(sb, "ID: " + player.getID(), player.getX() - 20, player.getY() + 10);
			}

			font.draw(sb, Long.toString(you.getScore()), 30, 80);
			font.draw(sb, "Your ID:  " + you.getID(), 30, 60);
			font.draw(sb, "Players on server:" + totalPlayers, 30, 40);


			sb.end();


		}
	}


	public void checkCollisions()
	{
		// player-player collision
		if(!you.isHit()) {
			if(you.deathWrap)
			{
				you.loseLife();
			}
			for(MPPlayer mpPlayer: client.players.values())
			{
				PlayerMP player = mpPlayer.getPlayer();
				if(you.intersects(player))
				{
					you.hit();
					you.decreaseScore(20);
					System.out.println("Crashed into a player");
				}
				if(player.intersects(you))
				{
					player.hit();
					System.out.println("Player crashed into a YOU");
				}
				
				for(int i = 0; i < player.bullets.size(); i++)
				{
					if(player.bullets.get(i) != null)
					{
						if(you.contains(player.bullets.get(i).getX(), player.bullets.get(i).getY()))
						{ 
							you.hit();
							you.decreaseScore(30);						
							Jukebox.play("explode");
						}

					} 
				} 
			}
		}

	}


	@Override
	public void handleInput() {

		if(state == 0 && GameKeys.isDown(GameKeys.ENTER))
		{
			inGame = true;
			state = 3;
			for(int i = 0; i < 8; i++) {
				createParticles(you.getX(), you.getY());
			}

		}

		if(state < 3 && GameKeys.isDown(GameKeys.ESCAPE))
		{
			gsm.setState(gsm.MENU);
		}

		if(state == 3)
		{
			you.setLeft(GameKeys.isDown(GameKeys.LEFT));
			you.setUp(GameKeys.isDown(GameKeys.UP));
			you.setRight(GameKeys.isDown(GameKeys.RIGHT));
			if(GameKeys.isPressed(GameKeys.SPACE)) {
				you.shoot();
			}

		}
	}

	@Override
	public void dispose() {

		sb.dispose();
		sr.dispose();
		font.dispose();
		lastNameFont.dispose();
		firstNameFont.dispose();
	}


}

