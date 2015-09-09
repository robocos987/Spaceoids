package com.waleed.Spaceoids.entities;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.collections4.CollectionUtils;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.waleed.Spaceoids.main.Spaceoids;
import com.waleed.Spaceoids.managers.Jukebox;
import com.waleed.Spaceoids.network.Network;
import com.waleed.Spaceoids.network.packets.PacketUpdateAcceleration;
import com.waleed.Spaceoids.network.packets.PacketUpdateBullet;
import com.waleed.Spaceoids.network.packets.PacketUpdateDeath;
import com.waleed.Spaceoids.network.packets.PacketUpdateFlames;
import com.waleed.Spaceoids.network.packets.PacketUpdateHit;
import com.waleed.Spaceoids.network.packets.PacketUpdatePosition;
import com.waleed.Spaceoids.network.packets.PacketUpdateRotation;
import com.waleed.Spaceoids.network.packets.PacketUpdateStats;

public class Player extends SpaceObject {

	private final int MAX_BULLETS = 4;
	public ArrayList<Bullet> bullets;
	private ArrayList<Bullet> networkBullets;

	private float[] flamex;
	private float[] flamey;

	private boolean left;
	private boolean right;
	private boolean up;

	private boolean netLeft, netRight, netUp;

	private float netRadians;

	private float maxSpeed;
	public float acceleration;
	public float deceleration;
	public float acceleratingTimer;

	private float networkAcceleration;
	private float nAT;

	public boolean hit;
	public boolean dead;

	private boolean networkHit, networkDead;

	private float hitTimer;
	private float hitTime;
	private Line2D.Float[] hitLines;
	private Point2D.Float[] hitLinesVector;

	private float netHitTimer;
	private float netHitTime;
	private Line2D.Float[] nTL;
	private Point2D.Float[] nLV;

	private float netDX, netDY, netX, netY;

	public Network network;

	private long score;
	private int extraLives;
	private long requiredScore;

	private long networkScore;
	private int networkLives;


	public boolean deathWrap = false;

	private Spaceoids INSTANCE;

	public float dt;
	private float netRotationSpeed;
	private boolean networkUp;

	public boolean hasShot;

	public static boolean remove = false;

	public Player(ArrayList<Bullet> bullets) {

		this.bullets = bullets;
		this.networkBullets = new ArrayList<Bullet>();

		x = Spaceoids.WIDTH / 2;
		y = Spaceoids.HEIGHT / 2;

		maxSpeed = 300;
		acceleration = 200;
		deceleration = 10;

		shapex = new float[4];
		shapey = new float[4];
		flamex = new float[3];
		flamey = new float[3];

		radians = 3.1415f / 2;
		rotationSpeed = 3;

		hit = false;
		hitTimer = 0;
		hitTime = 2;

		score = 0;
		extraLives = 3;
		requiredScore = 10000;

	}	

	public Player(float x, float y, ArrayList<Bullet> bullets) {

		this.bullets = bullets;
		this.networkBullets = this.bullets;

		this.x = x;
		this.y = y;

		maxSpeed = 300;
		acceleration = 200;
		deceleration = 10;

		shapex = new float[4];
		shapey = new float[4];
		flamex = new float[3];
		flamey = new float[3];

		radians = 3.1415f / 2;
		rotationSpeed = 3;

		hit = false;
		hitTimer = 0;
		hitTime = 2;

		score = 0;
		extraLives = 3;
		requiredScore = 10000;

	}


	private void setShape() {
		shapex[0] = x + MathUtils.cos(radians) * 8;
		shapey[0] = y + MathUtils.sin(radians) * 8;

		shapex[1] = x + MathUtils.cos(radians - 4 * 3.1415f / 5) * 8;
		shapey[1] = y + MathUtils.sin(radians - 4 * 3.1415f / 5) * 8;

		shapex[2] = x + MathUtils.cos(radians + 3.1415f) * 5;
		shapey[2] = y + MathUtils.sin(radians + 3.1415f) * 5;

		shapex[3] = x + MathUtils.cos(radians + 4 * 3.1415f / 5) * 8;
		shapey[3] = y + MathUtils.sin(radians + 4 * 3.1415f / 5) * 8;
	}

	private void setFlame() {
		flamex[0] = x + MathUtils.cos(radians - 5 * 3.1415f / 6) * 5;
		flamey[0] = y + MathUtils.sin(radians - 5 * 3.1415f / 6) * 5;

		flamex[1] = x + MathUtils.cos(radians - 3.1415f) *
				(6 + acceleratingTimer * 50);
		flamey[1] = y + MathUtils.sin(radians - 3.1415f) *
				(6 + acceleratingTimer * 50);

		flamex[2] = x + MathUtils.cos(radians + 5 * 3.1415f / 6) * 5;
		flamey[2] = y + MathUtils.sin(radians + 5 * 3.1415f / 6) * 5;
	}

