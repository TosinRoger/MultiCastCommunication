package br.com.tosin.sd.multicast.security;

import java.util.Random;

public class BuildKeyWith16Bytes {
	/**
	 * Criar uma string alfanumerica com tamanho 16, ou seja, 16 bytes
	 * @return
	 */
	public static String newKey() {
		 String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
	        StringBuilder salt = new StringBuilder();
	        Random rnd = new Random();
	        while (salt.length() < 16) {
	            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
	            salt.append(SALTCHARS.charAt(index));
	        }
	        String saltStr = salt.toString();
	        return saltStr;
	}
}
