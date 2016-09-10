package br.com.tosin.sd.multicast.networks;

import br.com.tosin.sd.multicast.controller.Controller;
import br.com.tosin.sd.multicast.interfaces.Response;
import br.com.tosin.sd.multicast.models.MasterPlayer;
import br.com.tosin.sd.multicast.utils.Constants;
import br.com.tosin.sd.multicast.utils.Log;


/**
 * Classe que verifica se ha algum programa rodando no pool. 
 * Verifica se ou outros programas sao {@link MasterPlayer}, 
 * senao houver nenhum se torna um {@link MasterPlayer}.
 * 
 * IMPORTANTE, e necessario rodar o execute()
 * @author tosin
 *
 */
public class HasSomeoneOnGrup {
	
	private static final int DELAY = 1000;
	private boolean ImMaster;
	
	private Response listerReturn;
	
		public HasSomeoneOnGrup(Response listerReturn) {
		super();
		this.listerReturn = listerReturn;
	}

	//TODO por tosin [9 de set de 2016] descobrir como tornar o metodo obrigatorio
	public void execute() {
		Log.handshakeLog("start");
		
		listenPool();
		askAnotherPlayer();
		
	}
	
	private void listenPool() {
		// comeca escutando o pool
				new Thread(new MulticastReceived(new Response() {
					
					@Override
					public void response(String messageReceived) {
						// TODO Auto-generated method stub
						Log.handshakeLog("message received");
						readReceivedMessage(messageReceived);
						
					}
				})).start();
	}
	
	private void askAnotherPlayer() {
		String message = Controller.getPlayer().getId();
		message += ":" + Constants.INITIAL_HANDSHAKE; 
		new MulticastSender().send(message);
	}
	
	/**
	 * Se receber apenas a mensage enviada por si mesmo, se tornar master
	 * Se houver alguem response, verificar se ele e o mestre, e se tornar jogador. 
	 */
	private void readReceivedMessage(String msg) {
		// received own message
		if (msg.startsWith(Controller.getPlayer().getId())){
			/* 
			 * cria um time para ver se algum responde 
			 * senao intitula-se mestre
			 */
			Log.handshakeLog("is my message");
			ImMaster = true;
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						Log.handshakeLog("Start delay");
						Thread.sleep(DELAY);
						Log.handshakeLog("Finish delay, i'm master: " + ImMaster);
						/*
						 * Se master == TRUE envia para o controler que e um mester
						 * se master == FALSE ja deve ser contemplado no enquato a thread aguarda
						 */
						if (ImMaster) {
							listerReturn.response(Constants.I_AM_A_MASTER);
						}
						
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		}
		// received messege from someone
		else {
			ImMaster = false;
			Log.handshakeLog("not is my message");
			/*
			 * Verificar se a mensagem contem um pergunta "Constant.INITIAL_HANDSHAKE" 
			 * ou se esta disendo que ja e master
			 */
			if (msg.contains(Constants.INITIAL_HANDSHAKE) && Controller.getMaster() != null) {
				sendImMaster();
			}
			
		}
	}
	
	/**
	 * Envia que eh mestre, 
	 * tem que fazer a troca de chaves para player identificar o master
	 * e para abrir as proximas mensagem que serao criptografadas
	 */
	private void sendImMaster() {
		Log.handshakeLog("EU SOU O MESTRE!!!!");
		
		String msg = Controller.getMaster().getId() + ":" +  "EU SOU O MESTRE!!!!";
		new MulticastSender().send(msg);
	}
}
