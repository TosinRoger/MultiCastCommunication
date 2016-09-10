package br.com.tosin.sd.multicast.ui;

import java.util.Scanner;

public class Ui {
	
	public String getUiMessage() {
		String result = "";
		
		System.out.print("Digite um mensagem para ser enviada: \n");
		@SuppressWarnings("resource")
		Scanner ler = new Scanner(System.in);
		
		result = ler.nextLine();
//		ler.close();
		System.out.println("\n");
		
		return result;
	}

}
