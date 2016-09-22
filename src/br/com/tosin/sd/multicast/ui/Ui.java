package br.com.tosin.sd.multicast.ui;

import java.io.*;
import java.util.Scanner;


public class Ui {

	private int WAIT = 9 * 1000;

	public void showSimpleMessage(String msg) {
		System.out.println(msg);
	}

	/**
	 * Pega a palavra digitada pelo usuario
	 * 
	 * @return Palavra
	 */
	public String getUiWord() {
		String result = "";

		System.out.println("Senao souber a palavra digite 0");
		System.out.print("Qual eh a palavra: \n");
		BufferedReader console = new BufferedReader( new InputStreamReader(System.in));
		try {
			long TIME = System.currentTimeMillis();
			long delay = 0;
			while (delay < WAIT ) {
				if (console.ready()) {
					result = console.readLine();
					break;
				}
				delay = System.currentTimeMillis() - TIME;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Pega a letra digitada pelo usuario
	 * 
	 * @return Letra
	 */
	public String getUiLetter() {
		String result = "";

		System.out.print("Digite uma letra: \n");
		BufferedReader console = new BufferedReader( new InputStreamReader(System.in));
		try {
			long TIME = System.currentTimeMillis();
			long delay = 0;
			while (delay < WAIT ) {
				if (console.ready()) {
					result = console.readLine();
					break;
				}
				delay = System.currentTimeMillis() - TIME;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
	 * 
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
			if (temp[i].equals("PUNCTUATION")) {
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

		while (i < temp.length) {
			result += temp[i] + "\t" + temp[i + 1] + "\n";
			i += 2;
		}

		result += "\n\n";

		System.out.println(result);
	}

	public void GameOver() {
		System.out.println("\n=======================================================");
		System.out.println("GAME OVER!");
		System.out.println("=======================================================\n");
	}

	public void finish(String id) {
		System.out.println("\n=======================================================");
		System.out.println("Acabou!");
		System.out.println("O jogador " + id + " ganhou!!");
		System.out.println("=======================================================\n");
	}

	/**
	 * Moostra o tempo expirou
	 * 
	 * @param id
	 */
	public void timeEnd(String id) {

	}
}
