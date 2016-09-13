package br.com.tosin.sd.multicast.ui;

import java.util.Arrays;
import java.util.Scanner;

import br.com.tosin.sd.multicast.models.Player;

public class Ui {
	
	/**
	 * Pega a palavra digitada pelo usuario
	 * @return Palavra
	 */
	public String getUiWord() {
		String result = "";
		
		System.out.println("Senao souber a palavra digite 0");
		System.out.print("Qual eh a palavra: \n");
		@SuppressWarnings("resource")
		Scanner ler = new Scanner(System.in);
		
		result = ler.nextLine();
		System.out.println("\n");

		return result;
	}

	/**
	 * Pega a letra digitada pelo usuario
	 * @return Letra
	 */
	public String getUiLetter() {
		String result = "";
		
		do{
			//TODO por tosin [12 de set de 2016] tem que verficicar se eh uma letra, nao um simbolo
		
			System.out.print("Digite uma letra: \n");
			@SuppressWarnings("resource")
			Scanner ler = new Scanner(System.in);
			
			result = ler.nextLine();
			System.out.println("\n");
			
			if(result.length() != 1) {
				System.out.println("Nao foi digita uma unica letra!!");
			}
		}while(result.length() != 1);

		return result;
	}
	
	/**
	 * Mostra mensagem de letra ja escolhida
	 */
	public void setUiLetterAlreadChoisen() {
		System.out.println("Essa letra ja foi escolhida\n");
	}
	
	/**
	 * Mostra mensagem de letra errada
	 */
	public void setUiLetterWrong() {
		System.out.println("Essa palavra nao contem essa letra\n");
	}
	
	/**
	 * Mostra mensagem de letra acertada
	 */
	public void setUiLetterRight() {
		System.out.println("Parabéns voce acertou essa letra\n");
	}
	
	/**
	 * Mostra a pontuacao de todos os jogadores
	 * @param punctuation
	 */
	public void showPunctuation(String punctuation) {
		String result = "";
		
		String[] temp = punctuation.split(";");
		
		result += "\n";
		result += "=================================================\n";
		result += "Palavra: " + temp[0] + "\n";
		result += "=================================================\n";
		result += "Letras ja utilizadas:\n";
		
		int i = 1;
		
		while (i < temp.length) {
			if(temp[i].equals("PUNCTUATION")) {
				i++;
				break;
			}
			result += temp[i] + " ";
			i++;
		}
		
		result += "\n=================================================\n";
		
		result += "\n";
		result += "JOGADOR\t\t" + "PONTOS";
		result += "\n";
		
		while(i < temp.length) {
			result += temp[i] + "\t" + temp[i+1] + "\n";
			i += 2;
		}
		
		for (String player : punctuation.split(";")) {
			result += player + "\t" + player + "\n";
		}
		result += "\n";
		
				
		
		
		
		System.out.println(result);
	}
	
	public void finish(String id) {
		System.out.println("Acabou!");
		System.out.println("O jogador " + id + " ganhou!!");
		
	}
	
	/**
	 * Moostra o tempo expirou
	 * @param id
	 */
	public void timeEnd(String id) {
		
	}
}
