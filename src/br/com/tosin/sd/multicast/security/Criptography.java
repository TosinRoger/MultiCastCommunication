package br.com.tosin.sd.multicast.security;

import java.security.*;

import javax.crypto.Cipher;


public class Criptography {

    private static final String ALGORITHM = "RSA";
    private KeyPair key;
	
	public void buildKey(){
		try {
			final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
			keyGen.initialize(1024);
	        key = keyGen.generateKeyPair();
	        
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
     * Criptografa o texto puro usando chave pública.
     */
    public static String criptografa(String texto, PublicKey chave) {
      byte[] cipherText = null;
      String result = "";
      
      try {
        final Cipher cipher = Cipher.getInstance(ALGORITHM);
        // Criptografa o texto puro usando a chave Púlica
        cipher.init(Cipher.ENCRYPT_MODE, chave);
        cipherText = cipher.doFinal(texto.getBytes());
        
        result = new String(cipherText, "ISO-8859-1");
        
      } catch (Exception e) {
        e.printStackTrace();
      }
      
      return result;
    }
    
    /**
     * Decriptografa o texto puro usando chave privada.
     */
    public static String decriptografa(String message, PrivateKey chave) {
      byte[] dectyptedText = null;
      
      try {

        byte[] texto = message.getBytes("ISO-8859-1");
          
        final Cipher cipher = Cipher.getInstance(ALGORITHM);
        // Decriptografa o texto puro usando a chave Privada
        cipher.init(Cipher.DECRYPT_MODE, chave);
        dectyptedText = cipher.doFinal(texto);
       
   
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      
      return new String(dectyptedText);
    }
	
	public PublicKey getPublicKey() {
		return key.getPublic();
	}
	
	public PrivateKey getPrivateKey() {
		return key.getPrivate();
	}
	
}
