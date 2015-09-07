package com.waleed.Spaceoids.network.packets;

import java.util.ArrayList;

import com.waleed.Spaceoids.entities.Bullet;

public class PacketUpdateBullet{
	
	public int id;
	public final int MAX_BULLETS = 4;
	public ArrayList<Bullet> bullet;

}
