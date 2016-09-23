package br.com.tosin.sd.multicast;

import br.com.tosin.sd.multicast.controller.Controller;
import br.com.tosin.sd.multicast.security.Criptography;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println("Iniciado Main");
		
		new Controller().config();
		
//		String msg = "a";
//		
//		Criptography cripty = new Criptography();
//		cripty.buildKey();
//		
//		Criptography cripty2 = new Criptography();
//		cripty2.buildKey();
//		
//		String crypto = Criptography.criptografa(msg, cripty.getPrivateKey());
//		
//		String result = Criptography.decriptografa(crypto, cripty.getPublicKey());
//		String result2 = Criptography.decriptografa(crypto, cripty2.getPublicKey());
//		
//		System.out.println(result);
//		System.out.println(result2);
	}

}
