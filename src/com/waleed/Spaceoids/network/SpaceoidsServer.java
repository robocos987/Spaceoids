package com.waleed.Spaceoids.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.esotericsoftware.minlog.Log.Logger;
import com.waleed.Spaceoids.entities.Bullet;
import com.waleed.Spaceoids.entities.Player;
import com.waleed.Spaceoids.network.packets.ConnectedPlayer;
import com.waleed.Spaceoids.network.packets.PacketAddPlayer;
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
		Network.register(server.getKryo());

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
				if(o instanceof PacketUpdatePosition){
					PacketUpdatePosition packet = (PacketUpdatePosition) o;
					players.get(c.getID()).y = packet.y;
					players.get(c.getID()).x = packet.x;
					players.get(c.getID()).dx = packet.dx;
					players.get(c.getID()).dy = packet.dy;
					
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
				}else if(o instanceof PacketUpdateAcceleration)
				{
					PacketUpdateAcceleration packet = (PacketUpdateAcceleration) o;
					players.get(c.getID()).acceleration = packet.accleration;
					players.get(c.getID()).acclerationTimer = packet.acclerationTimer;
					packet.id = c.getID();

					server.sendToAllExceptUDP(c.getID(), packet);
				}
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
				try
				{
					int kickID = 0;
					if(command.length() < 6 && !command.substring(5, 6).isEmpty())
					{
						System.err.println("Usage: kick -playerID <reason>");
						return;
					}else
						kickID = Integer.valueOf(command.substring(5, 6));
					
					for(ConnectedPlayer p: players.values())
					{
						if(kickID > 0 || kickID > players.values().size())
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
						}else
						{
							System.err.println("Usage: kick -playerID <reason>");
							return;
						}
					}
				}catch(ConcurrentModificationException e)
				{

				}
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



}
