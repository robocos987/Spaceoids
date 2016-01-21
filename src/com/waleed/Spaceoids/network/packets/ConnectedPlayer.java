package com.waleed.Spaceoids.network.packets;

import java.awt.geom.Line2D.Float;
import java.util.ArrayList;

import com.esotericsoftware.kryonet.Connection;
import com.waleed.Spaceoids.entities.Bullet;
import com.waleed.Spaceoids.entities.Player;
import com.waleed.Spaceoids.entities.PlayerMP;
import com.waleed.Spaceoids.main.Spaceoids;


public class ConnectedPlayer {

	public float x = Spaceoids.WIDTH / 2, y = Spaceoids.HEIGHT / 2;
	public int id;
	public float dx;
	public float dy;
	public float acceleration;
	public float acclerationTimer;
	public boolean death;
	public boolean hit;
	public float hitTimer;
	public float hitTime;
	public Float[] netHitLines;
	public java.awt.geom.Point2D.Float[] netHitLinesVec;
	public float radians;
	public float rotationSpeed;
	public Player player;
	public ArrayList<Bullet> bullets;
	public Connection c;
	public boolean up;
	public long score;
	public int lives;

}
