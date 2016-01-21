package com.waleed.Spaceoids.network;

import java.util.HashMap;
import java.util.Map;

import com.esotericsoftware.kryonet.Listener;
import com.waleed.Spaceoids.entities.Player;
import com.waleed.Spaceoids.entities.PlayerMP;

public class SpaceoidsClient extends Listener {
	public static Map<Integer, PlayerMP> players = new HashMap<Integer, PlayerMP>();
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
