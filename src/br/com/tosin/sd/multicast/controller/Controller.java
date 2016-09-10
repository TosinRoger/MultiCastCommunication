package br.com.tosin.sd.multicast.controller;

import br.com.tosin.sd.multicast.networks.MultcastReceived;
import br.com.tosin.sd.multicast.networks.MultcastSender;
import br.com.tosin.sd.multicast.ui.Ui;

public class Controller {

	public void execute() {

		// fica escutando para receber a mensagem dos outro processos
		new Thread(new MultcastReceived()).start();

		
		// fica escrevendo na tela
		
		
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				MultcastSender example = new MultcastSender();
				while (true) {
					String temp = new Ui().getUiMessage();
					example.send(temp);
				}
			}
		}).run();
	}
}
