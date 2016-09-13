package br.com.tosin.sd.multicast.models;

import java.security.PrivateKey;
import java.security.PublicKey;

public class Player {
	
	private long id;
	
	private PrivateKey privateKey;
	private PublicKey publicKey;
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

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(PrivateKey privateKey2) {
		this.privateKey = privateKey2;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(PublicKey publicKey2) {
		this.publicKey = publicKey2;
	}

	public int getPunctuation() {
		return punctuation;
	}

	public void setPunctuation(int punctuation) {
		this.punctuation += punctuation;
	}
	
	
}
