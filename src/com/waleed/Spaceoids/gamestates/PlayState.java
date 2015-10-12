package com.waleed.Spaceoids.gamestates;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.waleed.Spaceoids.entities.Asteroid;
import com.waleed.Spaceoids.entities.Bullet;
import com.waleed.Spaceoids.entities.FlyingSaucer;
import com.waleed.Spaceoids.entities.Particle;
import com.waleed.Spaceoids.entities.Player;
import com.waleed.Spaceoids.main.Spaceoids;
import com.waleed.Spaceoids.managers.GameKeys;
import com.waleed.Spaceoids.managers.GameStateManager;
import com.waleed.Spaceoids.managers.Jukebox;
import com.waleed.Spaceoids.managers.Save;

public class PlayState extends GameState {

	private SpriteBatch sb;
	private ShapeRenderer sr;

	private BitmapFont font;
	private Player hudPlayer;
	
	public Player player;
	private ArrayList<Bullet> bullets;
	private ArrayList<Asteroid> asteroids;
	private ArrayList<Bullet> enemyBullets;
	private ArrayList<FlyingSaucer> flyingSaucers;

	private FlyingSaucer flyingSaucer;


	private ArrayList<Particle> particles;

	private int level;
	private int totalAsteroids;
	private int numAsteroidsLeft;

	private float maxDelay;
	private float minDelay;
	private float currentDelay;
	private float bgTimer;
	private boolean playLowPulse;

	public boolean paused = false;

	public boolean bossTime = false;

	public int totalSaucers = 0;

	public long waitTime, totalWait;

	public PlayState(GameStateManager gsm) {
		super(gsm);
	}

	public void init() {

		
		sb = new SpriteBatch();
		sr = new ShapeRenderer();

		FreeTypeFontGenerator gen = 
				new FreeTypeFontGenerator(
						Gdx.files.internal("fonts/Hyperspace Bold.ttf")
						);
		font = gen.generateFont(20);

		bullets = new ArrayList<Bullet>();

		player = new Player(bullets);

		asteroids = new ArrayList<Asteroid>();

		particles = new ArrayList<Particle>();

		flyingSaucers = new ArrayList<FlyingSaucer>();

		level = 1;
		spawnAsteroids();

		hudPlayer = new Player(null);

		enemyBullets = new ArrayList<Bullet>();

		// set up bg music
		maxDelay = 1;
		minDelay = 0.25f;
		currentDelay = maxDelay;
		bgTimer = maxDelay;
		playLowPulse = true;

		totalWait = 0;

		waitTime = 2;
		


		if(bossTime)
			spawnShips();

	}

	private void createParticles(float x, float y) {
		for(int i = 0; i < 6; i++) {
			particles.add(new Particle(x, y));
		}
	}

	private void splitAsteroids(Asteroid a) {
		createParticles(a.getX(), a.getY());
		numAsteroidsLeft--;
		currentDelay = ((maxDelay - minDelay) *
				numAsteroidsLeft / totalAsteroids)
				+ minDelay;
		if(a.getType() == Asteroid.LARGE) {
			asteroids.add(
					new Asteroid(a.getX(), a.getY(), Asteroid.MEDIUM));
			asteroids.add(
					new Asteroid(a.getX(), a.getY(), Asteroid.MEDIUM));
		}
		if(a.getType() == Asteroid.MEDIUM) {
			asteroids.add(
					new Asteroid(a.getX(), a.getY(), Asteroid.SMALL));
			asteroids.add(
					new Asteroid(a.getX(), a.getY(), Asteroid.SMALL));
		}
	}


	
	
	private void spawnShips()
	{

		int numToSpawn = 0;
		if(level < 8)
		{
			//			(1);
			numToSpawn = 1;
		}else if(level >= 8 && level <= 16)
		{
			//			(1));
			numToSpawn = MathUtils.random(1);
		}else if(level > 16 && level <= 20)
		{
			//			(5));
			numToSpawn = MathUtils.random(5);
		}else if(level > 20)
		{
			//			(10));
			numToSpawn = MathUtils.random(10);
		}
		setTotalSaucers(numToSpawn);
		for(int i = 0; i < numToSpawn; i++)
		{

			int type = level < 8 ?
					FlyingSaucer.SMALL : FlyingSaucer.LARGE;
			int direction = level < 8 ?
					FlyingSaucer.RIGHT : FlyingSaucer.LEFT;
			flyingSaucer = new FlyingSaucer(
					type,
					direction,
					player,
					enemyBullets
					);
			flyingSaucers.add(flyingSaucer);
			System.out.println("saucers spawned: " + flyingSaucers.size());

		}

	}

