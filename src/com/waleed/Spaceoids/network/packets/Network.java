package com.waleed.Spaceoids.network.packets;

import java.io.IOException;
import java.util.ArrayList;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.waleed.Spaceoids.entities.Asteroid;
import com.waleed.Spaceoids.entities.Bullet;
import com.waleed.Spaceoids.entities.PlayerMP;
import com.waleed.Spaceoids.gamestates.MultiplayerState;
import com.waleed.Spaceoids.network.SpaceoidsClient;


public class Network extends Listener {

	public Client client;
	String ip;
	int port;
	public String reason;
	
	public void connect(String ip, int port, int udport)
	{
		this.ip = ip;
		this.port = port;
		client = new Client();
		client.getKryo().register(Asteroid.class);
		client.getKryo().register(ArrayList.class);
		client.getKryo().register(Bullet.class);
		client.getKryo().register(PacketAddPlayer.class);	
		client.getKryo().register(PacketAsteroids.class);
		client.getKryo().register(PacketRemovePlayer.class);
		client.getKryo().register(PacketUpdateAcceleration.class);
		client.getKryo().register(PacketUpdateBullet.class);
		client.getKryo().register(PacketUpdateDeath.class);
		client.getKryo().register(PacketUpdateFlames.class);
		client.getKryo().register(PacketUpdateHit.class);
		client.getKryo().register(PacketUpdatePosition.class);
		client.getKryo().register(PacketUpdateRotation.class);
		client.getKryo().register(PacketUpdateStats.class);
		client.addListener(this);

		client.start();
		try {
			client.connect(5000, ip, port, port + 1);
		} catch (IOException e) {
			reason = e.toString();
			e.printStackTrace();
		}
	}

	@Override
	public void received(Connection c, Object o)
	{
		if(o instanceof PacketAddPlayer)
		{
			PacketAddPlayer packet = (PacketAddPlayer) o;
			MPPlayer newPlayer = new MPPlayer();
			newPlayer.id = packet.id;
			PlayerMP playerMP = null;
			newPlayer.setPlayer(playerMP, SpaceoidsClient.players.values().size() > 0);	
			SpaceoidsClient.players.put(packet.id, newPlayer);

		}else if(o instanceof PacketRemovePlayer)
		{
			PacketRemovePlayer packet = (PacketRemovePlayer) o;
			SpaceoidsClient.players.remove(packet.id);

		}else if(o instanceof PacketUpdatePosition)
		{
			PacketUpdatePosition packet = (PacketUpdatePosition) o;
			SpaceoidsClient.players.get(packet.id).x = packet.x;
			SpaceoidsClient.players.get(packet.id).y = packet.y;

			SpaceoidsClient.players.get(packet.id).dx = packet.dx;
			SpaceoidsClient.players.get(packet.id).dy = packet.x;
		}else if(o instanceof PacketUpdateAcceleration)
		{
			PacketUpdateAcceleration packet = (PacketUpdateAcceleration) o;
			SpaceoidsClient.players.get(packet.id).acceleration = packet.accleration;
			SpaceoidsClient.players.get(packet.id).acceleration = packet.acclerationTimer;
		}else if(o instanceof PacketUpdateDeath)
		{
			PacketUpdateDeath packet = (PacketUpdateDeath) o;
			SpaceoidsClient.players.get(packet.id).death = packet.death;
		}else if(o instanceof PacketUpdateHit)
		{
			PacketUpdateHit packet = (PacketUpdateHit) o;
			SpaceoidsClient.players.get(packet.id).hit = packet.hit;
			SpaceoidsClient.players.get(packet.id).hitTime = packet.hitTime;
			SpaceoidsClient.players.get(packet.id).hitTimer = packet.hitTimer;
			SpaceoidsClient.players.get(packet.id).netHitLines = packet.netHitLines;
			SpaceoidsClient.players.get(packet.id).netHitLinesVec = packet.netHitLinesVec;
		}else if(o instanceof PacketUpdateRotation)
		{
			PacketUpdateRotation packet = (PacketUpdateRotation) o;
			SpaceoidsClient.players.get(packet.id).radians = packet.radians;
			SpaceoidsClient.players.get(packet.id).rotationSpeed = packet.rotationSpeed;
		}else if(o instanceof PacketUpdateFlames)
		{
			PacketUpdateFlames packet = (PacketUpdateFlames) o;
			SpaceoidsClient.players.get(packet.id).up = packet.up;
		}else if(o instanceof PacketUpdateBullet)
		{
			PacketUpdateBullet packet = (PacketUpdateBullet) o;
			SpaceoidsClient.players.get(packet.id).bullets = packet.bullet;

		}else if(o instanceof PacketUpdateStats)
		{
			PacketUpdateStats packet = (PacketUpdateStats) o;
			SpaceoidsClient.players.get(packet.id).score = packet.score;
			SpaceoidsClient.players.get(packet.id).lives = packet.extraLives;

		}else if(o instanceof PacketAsteroids)
		{
			PacketAsteroids packet = (PacketAsteroids) o;
			MultiplayerState.INSTANCE.asteroids = packet.asteroids;
			for(int i = 0; i < packet.asteroids.size(); i++)
			{
				Asteroid asteroid = packet.asteroids.get(i);
				MultiplayerState.INSTANCE.asteroids.add(asteroid);
			}
		}
		System.out.println("Recieved: " + o.getClass().getCanonicalName());

	}
}
