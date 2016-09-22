package br.com.tosin.sd.multicast.security;

import java.io.UnsupportedEncodingException;
import java.security.*;

import javax.crypto.*;


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
     * Criptografa o texto puro usando chave publica.
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
      String result = "";
      
      if(chave == null)
    	  return result;
      
      try {

        byte[] texto = message.getBytes("ISO-8859-1");
          
        final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        // Decriptografa o texto puro usando a chave Privada
        cipher.init(Cipher.DECRYPT_MODE, chave);
        dectyptedText = cipher.doFinal(texto);
       
        result = new String(dectyptedText);
      } catch (InvalidKeyException e) {
    	 e.printStackTrace();
      } catch (BadPaddingException e) {
		// TODO: handle exception
//    	  e.printStackTrace();
      } catch (IllegalBlockSizeException e) {
		// TODO: handle exception
    	  e.printStackTrace();
      } catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (NoSuchAlgorithmException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (NoSuchPaddingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
      
      return result;
    }
	
	public PublicKey getPublicKey() {
		return key.getPublic();
	}
	
	public PrivateKey getPrivateKey() {
		return key.getPrivate();
	}
	
}