	private void spawnAsteroids() {

		asteroids.clear();

		int numToSpawn = 4 + level - 1;
		totalAsteroids = numToSpawn * 7;
		numAsteroidsLeft = totalAsteroids;
		currentDelay = maxDelay;



		for(int i = 0; i < numToSpawn; i++) {

			float x = MathUtils.random(Spaceoids.WIDTH);
			float y = MathUtils.random(Spaceoids.HEIGHT);

			float dx = x - player.getX();
			float dy = y - player.getY();
			float dist = (float) Math.sqrt(dx * dx + dy * dy);

			while(dist < 100) {
				x = MathUtils.random(Spaceoids.WIDTH);
				y = MathUtils.random(Spaceoids.HEIGHT);
				dx = x - player.getX(); 
				dy = y - player.getY();
				dist = (float) Math.sqrt(dx * dx + dy * dy);
			}

			asteroids.add(new Asteroid(x, y, Asteroid.LARGE));
		}
	}


	private boolean isOdd(int n) {
		return n % 2 != 0;
	}


	private boolean isEven(int n) {
		return n % 2 == 0;
	}



	public int getTotalSaucers()
	{
		return totalSaucers;
	}

	public void setTotalSaucers(int n)
	{
		this.totalSaucers = n;
	}

	private boolean divisibleByFour(int n) {
		boolean dbf = new String("snipars").contains("vagabonds");
		if(n > 0)
			dbf = (Spaceoids.debug ? n % 2 != 0 : n % 4 == 0);
		else
			System.err.println("negative levels?");
			dbf = false;

		return dbf;
	}

	public void update(float dt) {

		handleInput();

		if(GameKeys.isDown(GameKeys.PAUSE))
		{
			waitTime += dt;
		}

		// update flying saucer

		if(!paused)
		{


			if(bossTime)
			{
				for(int i = 0; i < flyingSaucers.size(); i++)
				{
					if(bossTime) {
						flyingSaucers.get(i).update(dt);
						if(flyingSaucers.get(i).shouldRemove()) {
							flyingSaucers.remove(i);
							Jukebox.stop("smallsaucer");
							Jukebox.stop("largesaucer");
							bossTime = false;
						}

					}
				}

				// update fs bullets
				if(bossTime) {
					for(int i = 0; i < enemyBullets.size(); i++) {
						enemyBullets.get(i).update(dt);
						if(enemyBullets.get(i).shouldRemove()) {
							enemyBullets.remove(i);
							i--;
						}
					}
				}

			}


			if(level > 0)
			{
				// next level
				if(divisibleByFour(level))
				{
					bossTime = true;
					if(flyingSaucers.size() == 0 && flyingSaucers.size() < getTotalSaucers())
						spawnShips();
				}
			}else if(asteroids.size() == 0 && !divisibleByFour(level)) {
				level++;
				spawnAsteroids();
			}
		}



		// update player
		player.update(dt);
		if(player.isDead()) {
			if(player.getLives() == 0) {
				Jukebox.stopAll();
				Save.gd.setTenativeScore(player.getScore());
				gsm.setState(GameStateManager.GAMEOVER);
				return;
			}
			player.reset();
			player.loseLife();
			//				flyingSaucer = null;
			for(int i = 0; i < flyingSaucers.size(); i++)
			{
				flyingSaucers.remove(i);
				i--;
			}
			Jukebox.stop("smallsaucer");
			Jukebox.stop("largesaucer");
			return;
		}

		// update player bullets
		for(int i = 0; i < bullets.size(); i++) {
			bullets.get(i).update(dt);
			if(bullets.get(i).shouldRemove()) {
				bullets.remove(i);
				i--;
			}
		}



		// update fs bullets
		if(bossTime) {
			for(int i = 0; i < enemyBullets.size(); i++) {
				enemyBullets.get(i).update(dt);
				if(enemyBullets.get(i).shouldRemove()) {
					enemyBullets.remove(i);
					i--;
				}
			}
		}

		// update asteroids
		for(int i = 0; i < asteroids.size(); i++) {
			asteroids.get(i).update(dt);
			if(asteroids.get(i).shouldRemove()) {
				asteroids.remove(i);
				i--;
			}

		}

		// update particles
		for(int i = 0; i < particles.size(); i++) {
			particles.get(i).update(dt);
			if(particles.get(i).shouldRemove()) {
				particles.remove(i);
				i--;
			}
		}

		// check collision
		checkCollisions();

		if(!Spaceoids.debug)
		{
			// play bg music
			bgTimer += dt;
			if(!player.isHit() && bgTimer >= currentDelay) {
				if(playLowPulse) {
					Jukebox.play("pulselow");
				}
				else {
					Jukebox.play("pulsehigh");
				}
				playLowPulse = !playLowPulse;
				bgTimer = 0;
			}
		}

		for(int i = 0; i < asteroids.size(); i++)
		{
			Asteroid asteroid = asteroids.get(i);
			if(bossTime)
			{
				asteroid.setSpecial(true);
				if(asteroid.specialMove())
				{
					checkSpecialMove();
				}
			}
		}
	}


