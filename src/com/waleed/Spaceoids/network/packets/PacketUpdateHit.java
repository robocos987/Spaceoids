package com.waleed.Spaceoids.network.packets;

import java.awt.geom.Line2D.Float;

public class PacketUpdateHit {
	
	public int id;
	public boolean hit;
	public float hitTimer;
	public float hitTime;
	public Float[] netHitLines;
	public java.awt.geom.Point2D.Float[] netHitLinesVec;
}
