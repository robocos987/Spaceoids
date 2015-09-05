package com.waleed.Spaceoids.entities;

import java.util.Random;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;

public class Bullet extends SpaceObject {

	private float lifeTime;
	private float lifeTimer;

	private boolean remove;

	public Bullet(float x, float y, float radians) {

		this.x = x;
		this.y = y;
		this.radians = radians;
		Random rand = new Random();
		float speed = rand.nextInt(10) == 2 ? 100 : 300;
		dx = MathUtils.cos(radians) * speed;
		dy = MathUtils.sin(radians) * speed;

		width = height = 2;
		
		lifeTimer = 0;
		lifeTime = speed > 200 ? speed / 100 : 5;
	}

	public boolean shouldRemove() { return remove; }

	public void update(float dt) {

		x += dx * dt;
		y += dy * dt;

	
		lifeTimer += dt;
		if(lifeTimer > lifeTime) {
			remove = true;
		}

	}

	public void draw(ShapeRenderer sr) {
		sr.setColor(1, 1, 1, 1);
		sr.begin(ShapeType.Circle);
		sr.circle(x - width / 2, y - height / 2, width / 2);
		sr.end();
	}

}


















