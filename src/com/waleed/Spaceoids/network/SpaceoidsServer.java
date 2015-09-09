package com.waleed.Spaceoids.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.badlogic.gdx.graphics.Color;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.esotericsoftware.minlog.Log.Logger;
import com.waleed.Spaceoids.entities.Asteroid;
import com.waleed.Spaceoids.entities.Bullet;
import com.waleed.Spaceoids.entities.Player;
import com.waleed.Spaceoids.network.packets.ConnectedPlayer;
import com.waleed.Spaceoids.network.packets.PacketAddPlayer;
import com.waleed.Spaceoids.network.packets.PacketAsteroids;
import com.waleed.Spaceoids.network.packets.PacketRemovePlayer;
import com.waleed.Spaceoids.network.packets.PacketUpdateAcceleration;
import com.waleed.Spaceoids.network.packets.PacketUpdateBullet;
import com.waleed.Spaceoids.network.packets.PacketUpdateDeath;
import com.waleed.Spaceoids.network.packets.PacketUpdateFlames;
import com.waleed.Spaceoids.network.packets.PacketUpdateHit;
import com.waleed.Spaceoids.network.packets.PacketUpdatePosition;
import com.waleed.Spaceoids.network.packets.PacketUpdateRotation;
import com.waleed.Spaceoids.network.packets.PacketUpdateStats;


public class SpaceoidsServer extends Listener {

	static Server server;
	static final int port = 911;
	static Map<Integer, ConnectedPlayer> players = new HashMap<Integer, ConnectedPlayer>();
	
	
	
	static Scanner scan;
	
	Log log;

	public static void main(String[] args) throws IOException{
		scan = new Scanner(System.in);
		server = new Server();
		server.getKryo().register(ArrayList.class);
		server.getKryo().register(Bullet.class);
		server.getKryo().register(float[].class);
		server.getKryo().register(Color.class);
		server.getKryo().register(PacketAddPlayer.class);	
		server.getKryo().register(PacketAsteroids.class);
		server.getKryo().register(PacketRemovePlayer.class);
		server.getKryo().register(PacketUpdateAcceleration.class);
		server.getKryo().register(PacketUpdateBullet.class);
		server.getKryo().register(PacketUpdateDeath.class);
		server.getKryo().register(PacketUpdateFlames.class);
		server.getKryo().register(PacketUpdateHit.class);
		server.getKryo().register(PacketUpdatePosition.class);
		server.getKryo().register(PacketUpdateRotation.class);
		server.getKryo().register(PacketUpdateStats.class);
		for(int i = 0; i < server.getKryo().getNextRegistrationId(); i++)
		{
			System.out.println("Registering: " + server.getKryo().getRegistration(i).toString());
		}
		server.bind(port, port + 1);
		server.start();
		server.addListener(new SpaceoidsServer());
//		System.out.println("The server is ready");
		
	    Log.setLogger(new Logger());
	    Log.set(Log.LEVEL_TRACE);
	}
	
	@Override
	public void connected(Connection c){
		ConnectedPlayer player = new ConnectedPlayer();
		float x = 20;
		float y = 20;
		player.c = c;
		player.id = c.getID();
		player.player = new Player(x, y, new ArrayList<Bullet>());
//		asteroids = new ArrayList<Asteroid>();

		
	

		PacketAddPlayer packet = new PacketAddPlayer();
		packet.id = c.getID();
		server.sendToAllExceptTCP(c.getID(), packet);

		for(ConnectedPlayer p : players.values()){
			PacketAddPlayer packet2 = new PacketAddPlayer();
			packet2.id = p.c.getID();
			c.sendTCP(packet2);
			
			
		}
		
		

		players.put(c.getID(), player);
		System.out.println("Connection received from: " + c.getID());

	}

	@Override
	public void received(Connection c, Object o){
		if(o instanceof PacketUpdatePosition){
			PacketUpdatePosition packet = (PacketUpdatePosition) o;
			players.get(c.getID()).x = packet.x;
			players.get(c.getID()).y = packet.y;
			players.get(c.getID()).dx = packet.dx;
			players.get(c.getID()).dy = packet.dy;


			packet.id = c.getID();
			server.sendToAllExceptUDP(c.getID(), packet);

		}else if(o instanceof PacketUpdateAcceleration)
		{
			PacketUpdateAcceleration packet = (PacketUpdateAcceleration) o;
			players.get(c.getID()).acceleration = packet.accleration;
			players.get(c.getID()).accelerationTimer = packet.acclerationTimer;

			packet.id = c.getID();
			server.sendToAllExceptUDP(c.getID(), packet);

		}else if(o instanceof PacketUpdateRotation)
		{
			PacketUpdateRotation packet = (PacketUpdateRotation) o;
			players.get(c.getID()).radians = packet.radians;
			players.get(c.getID()).rotationSpeed = packet.rotationSpeed;

			packet.id = c.getID();
			server.sendToAllExceptUDP(c.getID(), packet);

		}else if(o instanceof PacketUpdateAcceleration)
		{
			PacketUpdateAcceleration packet = (PacketUpdateAcceleration) o;
			players.get(c.getID()).acceleration = packet.accleration;

			packet.id = c.getID();
			server.sendToAllExceptUDP(c.getID(), packet);

		}else if(o instanceof PacketUpdateDeath)
		{
			PacketUpdateDeath packet = (PacketUpdateDeath) o;
			players.get(c.getID()).death = packet.death;


			packet.id = c.getID();
			server.sendToAllExceptUDP(c.getID(), packet);
		}else if(o instanceof PacketUpdateHit)
		{
			PacketUpdateHit packet = (PacketUpdateHit) o;
			players.get(c.getID()).hit = packet.hit;
			players.get(c.getID()).hitTime = packet.hitTime;
			players.get(c.getID()).hitTimer = packet.hitTimer;
			players.get(c.getID()).netHitLines = packet.netHitLines;
			players.get(c.getID()).netHitLinesVec = packet.netHitLinesVec;

			packet.id = c.getID();
			server.sendToAllExceptUDP(c.getID(), packet);
		}else if(o instanceof PacketUpdateFlames)
		{
			PacketUpdateFlames packet = (PacketUpdateFlames) o;
			players.get(c.getID()).up = packet.up;
			packet.id = c.getID();

			server.sendToAllExceptUDP(c.getID(), packet);
		}else if(o instanceof PacketUpdateBullet)
		{
			PacketUpdateBullet packet = (PacketUpdateBullet) o;
			players.get(c.getID()).bullets = packet.bullet;
			packet.id = c.getID();
			
			server.sendToAllExceptUDP(c.getID(), packet);
		}else if(o instanceof PacketUpdateStats)
		{
			PacketUpdateStats packet = (PacketUpdateStats) o;
			players.get(c.getID()).score = packet.score;
			players.get(c.getID()).lives = packet.extraLives;
			packet.id = c.getID();
			server.sendToAllExceptTCP(c.getID(), packet);
		}
		System.out.println("Sent and recieved a: " + o.getClass().getCanonicalName());
	}

	@Override
	public void disconnected(Connection c){
		players.remove(c.getID());
		PacketRemovePlayer packet = new PacketRemovePlayer();
		packet.id = c.getID();
		server.sendToAllExceptTCP(c.getID(), packet);
		System.out.println("Connection dropped.");
		c.close();
	}
}
