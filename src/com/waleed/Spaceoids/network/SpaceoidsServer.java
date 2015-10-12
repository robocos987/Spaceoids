package com.waleed.Spaceoids.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.kryo.Kryo;
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


public class SpaceoidsServer extends Listener {

	static Server server;
	static final int port = 911;
	static Map<Integer, ConnectedPlayer> players = new HashMap<Integer, ConnectedPlayer>();

	static Scanner commandLine;

	//	static ArrayList<Asteroid> asteroids;

	Log log;

	public static void main(String[] args) throws IOException{
		commandLine = new Scanner(System.in);
		server = new Server();
		registerClasses(server.getKryo());

		server.bind(port, port + 1);
		server.start();
		System.out.println("The server is ready");

		/*
		 * Server communications with clients
		 */
		server.addListener(new Listener()
		{
			@Override
			public void connected(Connection c){
				ConnectedPlayer player = new ConnectedPlayer();
				float x = 20;
				float y = 20;
				player.c = c;
				player.id = c.getID();
				player.player = new Player(x, y, new ArrayList<Bullet>());

				PacketWelcome welcomePacket = new PacketWelcome();
				welcomePacket.id = player.id;
				server.sendToTCP(c.getID(), welcomePacket);


				PacketAddPlayer packet = new PacketAddPlayer();
				packet.id = c.getID();
				server.sendToAllExceptTCP(c.getID(), packet);

				for(ConnectedPlayer p : players.values()){
					PacketAddPlayer packet2 = new PacketAddPlayer();
					packet2.id = p.c.getID();
					c.sendUDP(packet2);
				}


				players.put(c.getID(), player);
				System.out.println("Player " + c.getID() + "(" + c.getRemoteAddressTCP().toString() + ")" + " has joined the game");

			}

			@Override
			public void received(Connection c, Object o){
				if(o instanceof PacketUpdateX){
					PacketUpdateX packet = (PacketUpdateX) o;
					players.get(c.getID()).x = packet.x;

					packet.id = c.getID();
					server.sendToAllExceptUDP(c.getID(), packet);

				}else if(o instanceof PacketUpdateY){
					PacketUpdateY packet = (PacketUpdateY) o;
					players.get(c.getID()).y = packet.y;

					packet.id = c.getID();
					server.sendToAllExceptUDP(c.getID(), packet);

				}else if(o instanceof PacketUpdateRotation)
				{
					PacketUpdateRotation packet = (PacketUpdateRotation) o;
					players.get(c.getID()).radians = packet.radians;
					players.get(c.getID()).rotationSpeed = packet.rotationSpeed;

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
				//				if(!o.getClass().getCanonicalName().contains("Position")) System.out.println("Sent and recieved a: " + o.getClass().getCanonicalName());
			}

			@Override
			public void disconnected(Connection c){
				players.remove(c.getID());
				PacketRemovePlayer packet = new PacketRemovePlayer();
				packet.id = c.getID();
				server.sendToAllExceptTCP(c.getID(), packet);
				System.out.println("Player " + c.getID() + " has left the game");
				c.close();

				String message = "Player " + c.getID() + " has left the game";
				PacketChatMessage packetMessage = new PacketChatMessage();
				packetMessage.message = message;
				server.sendToAllTCP(packetMessage);
			}
		});

		Log.setLogger(new Logger());
		Log.set(Log.LEVEL_TRACE);

		/*
		 * Command line
		 */
		while(true)
		{
			String command = commandLine.nextLine();
			if(command.startsWith("kick "))
			{
				kickPlayer(command);
			}else if(command.startsWith("tp "))
			{
				teleportPlayer(command);
			}else if(command.startsWith("say "))
			{
				String message = command.substring(4);
				if(message.isEmpty() || command.equals("say"))
				{
					System.err.println("Usage: say -message"); 
				}else
				{
					PacketChatMessage packet = new PacketChatMessage();
					packet.message = message;
					server.sendToAllTCP(packet);
				}
			}else if(command.equals("stop"))
			{
				System.out.println("Saving game data..");
				System.out.println("Shutting down server...");
				server.close();
				System.out.println("Stopped server sucessfully");
				server.stop();
				System.exit(0);
			}else
			{
				System.err.println("Invalid command!");
			}
		}
	}

	private static void teleportPlayer(String command) {

		for(ConnectedPlayer p: players.values())
		{
			int playerFrom = Integer.valueOf(command.substring(3, 4));
			int playerTo = Integer.valueOf(command.substring(5, 6));
			if(String.valueOf(playerFrom).isEmpty() || String.valueOf(playerTo).isEmpty() || command.equals("tp"))
			{
				System.err.println("Usage: tp -playerID -playerID");
			}else if(p.id == playerTo)
			{
				PacketUpdatePosition packet = new PacketUpdatePosition();
				packet.id = playerFrom;
				packet.x = p.x;
				packet.y = p.y;

				if(playerTo != playerFrom)
				{
					server.sendToAllTCP(packet);
					System.out.println("Teleported ID " + playerFrom + " to ID " + playerTo);
				}
			}


		}
			
	}

	private static void kickPlayer(String command) {
		
		int kickID = Integer.valueOf(command.substring(5, 6));
		
		if(players.values().size() <= 0)
		{
			System.err.println("Could not find player with ID: " + kickID);
			return;
		}
		for(ConnectedPlayer p: players.values())
		{
			int id = p.id;
			Connection c = p.c;
			String reason;

			if(command.length() > 6)
				reason = command.substring(7);
			else
				reason = "No reason :P";

			if(kickID == id)
			{
				if(reason.isEmpty()) reason = "No reason :P";

				players.remove(kickID);
				PacketRemovePlayer packet = new PacketRemovePlayer();
				packet.id = kickID;
				packet.reason = reason;
				server.sendToAllTCP(packet);
				System.out.println("Kicked player: " + kickID + " for " + reason);

				c.close();
			}else
			{
				System.err.println("Could not find player with ID: " + kickID);
			}
		}
	}


public static void registerClasses(Kryo kryo) {
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


}
