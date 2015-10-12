package com.waleed.Spaceoids.network.packets;

import java.awt.geom.Line2D.Float;
import java.util.ArrayList;

import com.waleed.Spaceoids.entities.Bullet;
import com.waleed.Spaceoids.entities.PlayerMP;
import com.waleed.Spaceoids.main.Spaceoids;


public class MPPlayer {

	public float x = Spaceoids.WIDTH / 2, y = Spaceoids.HEIGHT / 2;
	public int id;
	public float dx;
	public float dy;
	public float acceleration;
	public float accelerationTimer;
	public boolean death;
	public boolean hit;
	public float hitTimer;
	public float hitTime;
	public Float[] netHitLines;
	public java.awt.geom.Point2D.Float[] netHitLinesVec;
	public float radians;
	public float rotationSpeed;
	public PlayerMP player;
	public boolean up;
	public ArrayList<Bullet> bullets;
	public long score;
	public int lives;
	public float newX, newY;
	
		
	public void setPlayer(PlayerMP player, boolean firstPlayer) {
		float x = 20;
		float y = 20;
		bullets = new ArrayList<Bullet>();
		player = new PlayerMP(id, x, y, bullets);

		this.player = player;
	}
	
	public PlayerMP getPlayer()
	{
		player.x = this.x;
		player.y = this.y;
		player.acceleratingTimer = this.accelerationTimer;
		player.dead = this.death;
		player.rotationSpeed = this.rotationSpeed;
		player.radians = this.radians;
		player.hit = this.hit;
		player.hitTimer = this.hitTimer;
		player.hitTime = this.hitTime;
		player.up = this.up;
		player.id = this.id;
		player.bullets = this.bullets;
		player.score = score;
		player.extraLives = lives;
		return this.player;
	}

	public void dispose() {
		this.player.dispose();
	}

	public void setX(float newX) {
//		this.x = player.lerp(x, newX, player.dt);
		this.x = newX;
	}
	
	public void setY(float newY)
	{
//		this.y = player.lerp(y, newY, player.dt);
		this.y = newY;
	}

}
