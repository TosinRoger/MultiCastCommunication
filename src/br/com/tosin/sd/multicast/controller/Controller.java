package br.com.tosin.sd.multicast.controller;

import br.com.tosin.sd.multicast.models.MasterPlayer;
import br.com.tosin.sd.multicast.models.Player;
import br.com.tosin.sd.multicast.networks.MultcastReceived;
import br.com.tosin.sd.multicast.networks.MultcastSender;
import br.com.tosin.sd.multicast.ui.Ui;

public class Controller {
	
	private static Player player;
	private static MasterPlayer master;
	
	public void config() {
		player = new Player();
		master = new MasterPlayer();
		
		System.out.println("Player: " + player.getId() + "\n");
		
		execute();
	}

	public void execute() {

		// fica escutando para receber a mensagem dos outro processos
		new Thread(new MultcastReceived()).start();

		
		// espera mensagem do usuario e envia 
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				MultcastSender example = new MultcastSender();
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
	
	
}
