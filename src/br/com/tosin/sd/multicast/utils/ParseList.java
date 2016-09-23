package br.com.tosin.sd.multicast.utils;

import java.util.*;

import br.com.tosin.sd.multicast.models.Player;

public class ParseList {

	public static String parseListToString(List<Player> players) {
		String stringList = "";
		
		for (Player player : players) {
			stringList += player.getId() + " - " + ParsePublicKey.convertPublicKey(player.getPublicKey());
		}
		
		return stringList;
	}
	
	public static List<Player> parseStringToList (String array) {
		List<Player> players = new ArrayList<>();
		
		String[] info = array.split(" - ");
		
		if(info.length % 2 != 0)
			return null;
		
		for (int i = 0; i < info.length; i++) {
			Player player = new Player(info[i]);
			player.setPublicKey(ParsePublicKey.recoveryPublicKey(info[++i]));
			players.add(player);
		}
		
		return players;
	}
}
