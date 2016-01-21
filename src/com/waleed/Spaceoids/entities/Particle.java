package com.waleed.Spaceoids.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;

public class Particle extends SpaceObject {
	
	private float timer;
	private float time;
	private boolean remove;
	
	public Particle(float x, float y) {
		
		this.x = x;
		this.y = y;
		width = height = 2;
		
		speed = 50;
		radians = MathUtils.random(2 * 3.1415f);
		dx = MathUtils.cos(radians) * speed;
		dy = MathUtils.sin(radians) * speed;
		
		timer = 0;
		time = 1;
		
	}
	
	public boolean shouldRemove() { return remove; }
	
	public void update(float dt) {
		
		x += dx * dt;
		y += dy * dt;
		
		timer += dt;
		if(timer > time) {
			remove = true;
		}
		
	}
	
	public void draw(ShapeRenderer sr) {
		sr.setColor(1, 1, 1, 1);
		sr.begin(ShapeType.Filled);
		sr.circle(x - width / 2,  y - width / 2, width / 2);
		sr.end();
	}
	
}

















