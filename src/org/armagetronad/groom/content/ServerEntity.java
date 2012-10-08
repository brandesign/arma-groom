package org.armagetronad.groom.content;

import java.util.List;

public class ServerEntity {

	public final String name;
	public final String ip;
	public final int port;
	public final String version;
	public final String version_min;
	public final String version_max;
	public final String description;
	public final int numplayers;
	public final int maxplayers;
	public final String url;
	public final List<PlayerEntity> players;
	
	
	public ServerEntity(String name, String ip, int port, String version, String version_min, String version_max, String description, int numplayers, int maxplayers, String url, List<PlayerEntity> players) {
		this.name = name;
		this.ip = ip;
		this.port = port;
		this.version = version;
		this.version_min = version_min;
		this.version_max = version_max;
		this.description = description;
		this.numplayers = numplayers;
		this.maxplayers = maxplayers;
		this.url = url;
		this.players = players;
	}
}
