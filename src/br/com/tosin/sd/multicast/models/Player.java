package br.com.tosin.sd.multicast.models;

public class Player {
	
	private long id;
	
	public Player() {
		// TODO Auto-generated constructor stub
		id = System.currentTimeMillis();
	}
	
	public String getId() {
		return String.valueOf(id);
	}
}
