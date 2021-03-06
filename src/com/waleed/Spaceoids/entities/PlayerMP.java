package com.waleed.Spaceoids.entities;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.waleed.Spaceoids.main.Spaceoids;
import com.waleed.Spaceoids.main.SpaceoidsMain;
import com.waleed.Spaceoids.managers.Jukebox;


public class PlayerMP extends SpaceObject {
	
	private final int MAX_BULLETS = 4;
	public ArrayList<Bullet> bullets;
	
	private float[] flamex;
	private float[] flamey;
	
	public boolean up;
	
	private float maxSpeed;
	public float acceleration;
	public float deceleration;
	public float acceleratingTimer;
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
	public Vector2 newCoords;	
	public float ppx, ppdx, npx, npdx;
	public float ppy, ppdy, npy, npdy;
	public float ppr, ppdr, npr, npdr;
	public float cpd; //current packet delta
	
	public PlayerMP(int id, ArrayList<Bullet> bullets) {
		
		
		this.bullets = new ArrayList<Bullet>();
		
		this.id = id;
		
		x = Spaceoids.WIDTH / 2;
		y = Spaceoids.HEIGHT / 2;
				
		this.newCoords = new Vector2(x, y);
		
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
		
		flamex[1] =  x + MathUtils.cos(radians - 3.1415f) *
					(6 + acceleratingTimer * 50);
		flamey[1] = y + MathUtils.sin(radians - 3.1415f) *
				(6 + acceleratingTimer * 50);
		
		flamex[2] = x + MathUtils.cos(radians + 5 * 3.1415f / 6) * 5;
		flamey[2] = y + MathUtils.sin(radians + 5 * 3.1415f / 6) * 5;
	}
	
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
	
	

	public float getX()
	{
	  return this.x;
	}
	
	public float getY()
	{
	   return this.y;
	}
	
	public float getDX()
	{
	  return this.dx;
	}
	
	public float getDY()
	{
	  return this.dy;
	}
	
	public void update(float dt) {
		cpd += dt;
		float delta = min(cpd * 2.0f, 1); //This should be right, but if you notice small halts in movement, try tweaking the six to a different value.
		this.x = lerp(ppx + ppdx*delta, npx - npdx * delta, delta);
		this.y = lerp(ppy + ppdy*delta, npy - npdy * delta, delta);
		this.radians = lerp(ppr + ppdr * delta, npr - npdr * delta, delta); //approximate quadratic bezier interpolation
		
		// set shape
		setShape();
		
		// set flame
		if(up)
		    setFlame();
		
		// screen wrap
		wrap();
		
		
//		if(up) {
//			dx += MathUtils.cos(radians) * acceleration * dt;
//			dy += MathUtils.sin(radians) * acceleration * dt;
//			acceleratingTimer += dt;
//			if(acceleratingTimer > 0.1f) {
//				acceleratingTimer = 0;
//			}
//		}
//		else {
//			acceleratingTimer = 0;
//		}
	}
	
	public float min(float a, float b)
	{
		return a > b ? a : b;
	}
	
	public float lerp(float a, float b, float x)
	{
		return (b - a) * x + a;
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
			}*/
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
}


