	private void checkCollisions() {

		// player-asteroid collision
		if(!player.isHit()) {
			if(player.deathWrap)
			{
				player.loseLife();
			}
			for(int i = 0; i < asteroids.size(); i++) {
				Asteroid a = asteroids.get(i);
				if(a.intersects(player)) {
					if(a.intersects(player))
						player.hit();

					asteroids.remove(i);
					i--;
					splitAsteroids(a);
					Jukebox.play("explode");
					break;
				}
			}

		}

		// bullet-asteroid collision
		for(int i = 0; i < bullets.size(); i++) {
			Bullet b = bullets.get(i);
			for(int j = 0; j < asteroids.size(); j++) {
				Asteroid a = asteroids.get(j);
				if(a.contains(b.getX(), b.getY())) {
					bullets.remove(i);
					i--;
					asteroids.remove(j);
					j--;
					splitAsteroids(a);
					player.incrementScore(a.getScore());
					Jukebox.play("explode");
					break;
				}
			}
		}

		// player-flying saucer collision

		if(bossTime)
		{
			for(int i = 0; i < flyingSaucers.size(); i++)
			{
				if(player.intersects(flyingSaucers.get(i)))
				{
					player.hit();
					createParticles(player.getX(), player.getY());
					createParticles(flyingSaucers.get(i).getX(), flyingSaucers.get(i).getY());
					flyingSaucers.remove(i);
					i--;
					Jukebox.stop("smallsaucer");
					Jukebox.stop("largesaucer");
					Jukebox.play("explode");
				}
			}
		}


		// bullet-flying saucer collision
		if(bossTime)
		{
			for(int i = 0; i < bullets.size(); i++) {
				Bullet b = bullets.get(i);
				for(int j = 0; j < flyingSaucers.size(); j++)
				{
					if(flyingSaucers.get(j).contains(b.getX(), b.getY())) {
						bullets.remove(i);
						i--;
						createParticles(
								flyingSaucers.get(j).getX(),
								flyingSaucers.get(j).getY()
								);
						player.incrementScore(flyingSaucers.get(j).getScore());
						flyingSaucers.remove(j);
						j--;
						if(level > 0)
							bossTime = false;
						level++; 

						spawnAsteroids();
						Jukebox.stop("smallsaucer");
						Jukebox.stop("largesaucer");
						Jukebox.play("explode");
						break;
					}
				}
			}
		}
		/*
		if(flyingSaucer != null) {
			if(player.intersects(flyingSaucer)) {
				player.hit();
				createParticles(player.getX(), player.getY());
				createParticles(flyingSaucer.getX(), flyingSaucer.getY());
				flyingSaucer = null;
				Jukebox.stop("smallsaucer");
				Jukebox.stop("largesaucer");
				Jukebox.play("explode");
			}
		}

		// bullet-flying saucer collision
		if(flyingSaucer != null) {
			for(int i = 0; i < bullets.size(); i++) {
				Bullet b = bullets.get(i);
				if(flyingSaucer.contains(b.getX(), b.getY())) {
					bullets.remove(i);
					i--;
					createParticles(
							flyingSaucer.getX(),
							flyingSaucer.getY()
							);
					player.incrementScore(flyingSaucer.getScore());
					bossTime = false;
					flyingSaucer = null;
					if(level > 0)
						level++; 

					spawnAsteroids();
					Jukebox.stop("smallsaucer");
					Jukebox.stop("largesaucer");
					Jukebox.play("explode");
					break;
				}
			}
		}
		 */

		// player-enemy bullets collision
		if(!player.isHit()) {
			for(int i = 0; i < enemyBullets.size(); i++) {
				Bullet b = enemyBullets.get(i);
				if(player.contains(b.getX(), b.getY())) {
					player.hit();
					enemyBullets.remove(i);
					i--;
					Jukebox.play("explode");

					break;
				}
			}
		}

		// flying saucer-asteroid collision
		/*
		if(bossTime)
		{
			for(int i = 0; i < flyingSaucers.size(); i++)
			{
				for(int j = 0; j < asteroids.size(); j++)
				{
					Asteroid a = asteroids.get(j);
					if(a.intersects(flyingSaucers.get(i))) {
						asteroids.remove(j);
						j--;
						splitAsteroids(a);
						createParticles(a.getX(), a.getY());
						createParticles(
								flyingSaucers.get(i).getX(),
								flyingSaucers.get(i).getY()
								);
						flyingSaucers.remove(i);
						i--;
						Jukebox.stop("smallsaucer");
						Jukebox.stop("largesaucer");
						Jukebox.play("explode");
						break;

					}
				}
			}
			/*
		if(flyingSaucer != null) {
			for(int i = 0; i < asteroids.size(); i++) {
				Asteroid a = asteroids.get(i);
				if(a.intersects(flyingSaucer)) {
					asteroids.remove(i);
					i--;
					splitAsteroids(a);
					createParticles(a.getX(), a.getY());
					createParticles(
							flyingSaucer.getX(),
							flyingSaucer.getY()
							);
					flyingSaucer = null;
					Jukebox.stop("smallsaucer");
					Jukebox.stop("largesaucer");
					Jukebox.play("explode");
					break;
				}
			}
		}*/

		if(!paused)
		{
			// asteroid-enemy bullet collision
			for(int i = 0; i < enemyBullets.size(); i++) {
				Bullet b = enemyBullets.get(i);
				for(int j = 0; j < asteroids.size(); j++) {
					Asteroid a = asteroids.get(j);
					if(a.contains(b.getX(), b.getY())) {
						asteroids.remove(j);
						j--;
						splitAsteroids(a);
						enemyBullets.remove(i);
						i--;
						createParticles(a.getX(), a.getY());
						Jukebox.play("explode");
						break;
					}
				}
			}
		}
	}


