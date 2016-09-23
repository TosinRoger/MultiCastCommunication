package br.com.tosin.sd.multicast.controller;

import java.io.*;
import java.security.PublicKey;
import java.util.*;

import br.com.tosin.sd.multicast.interfaces.Response;
import br.com.tosin.sd.multicast.models.Player;
import br.com.tosin.sd.multicast.networks.*;
import br.com.tosin.sd.multicast.security.Criptography;
import br.com.tosin.sd.multicast.ui.Ui;
import br.com.tosin.sd.multicast.utils.*;

public class Controller {

	// Usado por ambos

	private Player player;
	private boolean ImMaster = true;
	private Ui ui;

	// Usado pelo master

	private static final int WAIT_ANSWER = 10 * 1000;
	private static final int MINIMUM_PLAYERS = 2;
	private Player currentPlayer;
	private List<Player> players;
	private String hiddenWord;
	private boolean gameStarted = false;
	Timer timer = new Timer();

	// Usado pelo jogador

	private PublicKey publicKeyMaster;

	// ==================================================

	private static final int MAXIMUM_NOTIFICATIONS = 3;


	private int numAttemptsNotification = 0; // numero da tentatica de
												// notifincacao

	private List<String> chosenLetter = new ArrayList<>();

	private boolean nowIsLetter = true; // esta entrando 2x na mensagem, var
										// para entrar so uma

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

		getPlayers().add(player);

		ui = new Ui();
		ui.showSimpleMessage("Esse eh meu id: " + player.getId());
		ui.showSimpleMessage("Aguardando o minimo de jogadores");

		listeningComunnication();

