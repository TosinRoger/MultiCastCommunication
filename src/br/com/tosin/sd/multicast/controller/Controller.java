package br.com.tosin.sd.multicast.controller;

import java.util.*;

import br.com.tosin.sd.multicast.interfaces.Response;
import br.com.tosin.sd.multicast.models.Player;
import br.com.tosin.sd.multicast.networks.*;
import br.com.tosin.sd.multicast.security.Criptography;
import br.com.tosin.sd.multicast.ui.Ui;
import br.com.tosin.sd.multicast.utils.*;

public class Controller {

	private static final int DELAY = 5000;
	private static final int MINIMUM_PLAYERS = 2;
	private static final int MAXIMUM_NOTIFICATIONS = 3;

	private boolean respended = false;
	
	private List<Player> players;

	private Player player;
	private boolean ImMaster = true;
	private int numAttemptsNotification = 0; // numero da tentatica de notifincacao

	private String hiddenWord;
	private List<String> chosenLetter = new ArrayList<>();

	private int positionOfTime = 1;
	
	private boolean nowIsLetter = true; // esta entrando 2x na mensagem, var para entrar so uma

	public void config() {
		/**
		 * cria a instancia Jogado, a instancia master deve ser cria no
		 * handshake inicial, caso seja o caso e usar o setMasterPlayer
		 */
		player = new Player();
		
		// adiciona a chave public e privada no jogador
		Criptography cripty = new Criptography();
		cripty.buildKey();
		player.setPrivateKey(cripty.getPrivateKey());
		player.setPublicKey(cripty.getPublicKey());

		System.out.println("Esse sou eu: " + player.getId() + "\n");

		listeningComunnication();

		askAnotherPlayer();
	}

	/**
	 * Logica do jogo aguarda ter pelo menos 2 jogadores para iniciar Lembrando
	 * que a primeira posicao da lista eh no master
	 */
	private void game() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				boolean running = true;
				System.out.println("vai executar Thread o game: " + running);
				while (running) {
					Log.game("num jogadores: " + getPlayers().size());
					try {
						// master + 1 jogador
						if (getPlayers().size() > MINIMUM_PLAYERS) {
							Log.master("Vai comecar essa treta!!!!!!");
							hiddenWord = new DatabaseWords().randonWord();
							// primeiro
							Log.master("LIST: " + new Parser().parseListToString(getPlayers()));
							sendData(Constants.LIST, new Parser().parseListToString(getPlayers()));
							Thread.sleep(3*1000);
							sendData(Constants.PLAYER_SELECT_LETTER, getPlayers().get(positionOfTime).getId());
							running = false;
							Thread.interrupted();
						}
					
						Thread.sleep(DELAY);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	private void listeningComunnication() {
		new Thread(new MulticastReceived(new Response() {

			@Override
			public void response(String messageReceived) {
				// TODO Auto-generated method stub
				identifiesReceiced(messageReceived);
			}
		})).start();
		;
	}

	/**
	 * Verifica se tem alguem no canal
	 */
	private void askAnotherPlayer() {

		Log.handshakeLog("Send Initical handshake");
		sendData(Constants.INITIAL_HANDSHAKE, getPlayer().getPublicKey().toString());

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.handshakeLog("wait result");
				/*
				 * Cria um delay para aguardar a resposta de outros jogadores Se
				 * ninguem response envia para os outros jogadores que eh o
				 * mestre
				 */
				try {
					Thread.sleep(DELAY);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.handshakeLog("Wait finish: " + ImMaster);
System.out.println("EU SOU MESTRE:" + ImMaster);
				if (ImMaster) {
					getPlayers().add(0, player);
					sendData(Constants.I_AM_A_MASTER, "");
				}
				Thread.interrupted();
			}
		}).start();

	}

