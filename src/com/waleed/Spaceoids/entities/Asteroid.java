package com.waleed.Spaceoids.entities;

import java.util.Random;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.waleed.Spaceoids.main.Spaceoids;

public class Asteroid extends SpaceObject {

	private int type;
	public static final int SMALL = 0;
	public static final int MEDIUM = 1;
	public static final int LARGE = 2;

	private Player player;

	float previousRadians;

	private int numPoints;
	private float[] dists;

	private int score;

	private boolean remove;


	public Asteroid(float x, float y, int type) {
		this.x = x;
		this.y = y;
		this.type = type;

		if(FlyingSaucer.player != null) player = FlyingSaucer.player;

		if(type == SMALL) {
			numPoints = 8;
			width = height = 12;
			speed = MathUtils.random(70, 100);
			score = 100;
		}
		else if(type == MEDIUM) {
			numPoints = 10;
			width = height = 20;
			speed = MathUtils.random(50, 60);
			score = 50;
		}
		else if(type == LARGE) {
			numPoints = 12;
			width = height = 40;
			speed = MathUtils.random(20, 30);
			score = 20;
		}

		rotationSpeed = MathUtils.random(-1, 1);

		radians = MathUtils.random(2 * 3.1415f);
		dx = MathUtils.cos(radians) * speed;
		dy = MathUtils.sin(radians) * speed;

		shapex = new float[numPoints];
		shapey = new float[numPoints];
		dists = new float[numPoints];

		int radius = width / 2;
		for(int i = 0; i < numPoints; i++) {
			dists[i] = MathUtils.random(radius / 2, radius);
		}

		setShape();

	}

	private void setShape() {
		float angle = 0;
		for(int i = 0; i < numPoints; i++) {
			shapex[i] = x + MathUtils.cos(angle + radians) * dists[i];
			shapey[i] = y + MathUtils.sin(angle + radians) * dists[i];
			angle += 2 * 3.1415f / numPoints;
		}
	}

	public int getType() { return type; }
	public boolean shouldRemove() { return remove; }
	public int getScore() { return score; }
	

	public void update(float dt) {

		x += dx * dt;
		y += dy * dt;
		radians += rotationSpeed * dt;
		dx = MathUtils.cos(radians) * speed;
		dy = MathUtils.sin(radians) * speed;
		
//		if(!FlyingSaucer.remove && FlyingSaucer.player != null) radians = MathUtils.atan2(FlyingSaucer.player.y - y, FlyingSaucer.player.x - x);
	
		
		setShape();

		wrap();

	}
	
	public boolean specialMove()
	{
		return special;
	}

	public void draw(ShapeRenderer sr) {
		sr.setColor(1, 1, 1, 1);
		sr.begin(ShapeType.Line);
		for(int i = 0, j = shapex.length - 1;
				i < shapex.length;
				j = i++) {

			sr.line(shapex[i], shapey[i], shapex[j], shapey[j]);
		}
		sr.end();
	}

	public void setSpecial(boolean special) {
		special = true;
		this.special = special;
	}

}


















