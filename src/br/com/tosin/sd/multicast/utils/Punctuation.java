package br.com.tosin.sd.multicast.utils;

import java.util.List;

import br.com.tosin.sd.multicast.models.Player;

public class Punctuation {

	
	public static int punctuationRight() {
		return 5;
	}
	
	public static int punctuationWrong() {
		return -1;
	}
	
	public static String buildPunctuation(List<Player> players) {
		String result = "";
		result = "PUNCTUATION;";
		
		for (Player player : players) {
			result += player.getId() + ";" + player.getPunctuation() + ";";
		}
		
		return result;
	}
}
