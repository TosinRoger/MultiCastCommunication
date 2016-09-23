package br.com.tosin.sd.multicast.controller;

import java.util.*;

import br.com.tosin.sd.multicast.models.Player;

public class ManagesTheList {

	public static Player findPlayerById(List<Player> players, String id) {
		for (Player player : players) {
			if (player.getId().equals(id))
				return player;
		}
		return null;
	}

	public static boolean hasThisPlayer(List<Player> players, Player player) {
		if (players.indexOf(player) == -1)
			return true;
		else
			return false;
	}

	/**
	 * requisita o proximo jogador, caso no seja possivel retorna null
	 * 
	 * @return proximo jogador
	 */
	public static Player nextPlayer(List<Player> players, Player master, Player current) {
		if (players == null || players.size() <= 1)
			return null;
		else {
			int currentPosition = players.indexOf(current == null ? master : current);

			if (currentPosition + 1 < players.size()) {
				return players.get(currentPosition + 1);
			} else {
				if (players.size() > 1) {
					return players.get(1);
				}
			}
		}
		return null;
	}
	public static List<Player> setPunctuation(List<Player> players, Player player, int punctuation) {
		
		for (Player item : players) {
			if(item.getId().equals(player.getId())) {
				item.setPunctuation(punctuation);
			}
		}
		return players;
	}
}