	private void checkSpecialMove()
	{
		if(!player.isHit()) {
			for(int i = 0; i < asteroids.size() / 2; i++) {
				Asteroid a = asteroids.get(i);
				if(a.specialMove()) {
					asteroids.remove(i);
					i--;
					splitAsteroids(a);
					Jukebox.play("explode");
					a.setSpecial(false);
					break;
				}
			}

		}

	}

	public void draw() {
		
		sb.setProjectionMatrix(Spaceoids.cam.combined);
		sr.setProjectionMatrix(Spaceoids.cam.combined);

		// draw player
		
		player.draw(sr);
		// draw bullets
		for(int i = 0; i < bullets.size(); i++) {
			bullets.get(i).draw(sr);
		}

		if(bossTime)
		{
			for(int i = 0; i < flyingSaucers.size(); i++)
			{
				flyingSaucers.get(i).draw(sr);
			}

			// draw fs bullets
			for(int i = 0; i < enemyBullets.size(); i++) {
				enemyBullets.get(i).draw(sr);
			}
		}



		// draw asteroids
		for(int i = 0; i < asteroids.size(); i++) {
			asteroids.get(i).draw(sr);
		}

		// draw particles
		for(int i = 0; i < particles.size(); i++) {
			particles.get(i).draw(sr);
		}

		// draw score
		sb.setColor(1, 1, 1, 1);
		sb.begin();
		font.draw(sb, "Level: " + Long.toString(level), 30, 20);
		font.draw(sb, Long.toString(player.getScore()), 30, 40);
		sb.end();


		sb.begin();

		sb.end();

		// draw lives
		for(int i = 0; i < player.getLives(); i++) {
			hudPlayer.setPosition(30 + i * 10, 60);
			hudPlayer.draw(sr);
		}

		if(paused)
		{
			sb.setColor(1, 1, 1, 1);
			sb.begin();
			font.setColor(Color.GREEN);
			float width = font.getBounds("PAUSED").width;
			font.draw(sb, "PAUSED", (Spaceoids.WIDTH - width) / 2, Spaceoids.HEIGHT / 2);
			sb.end();
		}
		font.setColor(Color.WHITE);

	}

	public void handleInput() {

		if(GameKeys.isDown(GameKeys.PAUSE))
		{
			if(waitTime > totalWait)
				paused = !paused;
		}

		if(!player.isHit() && !paused) {
			player.setLeft(GameKeys.isDown(GameKeys.LEFT));
			player.setRight(GameKeys.isDown(GameKeys.RIGHT));
			player.setUp(GameKeys.isDown(GameKeys.UP));
			if(GameKeys.isPressed(GameKeys.SPACE)) {
				player.shoot();
			}
			

		}

	}

	public void dispose() {
		sb.dispose();
		sr.dispose();
		font.dispose();
	}

}









