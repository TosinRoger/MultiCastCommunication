package br.com.tosin.sd.multicast.utils;

import java.util.*;

import br.com.tosin.sd.multicast.models.Player;

public class ParserListPlayer {

	/**
	 * Transforma a lista em string para se enviada para outros usuarios
	 * @return
	 */
	public String parseListToString(List<Player> idPlayer) {
		String list = "";
		
		for(Player item : idPlayer)
			list += item.getId() + ";";
		
		list = list.substring(0, list.length());
		
		return list;
	}
	
	/**
	 * Recupera os valores da lista recebida pelo master
	 * @param list
	 */
	public List<Player> parseStringToList(String list) {
		List<Player> idPlayer = new ArrayList<>();
		String[] array = list.split(";");
		
		idPlayer.clear();
		
		for(String item : array) {
			idPlayer.add(new Player(item));
		}
		
		return idPlayer;
	}

	
}
