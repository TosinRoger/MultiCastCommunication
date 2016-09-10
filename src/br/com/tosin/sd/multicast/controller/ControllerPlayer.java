package br.com.tosin.sd.multicast.controller;

import br.com.tosin.sd.multicast.interfaces.Response;
import br.com.tosin.sd.multicast.models.Player;
import br.com.tosin.sd.multicast.networks.MulticastReceived;

/**
 * Controller do {@link Player}, eh responsavel por gerenciar todas as jogadas, 
 * comunicacoes, intercoes com a tela do jogador. ate o fim da partida.
 * @author tosin
 *
 */
public class ControllerPlayer {
	
	private Player player;
	
	public ControllerPlayer(Player player) {
		super();
		this.player = player;
	}

	public void execute() {
		messageReceived();
		
	}
	
	private void messageReceived() {
		new Thread(new MulticastReceived(new Response() {
			
			@Override
			public void response(String messageReceived) {
				// TODO Auto-generated method stub
				/*
				 * Jogador deve ignorar mensagem INITIAL_HANDSHAKE
				 */
			}
		})).start();
	}
}
