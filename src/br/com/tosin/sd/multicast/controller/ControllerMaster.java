package br.com.tosin.sd.multicast.controller;

import br.com.tosin.sd.multicast.interfaces.Response;
import br.com.tosin.sd.multicast.models.MasterPlayer;
import br.com.tosin.sd.multicast.networks.ConfigNewPlayer;
import br.com.tosin.sd.multicast.networks.MulticastReceived;
import br.com.tosin.sd.multicast.utils.Constants;


/**
 * Controller do {@link MasterPlayer}, eh responsavel por gerenciar todas as jogadas, 
 * comunicacoes, receber novos jogadores, sincronizar lista de jogadores, 
 * verificar jogadores inativos. ate o fim da partida.
 * @author tosin
 *
 */
public class ControllerMaster {
	private MasterPlayer master;

	public ControllerMaster(MasterPlayer master) {
		super();
		this.master = master;
	}
	
	public void execute() {
		messageReceived();
		
	}
	
	private void messageReceived() {
		new Thread(new MulticastReceived(new Response() {
			
			@Override
			public void response(String messageReceived) {
				// TODO Auto-generated method stub
				// mestre deve tratar INITIAL_HANDSHAKE
				if (messageReceived.contains(Constants.INITIAL_HANDSHAKE)) {
					new ConfigNewPlayer().execute();
				}
				else {
					
				}
			}
		})).start();
	}
}