	private void identifiesReceiced(String message) {
		
		message = message.trim();
		String[] array = message.split(":");
		String senderPlayerId = array[0];
		String receivedIdentify = array[1];
		String receivedMessage = array.length == 2 ? "" : array[2];
		
		if(receivedIdentify.equals(Constants.YOUR_CREDENTIAL) && receivedMessage.equals(getPlayer().getId())) {
			sendData(Constants.INITIAL_HANDSHAKE, "");
		}
		
		// se receber a propria mensagem de que eh um mestre, inicia o jogo
		if (senderPlayerId.startsWith(getPlayer().getId())) {
			if(receivedIdentify.equals(Constants.I_AM_A_MASTER)) {
				ImMaster = true;
				game();
			}
		}
		// impede de processar a propria mensagem
		else {

			Log.handshakeLog("Listening comunication: " + message);

			// toda mensagem chega com o seguinte formato "id:message"
			/**
			 * Aqui deve ser descriptografado
			 */
			

			switch (receivedIdentify) {

			/*
			 * Alguem perguntou se tem alguem no canal Returnar que esta no
			 * canal
			 */
			case Constants.INITIAL_HANDSHAKE:
				Log.game("Quer entrar");
				Log.handshakeLog("Received initial handshake: " + ImMaster + ", " + message);
				/*
				 * se for o mestre, envia que o mestre e envia a chave public
				 * adicion o usuario a lista e envia a lista para os jogadores
				 */
				if (ImMaster == true) {
					Log.handshakeLog(message);
					sendData(Constants.I_AM_A_MASTER, "");
					// TODO por tosin [12 de set de 2016] enviar chave publica

					getPlayers().add(new Player(senderPlayerId));

					sendData(Constants.LIST, new Parser().parseListToString(getPlayers()));
				}
				// verifica se esta na lista de players senao adiciona no fim da lista
				else {
					if(!idAlreadyRegister(senderPlayerId)) {
						getPlayers().add(new Player(senderPlayerId));
					}
				}
				break;
			/*
			 * Alguem dizendo que eh o mestre
			 */
			case Constants.I_AM_A_MASTER:
				ImMaster = false;
				Log.handshakeLog("im not a master: " + ImMaster);
				
				break;

			/*
			 * Master notifica que tem que dizer uma letra
			 * a mensagem contem o id do jogador que deve "dizer" uma letra
			 */
			case Constants.PLAYER_SELECT_LETTER:
				if (receivedMessage.equals(getPlayer().getId()) && nowIsLetter) {
					nowIsLetter = !nowIsLetter;
					// jogador espera usuario digitar uma letra
					String lettrer = new Ui().getUiLetter();
					sendData(Constants.MASTER_LETTER_SELECTED_BY_THE_PLAYER, lettrer);
				}
				break;

			/*
			 * Master recebe a letra e compara como a palavra oculta
			 */
			case Constants.MASTER_LETTER_SELECTED_BY_THE_PLAYER:
				if (!ImMaster) 
					break;
				// desabilita time
				respended = true;
				numAttemptsNotification = 0;
				
				String letter = receivedMessage;
				
				Log.master("Recebeu a letra: " + receivedMessage);

				// verfica se a letra ja foi chutada
				if (new VerifyLetterWord().letterAlreadKick(letter, chosenLetter)) {
					Log.master("Que burro a letra ja foi usada!!!!");
					getPlayers().get(positionOfTime).setPunctuation(new Punctuation().punctuationWrong());
					sendData(Constants.PLAYER_HIT_THE_LETTER, Constants.LETTER_ALREAD_CHOSEN);
				} else {
					chosenLetter.add(letter);
					if (new VerifyLetterWord().hitTheLetter(hiddenWord, letter)) {
						Log.master("Acertou a letra");
						getPlayers().get(positionOfTime).setPunctuation(new Punctuation().punctuationRight());
						sendData(Constants.PLAYER_HIT_THE_LETTER, Constants.LETTER_RIGHT);
					} else {
						Log.master("ERRO!!! nao tem essa letra");
						getPlayers().get(positionOfTime).setPunctuation(new Punctuation().punctuationWrong());
						sendData(Constants.PLAYER_HIT_THE_LETTER, Constants.LETTER_WRONG);
					}
				}
				
				try {
					Thread.sleep(DELAY);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				// verifica se acertou a palavra
				if (new VerifyLetterWord().discoveryTheWord(hiddenWord, chosenLetter)) {
					Log.master("ACABOU!!!! ACABOU!!!! EH TETRA!!!!");
					sendData(Constants.CONGRATULATIONS, "");
					
					String temp = new VerifyLetterWord().status(hiddenWord, chosenLetter);
					temp += new Punctuation().buildPunctuation(getPlayers());
					
					sendData(Constants.PLAYER_PUNCTUATION, temp);
					
					/*
					 * Notifica os usuario para o novo mestre assumir
					 */
					positionOfTime = getPlayers().indexOf(getPlayer());
					nextPlayer();
					ImMaster = false;
					sendData(Constants.GAME_OVER, getPlayers().get(positionOfTime).getId());
					
				}
				// se errou envia letras acertadas, letras erradas e pontuacao
				else {
					// envia pontucao e pergunta a palavra

					Log.master("acertou a letre, mas me diga, qual eh a palavra?");
					String temp = new VerifyLetterWord().status(hiddenWord, chosenLetter);
					temp += new Punctuation().buildPunctuation(getPlayers());
					
					
					System.out.println("\n" + temp + "\n");
					
					sendData(Constants.PLAYER_PUNCTUATION, temp);
					
					try {
						Thread.sleep(DELAY);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					sendData(Constants.PLAYER_SELECT_WORD,getPlayers().get(positionOfTime).getId());
				}
				break;
				
			/*
			 * Master notifica que tem que dizer uma palavra
			 */
			case Constants.PLAYER_SELECT_WORD: 
				if (receivedMessage.equals(getPlayer().getId())) {
					nowIsLetter = !nowIsLetter;
					// jogador espera usuario digitar uma letra
					String word = new Ui().getUiWord();
					if(word.length() == 1)
						sendData(Constants.MASTER_PASSING_TIME_BY_PLAYER, "");
					else 
						sendData(Constants.MASTER_WORD_SELECTED_BY_THE_PLAYER, word);
				}
				break;
			/*
			 * Master notifica ue tem que dizer uma palavra ou passar a vez
			 * Master recebe que o jogador chuto a palavra
			 */
			case Constants.MASTER_PASSING_TIME_BY_PLAYER:
				if (!ImMaster) 
					break;
				Log.master("Ele passou a vez");
				
				nextPlayer();
				Log.master("Proximo jogador: " + getPlayers().get(positionOfTime));
				
				// mostra pontuacao 

				String temp = new VerifyLetterWord().status(hiddenWord, chosenLetter);
				temp += new Punctuation().buildPunctuation(getPlayers());
				
				sendData(Constants.PLAYER_PUNCTUATION, temp);
				
				// requisita novo jogador
				sendData(Constants.PLAYER_SELECT_LETTER, getPlayers().get(positionOfTime).getId());
				
				
				break;
				
			case Constants.MASTER_WORD_SELECTED_BY_THE_PLAYER:
				if (!ImMaster) 
					break;
				// desabilita time
				respended = true;
				numAttemptsNotification = 0;
				Log.master("Ele chuto uma palavra: " + receivedMessage);
				
				// verifica se a palavra foi descoberta
				if(hiddenWord.equals(receivedMessage)) {
					Log.master("ACABOU!!!! ACABOU!!!! EH TETRA!!!!");
					sendData(Constants.CONGRATULATIONS, "");
					
					String temp2 = new VerifyLetterWord().status(hiddenWord, chosenLetter);
					temp2 += new Punctuation().buildPunctuation(getPlayers());
					
					sendData(Constants.PLAYER_PUNCTUATION, temp2);
					
					/*
					 * Notifica os usuario para o novo mestre assumir
					 */
					positionOfTime = getPlayers().indexOf(getPlayer());
					nextPlayer();
					ImMaster = false;
					sendData(Constants.GAME_OVER, getPlayers().get(positionOfTime).getId());
				}
				else {
					String temp2 = new VerifyLetterWord().status(hiddenWord, chosenLetter);
					temp2 += new Punctuation().buildPunctuation(getPlayers());
					
					sendData(Constants.PLAYER_PUNCTUATION, temp2);
					
					try {
						Thread.sleep(DELAY);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					nextPlayer();
					Log.master("ERRO!! Proximo!!!!");
	
					sendData(Constants.PLAYER_SELECT_LETTER, getPlayers().get(positionOfTime).getId());
				}
				break;

			/*
			 * Master avisa que errou a letra
			 */
			case Constants.PLAYER_HIT_THE_LETTER:

				if (receivedMessage.equals(Constants.LETTER_ALREAD_CHOSEN)) {
					new Ui().setUiLetterAlreadChoisen();
				} else if (receivedMessage.equals(Constants.LETTER_WRONG)) {
					new Ui().setUiLetterWrong();
				} else if (receivedMessage.equals(Constants.LETTER_RIGHT)) {
					new Ui().setUiLetterRight();
				}

				break;
				
			case Constants.PLAYER_PUNCTUATION:
				
				new Ui().showPunctuation(receivedMessage);

				break;

			/*
			 * Master notifica que o jogo acabou, e envia id do proximo jogador que deve
			 * assumir o papel de mestre
			 */
			case Constants.GAME_OVER:
				// receivedMessege contem o id do jogador que sera o mestre
				new Ui().finish(receivedMessage);
				System.out.println("game_over, new master will be: " + receivedMessage);
				nowIsLetter = true;
				if(getPlayer().getId().equals(receivedMessage)) {
					ImMaster = true;
					getPlayers().clear();
					getPlayers().add(getPlayer());
					chosenLetter.clear();
					System.out.println("list size: " + getPlayers().size());
					
					// envia mensagem para o antigo mestre pedindo sua credenciais

					sendData(Constants.YOUR_CREDENTIAL, senderPlayerId);
					
					game();
				}
				// se nao for o novo mestre envia um handshake para o novo mestre
				else {
					try {
						Thread.sleep(DELAY);
						sendData(Constants.INITIAL_HANDSHAKE, "");

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;

			/*
			 * Mastre envia a lista de jogares
			 */
			case Constants.LIST:
				getPlayers().clear();
				getPlayers().addAll(new Parser().parseStringToList(receivedMessage));
				
				Log.game("LIST: " + new Parser().parseListToString(getPlayers()));
				
				break;

			/*
			 * Acertou a palavra
			 */
			case Constants.CONGRATULATIONS:
				new Ui().GameOver();
				break;

			default:
				break;
			}
		}
	}

	/**
	 * Envia a mensagem para o canal multicast, A mensagem sempre eh
	 * identificada com id do jogador que esta enviado a mensagem mais a
	 * mensagem
	 * 
	 * @param data
	 *            Mensagem que esta sendo enviada
	 */
	private void sendData(String identify, String data) {
		
		String message = getPlayer().getId() + ":" + identify + ":" + data;
		new MulticastSender().send(message);
		
		/*
		 * Se o jogador estiver enviando a letra ou a palavra
		 * habilita o time para resposta
		 * 
		 * Senao simplemente envia a mensagem
		 */
//		if(identify.equals(Constants.PLAYER_SELECT_LETTER) || identify.equals(Constants.PLAYER_SELECT_WORD)){
//			
//			try {
//				respended = false;
//				// tenta envia algumas vezes senao der certo envia para um no jogador
//				while (numAttemptsNotification < MAXIMUM_NOTIFICATIONS) {
//					String message = getPlayer().getId() + ":" + identify + ":" + data;
//					new MulticastSender().send(message);
//	
//					// aguarda 1 minuto para tentar enviar novamente
//					Thread.sleep(1*60*1000);
//					
//					if(respended) {
//						numAttemptsNotification = 0;
//						Thread.interrupted();
//					}
//					else {
//						numAttemptsNotification++;
//					}
//					Log.deuRuim("Nao recebeu a resposta: " + (numAttemptsNotification + 1));
//				}
//				
//				if(!respended && numAttemptsNotification >= MAXIMUM_NOTIFICATIONS) {
//					new Ui().timeEnd(getPlayers().get(positionOfTime).getId());
//					
//					nextPlayer();
//					// requisita letra do proximo jogador
//					sendData(Constants.PLAYER_SELECT_LETTER, getPlayers().get(positionOfTime).getId());
//				}
//				
//				
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		else {
//			String message = getPlayer().getId() + ":" + identify + ":" + data;
//			new MulticastSender().send(message);
//		}
	}

	
	private void nextPlayer() {
		// passa para proximo jogador
		positionOfTime++;
		
		if(positionOfTime == getPlayers().size()) {
			positionOfTime = 1;
		}
	}
	
	private boolean idAlreadyRegister(String id) {
		for (Player item : getPlayers()) {
			if (item.getId().equals(id))
				return true;
		}
		return false;
	}

	// ============================================================================

	public Player getPlayer() {
		return player;
	}

//	public MasterPlayer getMaster() {
//		return master;
//	}
//
//	public void setMaster(MasterPlayer master) {
//		this.master = master;
//	}

//	public List<String> getIdPlayers() {
//		if (idPlayers == null)
//			idPlayers = new ArrayList<>();
//		return idPlayers;
//	}

	public List<Player> getPlayers() {
		if(players == null) 
			players = new ArrayList<>();
		return players;
	}

}
