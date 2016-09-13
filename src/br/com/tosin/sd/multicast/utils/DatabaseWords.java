package br.com.tosin.sd.multicast.utils;

import java.util.*;

public class DatabaseWords {

	
	public String randonWord() {
		List<String> words = builWords();
		
		int position = new Random().nextInt(words.size());
		
		return words.get(position);
	}
	
	private List<String> builWords() {
		List<String> words = new ArrayList<>();
		
		words.add("camundongo");
		
		return words;
	}
}
