package org.armagetronad.groom.content;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ServersFeed {

	private Date date;
	private List<ServerEntity> servers;
	
	public ServersFeed(Date date,List<ServerEntity> servers) {
		this.setDate(date);
		this.servers =servers;
	}

	public List<ServerEntity> getServers() {
		if(servers == null) {
			servers = new ArrayList<ServerEntity>();
		}
		return servers;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
