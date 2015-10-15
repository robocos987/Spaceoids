package com.waleed.Spaceoids.network;

import java.util.HashMap;
import java.util.Map;

import com.esotericsoftware.kryonet.Listener;
import com.waleed.Spaceoids.entities.Player;
import com.waleed.Spaceoids.gamestates.MultiplayerState;
import com.waleed.Spaceoids.network.packets.MPPlayer;

public class SpaceoidsClient extends Listener {
	public static Map<Integer, MPPlayer> players = new HashMap<Integer, MPPlayer>();
	public Network network = new Network();

	public SpaceoidsClient(String host, int port, Player player) {
		network.connect("104.174.253.235", port, port + 1, player);
	}

	public boolean isConnected() {
		return network.client.isConnected();
	}
	
	public boolean isKicked()
	{
		return network.kicked && !isConnected();
	}
	
	public String getReason()
	{
		if(!isConnected())
		{
			return network.reason;
		}
		return "Connected!";
	}

}
