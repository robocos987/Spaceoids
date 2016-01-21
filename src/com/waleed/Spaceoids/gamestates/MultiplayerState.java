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
import com.badlogic.gdx.math.Vector2;
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
import com.waleed.Spaceoids.network.Network;
import com.waleed.Spaceoids.network.SpaceoidsClient;

public class MultiplayerState extends GameState {

	public static SpaceoidsClient client;

	private SpriteBatch sb;
	private GameStateManager gsm;
	private ShapeRenderer sr;


	private BitmapFont titleFont;
	private BitmapFont font;
	private BitmapFont idFont;
	private BitmapFont firstNameFont;
	private BitmapFont lastNameFont;

	private int state = -1;
	private Player hudPlayer;

	private ArrayList<Bullet> playerrBullets;
	private ArrayList<Particle> particles;
	private  ArrayList<Asteroid> asteroids;

	private Player player;

	private int totalPlayers;
	private boolean inGame;

	private boolean kicked;

	private String message;
	private float countDown = 7.0F;


	public MultiplayerState(GameStateManager gsm) {
		super(gsm);
		this.gsm = gsm;
		hudPlayer = new Player(null);
		kicked = false;
	}		

	@Override
	public void init() {
		sb = new SpriteBatch();
		sr = new ShapeRenderer();
		playerrBullets = new ArrayList<Bullet>();
		particles = new ArrayList<Particle>();
		asteroids = new ArrayList<Asteroid>();
		player = new Player(playerrBullets);
		message = "";
		client = new SpaceoidsClient(GameStateManager.ip, GameStateManager.port, player);
		

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
			player.updateMP(dt);

			for(PlayerMP mpPlayer: client.players.values())
			{
				mpPlayer.newCoords.set(mpPlayer.oldX, mpPlayer.oldY).lerp(new Vector2(mpPlayer.newX, mpPlayer.newY), dt);
				mpPlayer.update(dt);
				// update player bullets
				for(int i = 0; i < mpPlayer.bullets.size(); i++) {
					mpPlayer.bullets.get(i).update(dt);
					if(mpPlayer.bullets.get(i).shouldRemove()) {
						mpPlayer.bullets.remove(i);
						i--;
					}
				}
			}

			if(!message.contentEquals(""))
				countDown -= dt;

			for(int i = 0; i < particles.size(); i++)
			{
				particles.get(i).update(dt);
			}

			// update my bullets
			for(int i = 0; i < player.getBullets().size(); i++) {
				player.getBullets().get(i).update(dt);
				if(player.getBullets().get(i).shouldRemove()) {
					player.getBullets().remove(i);
					i--;
				}
			}

			for(int i = 0; i < asteroids.size(); i++)
			{
				asteroids.get(i).update(dt);
			}

			if(player.isDead())
			{
				/*if(player.getLives() > 0)
				{
					player.reset();
					player.loseLife();
				}*/
				player.reset();
				player.loseLife();
			}

			checkCollisions();
		}else if(state == 1 || state == 2)
		{
			if(!client.players.isEmpty())
			{
				client.players.values().clear();
			}
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
				state = 3;
			else
				state = 0;
		}else if(client.isKicked())
		{
			state = 2;
			client.network.getClient().close();
		}else
			state = 1;
	
		if(state == 0)
		{
			sb.begin();
			float width = titleFont.getBounds("Connected to: " + GameStateManager.ip).width;
			titleFont.draw(
					sb,
					"Connected to: " + GameStateManager.ip ,
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
			totalPlayers = client.players.size() + 1;
			for(PlayerMP mpPlayer: client.players.values())
			{
				mpPlayer.draw(sr);

				for(int i = 0; i < mpPlayer.bullets.size(); i++)
				{
					mpPlayer.bullets.get(i).draw(sr);
				}
			}

			// draw bullets
			for(int i = 0; i < player.getBullets().size(); i++) {
				player.getBullets().get(i).draw(sr);
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

			player.draw(sr);

			// draw lives
			for(int i = 0; i < player.getLives(); i++) {
				hudPlayer.setPosition(600 + i * 10, 60);
				hudPlayer.draw(sr);
			}

			sb.setColor(1, 1, 1, 1);
			sb.begin();

			idFont.draw(sb, "player", player.getX() - 20, player.getY() + 10);

			for(PlayerMP mpPlayer: client.players.values())
			{
				idFont.draw(sb, "ID: " + mpPlayer.getID(), mpPlayer.getX() - 20, mpPlayer.getY() + 10);
			}

			if(message != null && !message.isEmpty())
			{
				if(this.countDown > 3.0F)
				{
					font.setScale(.9f,.9f);
					if(message.length() > 10)
						font.draw(sb, "Server: " + message, (Spaceoids.WIDTH / 2) - 80, Spaceoids.HEIGHT / 2);
					else
						font.draw(sb, "Server: " + message, (Spaceoids.WIDTH / 2) - 30, Spaceoids.HEIGHT / 2);
						
				}
			}
			font.draw(sb, "playerr score:" + Long.toString(player.getScore()), 30, 80);
			font.draw(sb, "playerr ID:  " + player.getID(), 30, 60);
			font.draw(sb, "Players on server:" + totalPlayers, 30, 40);


			sb.end();


		}
	}

	public void checkCollisions()
	{
		// player-player collision
		if(!player.isHit()) {
			if(player.deathWrap)
			{
				player.loseLife();
			}
			for(PlayerMP mpPlayer: client.players.values())
			{
				if(player.intersects(mpPlayer))
				{
					player.hit();
					player.decreaseScore(20);
					System.out.println("Crashed into a player");
				}
				if(mpPlayer.intersects(player))
				{
					mpPlayer.hit();
					System.out.println("Player crashed into a player");
				}


				for(int i = 0; i < mpPlayer.bullets.size(); i++)
				{
					if(mpPlayer.bullets.get(i) != null)
					{
						if(player.contains(mpPlayer.bullets.get(i).getX(), mpPlayer.bullets.get(i).getY()))
						{ 
							player.hit();
							player.decreaseScore(30);						
							Jukebox.play("explode");
						}

					} 
				} 
				
				for(int i = 0; i < player.getBullets().size(); i++)
				{
					if(player.getBullets().get(i) != null)
					{
						if(mpPlayer.contains(player.getBullets().get(i).getX(), player.getBullets().get(i).getY()))
						{ 
							mpPlayer.hit();
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
				createParticles(player.getX(), player.getY());
			}

		}

		if(state < 3 && GameKeys.isDown(GameKeys.ESCAPE))
		{
			gsm.setState(gsm.MENU);

		}

		if(state == 3)
		{
			player.setLeft(GameKeys.isDown(GameKeys.LEFT));
			player.setUp(GameKeys.isDown(GameKeys.UP));
			player.setRight(GameKeys.isDown(GameKeys.RIGHT));
			if(GameKeys.isPressed(GameKeys.SPACE)) {
				player.shoot();
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

	public static Network getClient() {
		// TODO Auto-generated method stub
		return client.network;
	}



}

