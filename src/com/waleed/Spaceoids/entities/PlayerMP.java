package com.waleed.Spaceoids.entities;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.waleed.Spaceoids.main.Spaceoids;
import com.waleed.Spaceoids.managers.Jukebox;

public class PlayerMP extends SpaceObject {
	
	private final int MAX_BULLETS = 4;
	public ArrayList<Bullet> bullets;
	
	private float[] flamex;
	private float[] flamey;
	
	private boolean left;
	private boolean right;
	public boolean up;
	
	private float maxSpeed;
	public float acceleration;
	public float deceleration;
	public float acceleratingTimer;
	public float dt;
	
	public boolean hit;
	public boolean dead;
	
	public float time;

	
	public float hitTimer;
	public float hitTime;
	public Line2D.Float[] hitLines;
	public Point2D.Float[] hitLinesVector;
	
	public long score;
	public int extraLives;
	private long requiredScore;
	
	public boolean deathWrap = false;
	

	
	public static boolean remove = false;
	
	public int id;
	public float ping;
		
	public PlayerMP(int id, float x, float y, ArrayList<Bullet> bullets) {
		
		
		this.bullets = new ArrayList<Bullet>();
		
		this.id = id;
		
		x = Spaceoids.WIDTH / 2;
		y = Spaceoids.HEIGHT / 2;
		
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

	
	public int getID()
	{
		return id;
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
		x = Spaceoids.WIDTH / 2;
		y = Spaceoids.HEIGHT / 2;
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
		bullets.add(new Bullet(x, y, radians, new Color(1, 500, 1, 10)));
		Jukebox.play("shoot"); 
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
	
	public void draw(ShapeRenderer sr) {
		
		sr.setColor(1, 500, 1, 10);
		
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

	public float lerp (float fromValue, float toValue, float progress) {
		return (fromValue + (toValue - fromValue) * progress);
	}
	
}


















