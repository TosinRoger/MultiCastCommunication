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

	private static final int DELAY = 10* 1000;
	private static final int MICRO_DELAY = 1 * 1000;
	private static final int MINIMUM_PLAYERS = 2;
	private static final int MAXIMUM_NOTIFICATIONS = 3;

	private boolean respended = false;

	private List<Player> players;

	private Player player;
	private boolean ImMaster = true;
	private int numAttemptsNotification = 0; // numero da tentatica de
												// notifincacao

	private String hiddenWord;
	private List<String> chosenLetter = new ArrayList<>();

	private int positionOfTime = 1;

	private boolean nowIsLetter = true; // esta entrando 2x na mensagem, var
										// para entrar so uma

	private PublicKey publicKeyMaster;
	
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
							Thread.sleep(3 * 1000);
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
		sendData(Constants.INITIAL_HANDSHAKE, convertPublicKey(getPlayer().getPublicKey()));

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
		String senderPlayerId = array.length >= 1 ? array[0] : "";
		String receivedIdentify = array.length >= 2 ? array[1] : "";
		String receivedMessage = array.length == 3 ? array[2] : "";

		// descriptogra mensagem se for != INITIAL_HANDSHAKE
		//
		// if(!Constants.INITIAL_HANDSHAKE.equals(receivedIdentify)) {
		// receivedMessage = Criptography.decriptografa(receivedMessage,
		// getPlayer().getPrivateKey());
		//
		// if(receivedMessage == null)
		// return;
		// }

		if (receivedIdentify.equals(Constants.YOUR_CREDENTIAL) && receivedMessage.equals(getPlayer().getId())) {
			sendData(Constants.INITIAL_HANDSHAKE, convertPublicKey(getPlayer().getPublicKey()));
		}

		// se receber a propria mensagem de que eh um mestre, inicia o jogo
		if (senderPlayerId.startsWith(getPlayer().getId())) {
			if (receivedIdentify.equals(Constants.I_AM_A_MASTER)) {
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
			 * Alguem pergunto se tem alguem no canal.
			 * A mensagem recebida contem o publicKey de quem enviou a mensagem
			 */
			case Constants.INITIAL_HANDSHAKE:
				Log.game("Quer entrar");
				Log.handshakeLog("Received initial handshake: " + ImMaster + ", " + message);
				/*
				 * se for o mestre, envia que o mestre e envia o publicKey
				 * adiciona o usuario a lista e envia a lista para os jogadores
				 */
				if (ImMaster == true) {
					Log.handshakeLog(message);
					// master envia chave publica
					sendData(Constants.I_AM_A_MASTER, convertPublicKey(getPlayer().getPublicKey()));

					// se o jogador ainda nao esta na lista adiciona o jogador
					if (!idAlreadyRegister(senderPlayerId)) {
						Player newPlayer = new Player(senderPlayerId);
//						newPlayer.setPublicKey(recoveryPublicKey(receivedMessage));
						getPlayers().add(newPlayer);
					}
					
					// envia a lista de todos os jogadores para todos
					sendData(Constants.LIST, new Parser().parseListToString(getPlayers()));
				}
				
				break;
			/*
			 * Alguem dizendo que eh o mestre
			 */
			case Constants.I_AM_A_MASTER:
				ImMaster = false;
				Log.handshakeLog("im not a master: " + ImMaster);
				
//				if (!receivedMessage.isEmpty())
//					publicKeyMaster = recoveryPublicKey(receivedMessage);

				break;

			/*
			 * Master notifica que tem que dizer uma letra a mensagem contem o
			 * id do jogador que deve "dizer" uma letra
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
				if (VerifyLetterWord.letterAlreadKick(letter, chosenLetter)) {
					Log.master("Que burro a letra ja foi usada!!!!");
					getPlayers().get(positionOfTime).setPunctuation(new Punctuation().punctuationWrong());
					sendData(Constants.PLAYER_HIT_THE_LETTER, Constants.LETTER_ALREAD_CHOSEN);
				} else {
					chosenLetter.add(letter);
					if (VerifyLetterWord.hitTheLetter(hiddenWord, letter)) {
						Log.master("Acertou a letra");
						getPlayers().get(positionOfTime).setPunctuation(new Punctuation().punctuationRight());
						sendData(Constants.PLAYER_HIT_THE_LETTER, Constants.LETTER_RIGHT);
					} else {
						Log.master("ERRO!!! nao tem essa letra");
						getPlayers().get(positionOfTime).setPunctuation(new Punctuation().punctuationWrong());
						sendData(Constants.PLAYER_HIT_THE_LETTER, Constants.LETTER_WRONG);
					}
				}

				// verifica se acertou a palavra
				if (VerifyLetterWord.discoveryTheWord(hiddenWord, chosenLetter)) {
					Log.master("ACABOU!!!! ACABOU!!!! EH TETRA!!!!");
					sendData(Constants.CONGRATULATIONS, "");

					String temp = VerifyLetterWord.status(hiddenWord, chosenLetter);
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
					String temp = VerifyLetterWord.status(hiddenWord, chosenLetter);
					temp += new Punctuation().buildPunctuation(getPlayers());

					System.out.println("\n" + temp + "\n");

					sendData(Constants.PLAYER_PUNCTUATION, temp);

					try {
						Thread.sleep(MICRO_DELAY);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					sendData(Constants.PLAYER_SELECT_WORD, getPlayers().get(positionOfTime).getId());
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
					if (word.length() == 1)
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

				String temp = VerifyLetterWord.status(hiddenWord, chosenLetter);
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
				if (hiddenWord.equals(receivedMessage)) {
					Log.master("ACABOU!!!! ACABOU!!!! EH TETRA!!!!");
					sendData(Constants.CONGRATULATIONS, "");

					String temp2 = VerifyLetterWord.status(hiddenWord, chosenLetter);
					temp2 += new Punctuation().buildPunctuation(getPlayers());

					sendData(Constants.PLAYER_PUNCTUATION, temp2);

					/*
					 * Notifica os usuario para o novo mestre assumir
					 */
					positionOfTime = getPlayers().indexOf(getPlayer());
					nextPlayer();
					ImMaster = false;
					sendData(Constants.GAME_OVER, getPlayers().get(positionOfTime).getId());
				} else {
					String temp2 = VerifyLetterWord.status(hiddenWord, chosenLetter);
					temp2 += new Punctuation().buildPunctuation(getPlayers());

					sendData(Constants.PLAYER_PUNCTUATION, temp2);

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
			 * Master notifica que o jogo acabou, e envia id do proximo jogador
			 * que deve assumir o papel de mestre
			 */
			case Constants.GAME_OVER:
				// receivedMessege contem o id do jogador que sera o mestre
				new Ui().finish(receivedMessage);
				System.out.println("game_over, new master will be: " + receivedMessage);
				nowIsLetter = true;
				if (getPlayer().getId().equals(receivedMessage)) {
					ImMaster = true;
					getPlayers().clear();
					getPlayers().add(getPlayer());
					chosenLetter.clear();
					System.out.println("list size: " + getPlayers().size());

					// envia mensagem para o antigo mestre pedindo sua
					// credenciais

					sendData(Constants.YOUR_CREDENTIAL, senderPlayerId);

					game();
				}
				// se nao for o novo mestre envia um handshake para o novo
				// mestre
				else {
					try {
						Thread.sleep(MICRO_DELAY);
						sendData(Constants.INITIAL_HANDSHAKE,
								convertPublicKey(getPlayers().get(positionOfTime).getPublicKey()));

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
	private void sendData(final String identify, final String data) {

		/*
		 * Se o jogador estiver enviando a letra ou a palavra habilita o time
		 * para resposta
		 * 
		 * Senao simplemente envia a mensagem
		 */
		if (identify.equals(Constants.PLAYER_SELECT_LETTER) || identify.equals(Constants.PLAYER_SELECT_WORD)) {
			respended = false;

			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					while (numAttemptsNotification < MAXIMUM_NOTIFICATIONS) {
						String message = getPlayer().getId() + ":" + identify + ":" + data;
						new MulticastSender().send(message);

						// aguarda 1 minuto para tentar enviar novamente
						try {
							Thread.sleep(10 * 1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						if (respended) {
							numAttemptsNotification = 0;
							// Thread.interrupted();
							break;
						} else {
							numAttemptsNotification++;
						}
						Log.deuRuim("Nao recebeu a resposta: " + (numAttemptsNotification + 1));
					}

					if (!respended && numAttemptsNotification >= MAXIMUM_NOTIFICATIONS) {
						if (positionOfTime < getPlayers().size())
							new Ui().timeEnd(getPlayers().get(positionOfTime).getId());

						numAttemptsNotification = 0;
						respended = false;
						nextPlayer();
						// requisita letra do proximo jogador
						sendData(Constants.PLAYER_SELECT_LETTER, getPlayers().get(positionOfTime).getId());

					}
				}
			}).start();
			// tenta envia algumas vezes senao der certo envia para um no
			// jogador

		} else {
			String message = getPlayer().getId() + ":" + identify + ":" + data;
			new MulticastSender().send(message);
		}
	}

	/**
	 * Seta a posicao do proximo jogador da lista. 
	 * Nao conta a primeira posicao que sera o master. 
	 * esse metodo so deve ser usado pelo master.
	 */
	private void nextPlayer() {
		// passa para proximo jogador
		positionOfTime++;

		if (positionOfTime == getPlayers().size()) {
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

	/**
	 * Converte um public key em um string
	 * @param publicKey
	 * @return
	 */
	private String convertPublicKey(PublicKey publicKey) {
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
	 * @param message
	 * @return
	 */
	private PublicKey recoveryPublicKey(String message) {

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

	public Player getPlayer() {
		return player;
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
