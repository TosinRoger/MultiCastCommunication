package br.com.tosin.sd.multicast.models;

import br.com.tosin.sd.multicast.security.BuildKeyWith16Bytes;

public class Player {
	
	private long id;
	
	private String privateKey;
	private int punctuation;
	
	public Player() {
		// TODO Auto-generated constructor stub
		id = System.currentTimeMillis();
	}
	
	public Player(String id) {
		this.id = Long.valueOf(id);
	}
	
	public String getId() {
		return String.valueOf(id);
	}

	public String getPrivateKey() {
		if (privateKey == null || privateKey.isEmpty()) {
			privateKey = BuildKeyWith16Bytes.newKey();
		}
		return privateKey;
	}

	public int getPunctuation() {
		return punctuation;
	}

	public void setPunctuation(int punctuation) {
		this.punctuation += punctuation;
	}
	
	
}
