package com.waleed.Spaceoids.network;

import java.io.IOException;
import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.waleed.Spaceoids.entities.Bullet;
import com.waleed.Spaceoids.entities.Player;
import com.waleed.Spaceoids.entities.PlayerMP;
import com.waleed.Spaceoids.gamestates.MultiplayerState;
import com.waleed.Spaceoids.network.packets.MPPlayer;
import com.waleed.Spaceoids.network.packets.PacketAddPlayer;
import com.waleed.Spaceoids.network.packets.PacketAsteroids;
import com.waleed.Spaceoids.network.packets.PacketChatMessage;
import com.waleed.Spaceoids.network.packets.PacketRemovePlayer;
import com.waleed.Spaceoids.network.packets.PacketUpdateAcceleration;
import com.waleed.Spaceoids.network.packets.PacketUpdateBullet;
import com.waleed.Spaceoids.network.packets.PacketUpdateDX;
import com.waleed.Spaceoids.network.packets.PacketUpdateDY;
import com.waleed.Spaceoids.network.packets.PacketUpdateDeath;
import com.waleed.Spaceoids.network.packets.PacketUpdateFlames;
import com.waleed.Spaceoids.network.packets.PacketUpdateHit;
import com.waleed.Spaceoids.network.packets.PacketUpdatePosition;
import com.waleed.Spaceoids.network.packets.PacketUpdateRotation;
import com.waleed.Spaceoids.network.packets.PacketUpdateStats;
import com.waleed.Spaceoids.network.packets.PacketUpdateX;
import com.waleed.Spaceoids.network.packets.PacketUpdateY;
import com.waleed.Spaceoids.network.packets.PacketWelcome;


public class Network extends Listener {

	public Client client;
	String ip;
	int port;
	public String reason;
	Player player;
	public boolean kicked;


	public void connect(String ip, int port, int udport, Player player)
	{
		this.ip = ip;
		this.port = port;
		this.player = player;
		client = new Client();
		registerClasses(client.getKryo());
		client.addListener(this);

		client.start();
		try {
			client.connect(5000, ip, port, port + 1);
		} catch (IOException e) {
			reason = e.toString();
			e.printStackTrace();
		}
	}

	private void registerClasses(Kryo kryo) {
		kryo.register(ArrayList.class);
		kryo.register(Bullet.class);
		kryo.register(Color.class);
		kryo.register(float[].class);
		kryo.register(PacketAddPlayer.class);	
		kryo.register(PacketAsteroids.class);
		kryo.register(PacketChatMessage.class);
		kryo.register(PacketUpdateAcceleration.class);
		kryo.register(PacketUpdateBullet.class);
		kryo.register(PacketUpdateDeath.class);
		kryo.register(PacketUpdateFlames.class);
		kryo.register(PacketUpdateHit.class);
		kryo.register(PacketUpdatePosition.class);
		kryo.register(PacketUpdateRotation.class);
		kryo.register(PacketUpdateStats.class);
		kryo.register(PacketRemovePlayer.class);
		kryo.register(PacketWelcome.class);
		kryo.register(PacketUpdateX.class);
		kryo.register(PacketUpdateY.class);
		for(int i = 0; i < kryo.getNextRegistrationId(); i++)
		{
			System.out.println("Registering: " + kryo.getRegistration(i).toString());
		}
	}

	@Override
	public void received(Connection c, Object o)
	{
		if(o instanceof PacketWelcome)
		{
			PacketWelcome packet = (PacketWelcome) o;
			this.player.id = packet.id;
			System.out.println(this.player.id);
		}
		else if(o instanceof PacketAddPlayer)
		{
			PacketAddPlayer packet = (PacketAddPlayer) o;
			MPPlayer newPlayer = new MPPlayer();
			newPlayer.id = packet.id;
			PlayerMP playerMP = null;
			newPlayer.setPlayer(playerMP, SpaceoidsClient.players.values().size() > 0);	
			SpaceoidsClient.players.put(packet.id, newPlayer);

			PacketUpdateStats packetStats = new PacketUpdateStats();
			packetStats.extraLives = this.player.getLives();
			packetStats.score = this.player.getScore();

			this.client.sendUDP(packetStats);

		}else if(o instanceof PacketRemovePlayer)
		{
			PacketRemovePlayer packet = (PacketRemovePlayer) o;
			if(packet.id != this.player.id)
			{
				SpaceoidsClient.players.remove(packet.id);
			}else
			{
				reason = packet.reason;
				this.kicked = true;
			}

		}else if(o instanceof PacketUpdatePosition)
		{
			PacketUpdatePosition packet = (PacketUpdatePosition) o;
<<<<<<< HEAD
			if(packet.id == this.player.id)
			{
				float x = packet.x - 20;
				float y = packet.y;
				
				this.player.setPosition(x, y);
			}

=======
			SpaceoidsClient.players.get(packet.id).netX = packet.x;
			SpaceoidsClient.players.get(packet.id).netY = packet.y;

			SpaceoidsClient.players.get(packet.id).dx = packet.dx;
			SpaceoidsClient.players.get(packet.id).dy = packet.dy;
		}else if(o instanceof PacketUpdateAcceleration)
		{
			PacketUpdateAcceleration packet = (PacketUpdateAcceleration) o;
			SpaceoidsClient.players.get(packet.id).acceleration = packet.accleration;
			SpaceoidsClient.players.get(packet.id).acceleration = packet.acclerationTimer;
>>>>>>> ed81f30c3fc7c0e5855b0d658362f9d8e9105660
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
		}else if(o instanceof PacketChatMessage)
		{
			PacketChatMessage packet = (PacketChatMessage) o;
			MultiplayerState.INSTANCE.message = packet.message;
			MultiplayerState.INSTANCE.countDown = 7.0F;
		}else if(o instanceof PacketUpdateX)
		{
			PacketUpdateX packet = (PacketUpdateX) o;
			SpaceoidsClient.players.get(packet.id).setX(packet.x);
		}else if(o instanceof PacketUpdateY)
		{
			PacketUpdateY packet = (PacketUpdateY) o;
			SpaceoidsClient.players.get(packet.id).setY(packet.y);		
		}

		if(!(o.getClass().getCanonicalName().contains("UpdatePosition") || o.getClass().getCanonicalName().contains("UpdateAcceleration")
				|| o.getClass().getCanonicalName().contains("UpdateHit")))
			System.out.println("Recieved: " + o.getClass().getCanonicalName());

	}

	public String getReason()
	{
		return reason;
	}
}
