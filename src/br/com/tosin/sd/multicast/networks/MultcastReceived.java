package br.com.tosin.sd.multicast.networks;

import java.io.*;
import java.net.*;

import utils.Constants;

public class MultcastReceived implements Runnable {


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
					System.out.println("Received: " + new String(messageIn.getData()));
//					System.out.println("B Host address: " +messageIn.);
					System.out.println("Host Address: " + messageIn.getAddress());
					

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