		askAnotherPlayer();
	}

	private void listeningComunnication() {
		new Thread(new MulticastReceived(new Response() {

			@Override
			public void response(String messageReceived) {
				// TODO Auto-generated method stub
				// identifiesReceiced(messageReceived);
				processesMessage(messageReceived);
			}
		})).start();
	}

	/**
	 * Verifica se tem alguem no canal
	 */
	private void askAnotherPlayer() {

		String publicKey = ParsePublicKey.convertPublicKey(player.getPublicKey());
		sendData(Constants.INITIAL_HANDSHAKE, publicKey, "nada");
	}

	/**
	 * Logica do jogo aguarda ter pelo menos 2 jogadores para iniciar Lembrando
	 * que a primeira posicao da lista eh no master
	 */
	private void startTheGame() {
		ui.showSimpleMessage("Vai comecar o jogo!!!!");
		hiddenWord = new DatabaseWords().randonWord();
		currentPlayer = ManagesTheList.nextPlayer(getPlayers(), player, null);
		
		String encode = Criptography.criptografa(Constants.EMPTY, currentPlayer.getPublicKey());
		System.out.println("Primeiro jogador sera: " + currentPlayer.getId());
		sendData(Constants.PLAYER_SELECT_LETTER, encode, currentPlayer.getId());
	}

	private void processesMessage(String message) {
		message = message.trim();
		String[] array = message.split(" - ");
		String senderPlayerId = array.length >= 1 ? array[0] : "";
		String receivedIdentifier = array.length >= 2 ? array[1] : "";
		String receivedMessage = array.length >= 3 ? array[2] : "";
		String whoShouldReceive = array.length == 4 ? array[3] : "";
				
		switch (receivedIdentifier) {

		/*
		 * Alguem pergunto se tem alguem no canal. A mensagem recebida contem o
		 * publicKey de quem enviou a mensagem
		 */
		case Constants.INITIAL_HANDSHAKE:

			if (ImMaster && !senderPlayerId.equals(player.getId())) {

				String publicKey = ParsePublicKey.convertPublicKey(player.getPublicKey());
				sendData(Constants.I_AM_A_MASTER, publicKey, "nada");

				if (ManagesTheList.findPlayerById(getPlayers(), senderPlayerId) == null) {
					Player player = new Player(senderPlayerId);
					player.setPublicKey(ParsePublicKey.recoveryPublicKey(receivedMessage));
					getPlayers().add(player);
				}

				System.out.println("Numero de jogadores: " + getPlayers().size());
				if (!gameStarted && getPlayers().size() > MINIMUM_PLAYERS) {
					gameStarted = true;
					startTheGame();
				}
				sendData(Constants.LIST, ParseList.parseListToString(getPlayers()), Constants.EMPTY);
			}
			break;

		/*
		 * Alguem dizendo que eh o mestre
		 */
		case Constants.I_AM_A_MASTER:
			// se receber a propria mensagem de que eh um mestre, inicia o jogo
			if (senderPlayerId.startsWith(player.getId())) {
				if (receivedIdentifier.equals(Constants.I_AM_A_MASTER)) {
					ImMaster = true;
					// game();
				}
			} else {
				ImMaster = false;
				publicKeyMaster = ParsePublicKey.recoveryPublicKey(receivedMessage);
			}
			break;

		/*
		 * Master notifica que tem que dizer uma letra
		 */
		case Constants.PLAYER_SELECT_LETTER:

			if (whoShouldReceive.equals(player.getId()) && nowIsLetter) {
				String received = Criptography.decriptografa(receivedMessage, player.getPrivateKey());
				// jogador espera usuario digitar uma letra
				String letter = new Ui().getUiLetter();
				if (letter.isEmpty())
					letter = Constants.EMPTY;
				else 
					letter = letter.substring(0, 1);
				String encrypt = Criptography.criptografa(letter, publicKeyMaster);
				sendData(Constants.MASTER_LETTER_SELECTED_BY_THE_PLAYER, encrypt, "nada");
			} else {
				nowIsLetter = true;
			}
			break;

		/*
		 * Master recebe a letra e compara como a palavra oculta
		 */
		case Constants.MASTER_LETTER_SELECTED_BY_THE_PLAYER:
			if (!ImMaster)
				break;
			// desabilita time
			timer.cancel();
			numAttemptsNotification = 0;

			// TODO por tosin [21 de set de 2016] Decriptografar
			String letter = Criptography.decriptografa(receivedMessage, player.getPrivateKey());
			letter = receivedMessage;
			letter = letter.substring(0,1);

			ui.showSimpleMessage("Recebeu a letra: " + letter);
			
			if (letter.isEmpty() || letter.equals(Constants.EMPTY)) {
				String temp = VerifyLetterWord.status(hiddenWord, chosenLetter);
				temp += Punctuation.buildPunctuation(getPlayers());

				sendData(Constants.PLAYER_HIT_THE_LETTER, Constants.LETTER_WRONG, currentPlayer.getId());
				sendData(Constants.PLAYER_PUNCTUATION, temp, "nada");

				String encode = Criptography.criptografa(Constants.EMPTY, currentPlayer.getPublicKey());
				sendData(Constants.PLAYER_SELECT_WORD, encode, currentPlayer.getId());
				break;
			}

			// verfica se a letra ja foi chutada
			if (VerifyLetterWord.letterAlreadKick(letter, chosenLetter)) {
				Log.master("Que burro a letra ja foi usada!!!!");
				players = ManagesTheList.setPunctuation(getPlayers(), currentPlayer, Punctuation.punctuationWrong());
				sendData(Constants.PLAYER_HIT_THE_LETTER, Constants.LETTER_ALREAD_CHOSEN, "nada");
			} else {
				if(letter.length() == 1)
					chosenLetter.add(letter);
				if (VerifyLetterWord.hitTheLetter(hiddenWord, letter)) {
					Log.master("Acertou a letra");
					players = ManagesTheList.setPunctuation(getPlayers(), currentPlayer,
							Punctuation.punctuationRight());
					sendData(Constants.PLAYER_HIT_THE_LETTER, Constants.LETTER_RIGHT, "nada");
				} else {
					Log.master("ERRO!!! nao tem essa letra");
					players = ManagesTheList.setPunctuation(getPlayers(), currentPlayer,
							Punctuation.punctuationWrong());
					sendData(Constants.PLAYER_HIT_THE_LETTER, Constants.LETTER_WRONG, "nada");
				}
			}

			// verifica se acertou a palavra
			if (VerifyLetterWord.discoveryTheWord(hiddenWord, chosenLetter)) {
				Log.master("ACABOU!!!! ACABOU!!!! EH TETRA!!!!");
				sendData(Constants.CONGRATULATIONS, "nada", "nada");

				String temp = VerifyLetterWord.status(hiddenWord, chosenLetter);
				temp += Punctuation.buildPunctuation(getPlayers());

				sendData(Constants.PLAYER_PUNCTUATION, temp, "nada");

				/*
				 * Notifica os usuario para o novo mestre assumir
				 */
				ImMaster = false;
				sendData(Constants.GAME_OVER, currentPlayer.getId(), currentPlayer.getId());

			}
			// se errou envia letras acertadas, letras erradas e pontuacao
			else {
				// envia pontucao e pergunta a palavra

				Log.master("acertou a letra, mas me diga, qual eh a palavra?");
				String temp = VerifyLetterWord.status(hiddenWord, chosenLetter);
				temp += Punctuation.buildPunctuation(getPlayers());

				sendData(Constants.PLAYER_PUNCTUATION, temp, "nada");

				String encode = Criptography.criptografa(Constants.EMPTY, currentPlayer.getPublicKey());
				sendData(Constants.PLAYER_SELECT_WORD, encode, currentPlayer.getId());
			}
			break;

		/*
		 * Master notifica que tem que dizer uma palavra
		 */
		case Constants.PLAYER_SELECT_WORD:
			if (whoShouldReceive.equals(player.getId())) {
				String received = Criptography.decriptografa(receivedMessage, player.getPrivateKey());
				if (received.equals(Constants.EMPTY))
					ui.showSimpleMessage("Mensagem chegou errada....\n\n");
				
				nowIsLetter = !nowIsLetter;
				// jogador espera usuario digitar uma letra
				String word = new Ui().getUiWord();
				String encrypt = Criptography.criptografa(word, publicKeyMaster);
				if (word.length() == 1)
					sendData(Constants.MASTER_WORD_SELECTED_BY_THE_PLAYER, "nada", "nada");
				else
					sendData(Constants.MASTER_WORD_SELECTED_BY_THE_PLAYER, encrypt, "nada");
			}
			break;

		case Constants.PLAYER_PUNCTUATION:

			new Ui().showPunctuation(receivedMessage);

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
			
		case Constants.MASTER_WORD_SELECTED_BY_THE_PLAYER:
			if (!ImMaster)
				break;
			// desabilita time
			timer.cancel();
			numAttemptsNotification = 0;

			// TODO por tosin [22 de set de 2016] decriptar
			String word = Criptography.decriptografa(receivedMessage, player.getPrivateKey());
			ui.showSimpleMessage("Player: " + senderPlayerId + " chutou a palavra: " + word);

			// verifica se a palavra foi descoberta
			if (hiddenWord.equals(word)) {
				Log.master("ACABOU!!!! ACABOU!!!! EH TETRA!!!!");
				sendData(Constants.CONGRATULATIONS, "nada", "nada");

				String temp2 = VerifyLetterWord.status(hiddenWord, chosenLetter);
				temp2 += Punctuation.buildPunctuation(getPlayers());

				sendData(Constants.PLAYER_PUNCTUATION, temp2, "nada");

				/*
				 * Notifica os usuario para o novo mestre assumir
				 */
				ImMaster = false;
				sendData(Constants.GAME_OVER, currentPlayer.getId(), currentPlayer.getId());
			} else {
				String temp2 = VerifyLetterWord.status(hiddenWord, chosenLetter);
				temp2 += Punctuation.buildPunctuation(getPlayers());

				sendData(Constants.PLAYER_PUNCTUATION, temp2, "nada");
				
				if (word.equals(Constants.EMPTY)) {
					Log.master("PASSOU A VEZ!! Proximo!!!!");
				}
				else {
					currentPlayer = ManagesTheList.nextPlayer(getPlayers(), player, currentPlayer);
					Log.master("ERRO!! Proximo!!!!");
				}

				String encode = Criptography.criptografa(Constants.EMPTY, currentPlayer.getPublicKey());
				sendData(Constants.PLAYER_SELECT_LETTER, encode, currentPlayer.getId());
			}
			break;

		/*
		 * Master notifica que o jogo acabou, e envia id do proximo jogador que
		 * deve assumir o papel de mestre
		 */
		case Constants.GAME_OVER:
			// receivedMessege contem o id do jogador que sera o mestre
			String decripto = receivedMessage;
			new Ui().finish(decripto);
			System.out.println("Fim de jogo o novo mestre sera: " + decripto);
			nowIsLetter = true;
			chosenLetter.clear();
			hiddenWord = "";
			if (player.getId().equals(decripto)) {
				ImMaster = true;
				gameStarted = false;
				getPlayers().clear();
				getPlayers().add(player);

			}
			// se nao for o novo mestre envia um handshake para o novo
			// mestre
			else {
				ImMaster = false;
				String publicKey = ParsePublicKey.convertPublicKey(player.getPublicKey());
				sendData(Constants.INITIAL_HANDSHAKE, publicKey, "nada");
			}
			break;

		/*
		 * Acertou a palavra
		 */
		case Constants.CONGRATULATIONS:
			new Ui().GameOver();
			break;

		case Constants.LIST: 
//			if (!ImMaster) {
//				System.out.println("Sincronizou a lista");
//				List<Player> temp = ParseList.parseStringToList(receivedMessage);
//				if(temp != null && !temp.isEmpty()) {
//					getPlayers().clear();
//					getPlayers().addAll(temp);
//					publicKeyMaster = getPlayers().get(0).getPublicKey();
//				}
//			}
			break;
		default:
			break;
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
	private void sendData(final String identifier, final String data, final String idDestination) {
		/*
		 * Se o jogador estiver enviando a letra ou a palavra habilita o time
		 * para resposta
		 * 
		 * Senao simplemente envia a mensagem
		 */
		if (identifier.equals(Constants.PLAYER_SELECT_LETTER) || identifier.equals(Constants.PLAYER_SELECT_WORD)) {
			
			String message = player.getId() + " - " + identifier + " - " + data + " - " + idDestination;
			new MulticastSender().send(message);
			
			Log.time();
			timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Log.time();
					if (numAttemptsNotification < MAXIMUM_NOTIFICATIONS) {
						String message = player.getId() + " - " + identifier + " - " + data + " - " + idDestination;
						new MulticastSender().send(message);
						numAttemptsNotification++;
					} else {
						ui.showSimpleMessage("O tempo do jogador " + currentPlayer.getId() + " acabou");
						numAttemptsNotification = 0;
						currentPlayer = ManagesTheList.nextPlayer(getPlayers(), player, currentPlayer);

						// requisita letra do proximo jogador
						String encode = Criptography.criptografa(Constants.EMPTY, currentPlayer.getPublicKey());
						sendData(Constants.PLAYER_SELECT_LETTER, encode, currentPlayer.getId());

					}
				}
			}, WAIT_ANSWER);

			
			// tenta envia algumas vezes senao der certo envia para um no
			// jogador

		} else {
			String message = player.getId() + " - " + identifier + " - " + data + " - " + idDestination;
			new MulticastSender().send(message);
		}
	}

	public List<Player> getPlayers() {
		if (players == null)
			players = new ArrayList<>();
		return players;
	}

	public Player getPlayerById(String id) {
		for (Player player : getPlayers()) {
			if (player.getId().equals(id))
				return player;
		}
		return null;
	}

}
