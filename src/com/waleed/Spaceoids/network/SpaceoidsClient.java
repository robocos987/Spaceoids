package com.waleed.Spaceoids.network;

import java.util.HashMap;
import java.util.Map;

import com.esotericsoftware.kryonet.Listener;
import com.waleed.Spaceoids.network.packets.MPPlayer;

public class SpaceoidsClient extends Listener {
	public static Map<Integer, MPPlayer> players = new HashMap<Integer, MPPlayer>();
	public Network network = new Network();

	public SpaceoidsClient(String host, int port) {
		network.connect("192.168.0.17", 911, 911 + 1);
	}

	public boolean isConnected() {
		return network.client.isConnected();
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