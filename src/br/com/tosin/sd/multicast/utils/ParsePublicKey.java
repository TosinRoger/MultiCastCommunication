package br.com.tosin.sd.multicast.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PublicKey;

public class ParsePublicKey {

	/**
	 * Converte um public key em um string
	 * 
	 * @param publicKey
	 * @return
	 */
	public static String convertPublicKey(PublicKey publicKey) {
		String result = "";
		try {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(buffer);
			oos.writeObject(publicKey);
			result = new String(buffer.toByteArray(), "ISO-8859-1");
			oos.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Recupera a publickey que esta armazenda em um string
	 * 
	 * @param message
	 * @return
	 */
	public static PublicKey recoveryPublicKey(String message) {

		ObjectInputStream inputStream = null;
		PublicKey publicKey = null;
		
		try {

			byte[] array = message.getBytes("ISO-8859-1");
			ByteArrayInputStream bis = new ByteArrayInputStream(array);

			inputStream = new ObjectInputStream(bis);

			publicKey = (PublicKey) inputStream.readObject();

		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return publicKey;
	}
}
