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
import com.waleed.Spaceoids.main.Spaceoids;
import com.waleed.Spaceoids.network.packets.PacketAddPlayer;
import com.waleed.Spaceoids.network.packets.PacketAsteroids;
import com.waleed.Spaceoids.network.packets.PacketChatMessage;
import com.waleed.Spaceoids.network.packets.PacketRemovePlayer;
import com.waleed.Spaceoids.network.packets.PacketUpdateAcceleration;
import com.waleed.Spaceoids.network.packets.PacketUpdateBullet;
import com.waleed.Spaceoids.network.packets.PacketUpdateDeath;
import com.waleed.Spaceoids.network.packets.PacketUpdateFlames;
import com.waleed.Spaceoids.network.packets.PacketUpdateHit;
import com.waleed.Spaceoids.network.packets.PacketUpdatePosition;
import com.waleed.Spaceoids.network.packets.PacketUpdateRotation;
import com.waleed.Spaceoids.network.packets.PacketUpdateStats;
import com.waleed.Spaceoids.network.packets.PacketWelcome;


public class Network extends Listener {

	private Client client;
	private String ip;
	private int port = 911;
	private String reason;
	private Player player;
	private boolean kicked;


	public void connect(String ip, int port, int udport, Player player)
	{
		this.ip = ip;
		this.port = port;
		this.player = player;
		setClient(new Client());
		register(getClient().getKryo());
		getClient().addListener(this);

		getClient().start();
		try {
			getClient().connect(5000, ip, port, port + 1);
		} catch (IOException e) {
			reason = e.toString();
			e.printStackTrace();
		}
	}

	public static void register(Kryo kryo) {
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
		kryo.register(PacketUpdatePosition.class);
		
		for(int i = 0; i < kryo.getNextRegistrationId(); i++)
			System.out.println("Registering: " + kryo.getRegistration(i).toString());
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
			PlayerMP newPlayer = new PlayerMP(packet.id, new ArrayList<Bullet>());
			SpaceoidsClient.players.put(packet.id, newPlayer);

			PacketUpdateStats packetStats = new PacketUpdateStats();
			packetStats.extraLives = this.player.getLives();
			packetStats.score = this.player.getScore();

			this.getClient().sendTCP(packetStats);

		}else if(o instanceof PacketRemovePlayer)
		{
			PacketRemovePlayer packet = (PacketRemovePlayer) o;
			if(packet.id != this.player.id)
			{
				reason = packet.reason;
				this.setKicked(true);
			}else
				SpaceoidsClient.players.remove(packet.id);

		}else if(o instanceof PacketUpdatePosition)
		{
			PacketUpdatePosition packet = (PacketUpdatePosition) o;
			if(packet.id == this.player.id)
			{
				float x = packet.x - 20;
				float y = packet.y;

				this.player.setPosition(x, y);
			}else
			{
				PlayerMP mpPlayer = (PlayerMP) SpaceoidsClient.players.get(packet.id);
				mpPlayer.ppx = mpPlayer.npx;
				mpPlayer.ppdx = mpPlayer.npdx;
				mpPlayer.ppy = mpPlayer.npy;
				mpPlayer.ppdy = mpPlayer.npdy;
				mpPlayer.ppr = mpPlayer.npr;
				mpPlayer.ppdr = mpPlayer.npdr;
				mpPlayer.cpd = 0;
				mpPlayer.npx = packet.x;
				mpPlayer.npdx = packet.dx;
				mpPlayer.npy = packet.y;
				mpPlayer.npdy = packet.dy;
				mpPlayer.npr = packet.radians;
				mpPlayer.npdr = packet.rotationSpeed;
			}
			
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
			SpaceoidsClient.players.get(packet.id).hitLines = packet.netHitLines;
			SpaceoidsClient.players.get(packet.id).hitLinesVector = packet.netHitLinesVec;
		}else if(o instanceof PacketUpdateFlames)
		{
			PacketUpdateFlames packet = (PacketUpdateFlames) o;
			SpaceoidsClient.players.get(packet.id).up = packet.up;
		}else if(o instanceof PacketUpdateBullet)
		{
			PacketUpdateBullet packet = (PacketUpdateBullet) o;
			SpaceoidsClient.players.get(packet.id).bullets = packet.bullet;
			SpaceoidsClient.players.get(packet.id).newX = packet.bullet.get(0).x;
			SpaceoidsClient.players.get(packet.id).newY = packet.bullet.get(0).y;
			
		}else if(o instanceof PacketUpdateStats)
		{
			PacketUpdateStats packet = (PacketUpdateStats) o;
			SpaceoidsClient.players.get(packet.id).score = packet.score;
			SpaceoidsClient.players.get(packet.id).extraLives = packet.extraLives;

		}else if(o instanceof PacketAsteroids)
		{
			PacketAsteroids packet = (PacketAsteroids) o;
//			MultiplayerState.INSTANCE.asteroids = packet.asteroids;
		}else if(o instanceof PacketChatMessage)
		{
			PacketChatMessage packet = (PacketChatMessage) o;
//			MultiplayerState.INSTANCE.message = packet.message;
//			MultiplayerState.countDown = 7.0F;
		}else if(o instanceof PacketUpdatePosition)
		{
			PacketUpdatePosition packet = (PacketUpdatePosition) o;
			SpaceoidsClient.players.get(packet.id).oldX = SpaceoidsClient.players.get(packet.id).x;
			SpaceoidsClient.players.get(packet.id).oldY = SpaceoidsClient.players.get(packet.id).y;
			SpaceoidsClient.players.get(packet.id).newX = packet.x;
			SpaceoidsClient.players.get(packet.id).newY = packet.y;
			SpaceoidsClient.players.get(packet.id).dx = packet.dx;
			SpaceoidsClient.players.get(packet.id).dy = packet.dy;
			

		}else if(o instanceof PacketUpdateAcceleration)
		{
			PacketUpdateAcceleration packet = (PacketUpdateAcceleration) o;
			SpaceoidsClient.players.get(packet.id).acceleration = packet.accleration;
			SpaceoidsClient.players.get(packet.id).acceleratingTimer = packet.acclerationTimer;

		}

		if(!(o.getClass().getCanonicalName().contains("UpdatePosition")
				|| o.getClass().getCanonicalName().contains("UpdateHit")))
			System.out.println("Recieved: " + o.getClass().getCanonicalName());

	}

	public String getReason()
	{
		return reason;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public boolean isKicked() {
		return kicked;
	}

	public void setKicked(boolean kicked) {
		this.kicked = kicked;
	}
}
