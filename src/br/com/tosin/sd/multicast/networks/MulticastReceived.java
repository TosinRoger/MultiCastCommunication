package br.com.tosin.sd.multicast.networks;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

import br.com.tosin.sd.multicast.interfaces.Response;
import br.com.tosin.sd.multicast.utils.Constants;

public class MulticastReceived implements Runnable {

	Response response;
	
	/**
	 * Runnable que roda por tempo indefido e recebe todas as mensagem do multicast e 
	 * envia para o programa atraves do lister {@link Response}
	 * @param response Listener que notificara sobre uma mensagem recebida.
	 */
	public MulticastReceived(Response response) {
		super();
		this.response = response;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub

		MulticastSocket s = null;
		try {
			InetAddress group = InetAddress.getByName(Constants.ADDRESS);
			s = new MulticastSocket(Constants.PORT);
			s.joinGroup(group);
			

			while (true) {
				try {
					byte[] buffer = new byte[1000];
					DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
					if (s != null)
						s.receive(messageIn);

					/*
					 *  NAO VERIFICAR SE A MENSAGEM FOI ENVIADA POR ELE MESMO AQUI, 
					 *  DELEGAR ISSO PARA O PLAYER OU MASTER.
					 */
					
					String received = new String(messageIn.getData());

					response.response(received);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (s != null)
				s.close();
		}

	}

}
