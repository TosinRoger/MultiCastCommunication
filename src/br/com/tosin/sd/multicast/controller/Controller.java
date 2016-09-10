package br.com.tosin.sd.multicast.controller;

import br.com.tosin.sd.multicast.interfaces.Response;
import br.com.tosin.sd.multicast.models.MasterPlayer;
import br.com.tosin.sd.multicast.models.Player;
import br.com.tosin.sd.multicast.networks.HasSomeoneOnGrup;
import br.com.tosin.sd.multicast.networks.MulticastReceived;
import br.com.tosin.sd.multicast.networks.MulticastSender;
import br.com.tosin.sd.multicast.ui.Ui;
import br.com.tosin.sd.multicast.utils.Constants;
import br.com.tosin.sd.multicast.utils.Log;

public class Controller {
	
	private static Player player;
	private static MasterPlayer master;
	
	public void config() {
		/**
		 *  cria a instancia Jogado, 
		 *  a instancia master deve ser cria no handshake inicial, caso seja o caso e usar o setMasterPlayer
		 */
		player = new Player();
		
		System.out.println("Player: " + player.getId() + "\n");
		
//		execute();
		fetchSomeone();
	}

	private void fetchSomeone() {
		new HasSomeoneOnGrup(new Response() {
			
			@Override
			public void response(String messageReceived) {
				// TODO Auto-generated method stub
				if (messageReceived.equals(Constants.I_AM_A_MASTER)) {
					runAsMaster();
				}
				else if (messageReceived.equals(Constants.I_AM_NOT_A_MASTER)) {
					runAsPlayer();
				}
				else {
					Log.deuRuim("Handshake inicial nao funfou");
				}
			}
		}).execute();
	}
	
	/**
	 * Roda o controle do jogador
	 */
	private void runAsPlayer() {
		master = null;
		new ControllerPlayer(player);
	}
	
	/**
	 * Roda o controller do master
	 */
	private void runAsMaster() {
		master = new MasterPlayer();
		new ControllerMaster(master);
	}
	
	
	/**
	 * Funtion teste send and received message
	 */
	public void execute() {

		// fica escutando para receber a mensagem dos outro processos
		new Thread(new MulticastReceived(new Response() {
			
			@Override
			public void response(String message) {
				// TODO Auto-generated method stub
				
				if(message.startsWith(String.valueOf(getPlayer().getId()))) {
					System.out.println("Recebi minha mensagem");
					System.out.println("");
				}
				else {
					System.out.println(Controller.getPlayer().getId() + ", received: \n\t" + message);
					System.out.println("");
				}
			}
		})).start();

		
		// espera mensagem do usuario e envia 
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				MulticastSender example = new MulticastSender();
				while (true) {
					String temp = new Ui().getUiMessage();
					if(!temp.isEmpty()) {
						temp = String.valueOf(player.getId()) + " : " + temp;
						example.send(temp);
					}
				}
			}
		}).run();
	}

	public static Player getPlayer() {
		return player;
	}

	public static MasterPlayer getMaster() {
		return master;
	}

	public static void setMaster(MasterPlayer master) {
		Controller.master = master;
	}
	
	
}
