package br.com.tosin.sd.multicast.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

	public static void handshakeLog(String msg) {
		String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(System.currentTimeMillis()));

//		System.out.println(date + " Handshake: " + msg);
	}
	
	public static void deuRuim(String msg) {
		String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(System.currentTimeMillis()));

//		System.out.println(date + " Deu ruim: " + msg);
	}
	
	public static void game(String msg) {
		String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(System.currentTimeMillis()));

//		System.out.println(date + " Deu ruim: " + msg);
	}

	public static void master(String msg) {
		String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(System.currentTimeMillis()));

		System.out.println(date + " O mestre disse: " + msg);
	}
}
