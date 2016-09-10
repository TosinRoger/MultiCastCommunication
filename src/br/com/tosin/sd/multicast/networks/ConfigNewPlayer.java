package br.com.tosin.sd.multicast.networks;

import br.com.tosin.sd.multicast.controller.Controller;
import br.com.tosin.sd.multicast.models.MasterPlayer;

/**
 * Configura novo jogador com o {@link MasterPlayer}, 
 * troca lista de todos os jogadores, 
 * troca a chave publica, etc
 * @author tosin
 *
 */
public class ConfigNewPlayer {

	/**
	 * Envia mensagem testes
	 */
	public void execute() {

		String msg = Controller.getMaster().getId() + ":" +  "EU SOU O MESTRE!!!!";
		new MulticastSender().send(msg);
	}
}
