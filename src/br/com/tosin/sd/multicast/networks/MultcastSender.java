package br.com.tosin.sd.multicast.networks;

import java.io.*;
import java.net.*;

import utils.Constants;

public class MultcastSender{
//	String message = "";

	public void send(String message) {
		// TODO Auto-generated method stub
		MulticastSocket s = null;
		try {
			InetAddress group = InetAddress.getByName(Constants.ADDRESS);
			s = new MulticastSocket(Constants.PORT);
			s.joinGroup(group);
			byte[] m = message.getBytes();
			DatagramPacket messageOut = new DatagramPacket(m, m.length, group, Constants.PORT);
			s.send(messageOut);

			s.leaveGroup(group);
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