	public void setLeft(boolean b) { left = b; }
	public void setRight(boolean b) { right = b; }
	public void setUp(boolean b) {
		if(b && !up && !hit) {
			Jukebox.loop("thruster");
		}
		else if(!b) {
			Jukebox.stop("thruster");
		}
		up = b;
	}

	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		setShape();
	}

	public boolean isHit() { return hit; }
	public boolean isDead() { return dead; }
	public void reset() {
		x = (Spaceoids.WIDTH / 2) + new Random().nextInt(40);
		y = (Spaceoids.HEIGHT / 2) + new Random().nextInt(40);
		setShape();
		hit = dead = false;
	}

	public long getScore() { return score; }
	public int getLives() { return extraLives; }
	public boolean shouldRemove() { return remove; }


	public void loseLife() { extraLives--; }
	public void incrementScore(long l) { score += l; }

	public void shoot() {

		if(bullets.size() == MAX_BULLETS) return;
		bullets.add(new Bullet(x, y, radians));
		Jukebox.play("shoot");
		Spaceoids.cam.project(new Vector3(x, y, 0));

		PacketUpdateBullet packet = new PacketUpdateBullet();
		packet.bullet = bullets;
		network.client.sendUDP(packet);


	}

	public void hit() {

		if(hit) return;

		hit = true;
		dx = dy = 0;
		left = right = up = false;
		Jukebox.stop("thruster");

		hitLines = new Line2D.Float[4];
		for(int i = 0, j = hitLines.length - 1;
				i < hitLines.length;
				j = i++) {
			hitLines[i] = new Line2D.Float(
					shapex[i], shapey[i], shapex[j], shapey[j]
					);
		}

		hitLinesVector = new Point2D.Float[4];
		hitLinesVector[0] = new Point2D.Float(
				MathUtils.cos(radians + 1.5f),
				MathUtils.sin(radians + 1.5f)
				);
		hitLinesVector[1] = new Point2D.Float(
				MathUtils.cos(radians - 1.5f),
				MathUtils.sin(radians - 1.5f)
				);
		hitLinesVector[2] = new Point2D.Float(
				MathUtils.cos(radians - 2.8f),
				MathUtils.sin(radians - 2.8f)
				);
		hitLinesVector[3] = new Point2D.Float(
				MathUtils.cos(radians + 2.8f),
				MathUtils.sin(radians + 2.8f)
				);

	}

	public void update(float dt) {

		this.dt = dt;
		// check if hit
		if(hit) {
			hitTimer += dt;
			if(hitTimer > hitTime) {
				dead = true;
				hitTimer = 0;
			}
			for(int i = 0; i < hitLines.length; i++) {
				hitLines[i].setLine(
						hitLines[i].x1 + hitLinesVector[i].x * 10 * dt,
						hitLines[i].y1 + hitLinesVector[i].y * 10 * dt,
						hitLines[i].x2 + hitLinesVector[i].x * 10 * dt,
						hitLines[i].y2 + hitLinesVector[i].y * 10 * dt
						);
			}
			return;
		}

		// check extra lives
		if(score >= requiredScore) {
			extraLives++;
			requiredScore += 10000;
			Jukebox.play("extralife");
		}

		// turning
		if(left) {
			radians += rotationSpeed * dt;
		}
		else if(right) {
			radians -= rotationSpeed * dt;
		}

		// accelerating
		if(up) {
			dx += MathUtils.cos(radians) * acceleration * dt;
			dy += MathUtils.sin(radians) * acceleration * dt;
			acceleratingTimer += dt;
			if(acceleratingTimer > 0.1f) {
				acceleratingTimer = 0;
			}
		}
		else {
			acceleratingTimer = 0;
		}

		// deceleration
		float vec = (float) Math.sqrt(dx * dx + dy * dy);
		if(vec > 0) {
			dx -= (dx / vec) * deceleration * dt;
			dy -= (dy / vec) * deceleration * dt;
		}
		if(vec > maxSpeed) {
			dx = (dx / vec) * maxSpeed;
			dy = (dy / vec) * maxSpeed;
		}

		// set position
		x += dx * dt;
		y += dy * dt;

		// set shape
		setShape();

		// set flame
		if(up) {
			setFlame();
		}

		// screen wrap
		wrap();
	}

	public void updateMP(float delta)
	{
		network = Spaceoids.getClient();
		if(this.dx != this.netX || this.dy != this.netDY || this.x != this.netX || this.y != this.netY)
		{
			PacketUpdatePosition pup = new PacketUpdatePosition();
			pup.dx = this.dx;
			pup.dy = this.dy;
			pup.x = this.x;
			pup.y = this.y;

			network.client.sendUDP(pup);

			this.setNetPosition(x, y);
			this.setDeltaPosition(dx, dy);
		}

		if(this.hit != this.networkHit || this.hitTimer != this.netHitTimer || this.hitTime != this.netHitTime
				|| this.hitLines != this.nTL || this.hitLinesVector != this.nLV)
		{
			PacketUpdateHit packet = new PacketUpdateHit();
			packet.hit = this.hit;
			packet.hitTimer = this.hitTimer;
			packet.hitTime = this.hitTime;
			packet.netHitLines = this.nTL;
			packet.netHitLinesVec = this.nLV;

			network.client.sendUDP(packet);

			//			this.nTL = this.hitLines;			
			//			this.nLV = this.hitLinesVector;
			this.networkHit = this.hit;
			this.netHitTimer = this.hitTimer;
			this.netHitTime = this.hitTime;

		}


		if(this.dead != this.networkDead)
		{
			PacketUpdateDeath packet = new PacketUpdateDeath();
			packet.death = this.dead;
			network.client.sendUDP(packet);

			this.networkDead = this.dead;
		}

		if(this.radians != netRadians || this.rotationSpeed != this.netRotationSpeed)
		{
			PacketUpdateRotation packet = new PacketUpdateRotation();
			packet.radians = this.radians;
			packet.rotationSpeed = this.rotationSpeed;
			network.client.sendUDP(packet);

			this.netRotationSpeed = this.rotationSpeed;
			this.netRadians = this.radians;
		}

		if(this.nAT != this.acceleratingTimer || this.networkAcceleration != this.acceleration)
		{
			PacketUpdateAcceleration packet = new PacketUpdateAcceleration();
			packet.accleration = this.acceleration;
			packet.acclerationTimer = this.acceleratingTimer;
			network.client.sendUDP(packet);

			this.nAT = this.acceleratingTimer;
			this.networkAcceleration = this.acceleration;
		}

		if(this.networkUp != this.up)
		{
			PacketUpdateFlames packet = new PacketUpdateFlames();
			packet.up = this.up;
			network.client.sendUDP(packet);

			this.networkUp = this.up;
		}

		if(this.networkLives != this.getLives() || this.networkScore != this.score)
		{
			PacketUpdateStats packet = new PacketUpdateStats();
			packet.score = this.score;
			packet.extraLives = this.extraLives;
			network.client.sendTCP(packet);

			this.networkScore = this.score;
			this.networkLives = this.extraLives;
		}



		this.dt = delta;
		// check if hit
		if(hit) {
			hitTimer += dt;
			if(hitTimer > hitTime) {
				dead = true;
				hitTimer = 0;
			}
			for(int i = 0; i < hitLines.length; i++) {
				hitLines[i].setLine(
						hitLines[i].x1 + hitLinesVector[i].x * 10 * dt,
						hitLines[i].y1 + hitLinesVector[i].y * 10 * dt,
						hitLines[i].x2 + hitLinesVector[i].x * 10 * dt,
						hitLines[i].y2 + hitLinesVector[i].y * 10 * dt
						);
			}
			return;
		}

		// check extra lives
		if(score >= requiredScore) {
			extraLives++;
			requiredScore += 10000;
			Jukebox.play("extralife");
		}

		// turning
		if(left) {
			radians += rotationSpeed * dt;
		}
		else if(right) {
			radians -= rotationSpeed * dt;
		}

		// accelerating
		if(up) {
			dx += MathUtils.cos(radians) * acceleration * dt;
			dy += MathUtils.sin(radians) * acceleration * dt;
			acceleratingTimer += dt;
			if(acceleratingTimer > 0.1f) {
				acceleratingTimer = 0;
			}
		}
		else {
			acceleratingTimer = 0;
		}

		// deceleration
		float vec = (float) Math.sqrt(dx * dx + dy * dy);
		if(vec > 0) {
			dx -= (dx / vec) * deceleration * dt;
			dy -= (dy / vec) * deceleration * dt;
		}
		if(vec > maxSpeed) {
			dx = (dx / vec) * maxSpeed;
			dy = (dy / vec) * maxSpeed;
		}

		// set position
		x += dx * dt;
		y += dy * dt;

		// set shape
		setShape();

		// set flame
		if(up) {
			setFlame();
		}

		// screen wrap
		wrap();
	}

	private void setNetPosition(float x, float y) {
		this.netX = x;
		this.netY = y;
	}

	public void setDeltaPosition(float dx, float dy) {
		this.netDX = dx;
		this.netDY = dy;
	}


	public void draw(ShapeRenderer sr) {

		sr.setColor(1, 1, 1, 1);

		sr.begin(ShapeType.Line);

		// check if hit
		if(hit) {
			for(int i = 0; i < hitLines.length; i++) {
				sr.line(
						hitLines[i].x1,
						hitLines[i].y1,
						hitLines[i].x2,
						hitLines[i].y2
						);
			}
			sr.end();
			return;
		}

		// draw ship
		for(int i = 0, j = shapex.length - 1;
				i < shapex.length;
				j = i++) {

			sr.line(shapex[i], shapey[i], shapex[j], shapey[j]);

		}

		// draw flames
		if(up) {
			for(int i = 0, j = flamex.length - 1;
					i < flamex.length;
					j = i++) {

				sr.line(flamex[i], flamey[i], flamex[j], flamey[j]);

			}
		}


		sr.end();

	}

	public void decreaseScore(int i) {
		this.score -= i;
	}



}


















