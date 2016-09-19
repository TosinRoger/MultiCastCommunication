package br.com.tosin.sd.multicast.utils;

import java.util.*;

public class VerifyLetterWord {


	/**
	 * Verficiar todas a letras que ja foram chutadas como a palavra oculta
	 * @param hiddenWord palavra oculta
	 * @param chosenLetter lista de letras chutadas
	 * @return String[] com as letras acertadas e * com as letras erradas
	 */
	public static String[] discoveredLetter(String hiddenWord, List<String> chosenLetter) {
		String[] discovered = new String[hiddenWord.length()];
		
		for (int i = 0; i < discovered.length; i++) {
			discovered[i] = "*";
		}

		for (int i = 0; i < hiddenWord.length(); i++) {
			for (String chosen : chosenLetter) {
				if (hiddenWord.charAt(i) == chosen.charAt(0)) {
					discovered[i] = String.valueOf(hiddenWord.charAt(i));
				}
			}
		}
		
		return discovered;
	}
	
	/**
	 * Verifica se a letra ja foi chutada
	 * @param hiddenWord
	 * @param chosenLetter
	 * @return
	 */
	public static boolean letterAlreadKick(String hiddenWord, List<String> chosenLetter) {
		for (int i = 0; i < hiddenWord.length(); i++) {
			for (String chosen : chosenLetter) {
				if (hiddenWord.charAt(i) == chosen.charAt(0)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Verfica se descobriu a letra
	 * @param hiddenWord
	 * @param letter
	 * @return
	 */
	public static boolean hitTheLetter(String hiddenWord, String letter) {
		
		for (int i = 0; i < hiddenWord.length(); i++) {
			if(hiddenWord.charAt(i) == letter.charAt(0))
				return true;
		}
		return false;
	}
	
	public static boolean discoveryTheWord(String hiddenWord, List<String> chosenLetter) {
		String[] result = discoveredLetter(hiddenWord, chosenLetter);
		
		for (int i = 0; i < result.length; i++) {
			if(result[i] == "*")
				return false;
		}
		return true;
	}
	
	public static String status(String hidem, List<String> chosen) {
		String result = "";
		String jj = Arrays.toString(discoveredLetter(hidem, chosen));
		result += jj + ";";
		
		for (String item : chosen) {
			result += item + ";";
		}
		
		return result;
	}
	
}
