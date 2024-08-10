package servent.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import app.AppConfig;
import app.ChordState;
import app.ServentInfo;
import servent.message.*;
import servent.message.util.MessageUtil;

public class NewNodeHandler implements MessageHandler {

	private Message clientMessage;
	
	public NewNodeHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}
	
	@Override
	public void run() {
		synchronized (AppConfig.chordState.lock){
			if (clientMessage.getMessageType() == MessageType.NEW_NODE) {
				int newNodePort = clientMessage.getSenderPort();
				ServentInfo newNodeInfo = new ServentInfo("localhost", newNodePort);

				//check if the new node collides with another existing node.
				if (AppConfig.chordState.isCollision(newNodeInfo.getChordId())) {
					Message sry = new SorryMessage(AppConfig.myServentInfo.getListenerPort(), clientMessage.getSenderPort());
					MessageUtil.sendMessage(sry);
					return;
				}

				//check if he is my predecessor
				boolean isMyPred = AppConfig.chordState.isKeyMine(newNodeInfo.getChordId());
				if (isMyPred) { //if yes, prepare and send welcome message

					if(!AppConfig.myServentInfo.haveToken.get()) {

						AppConfig.myServentInfo.getRequestMap().put(AppConfig.myServentInfo.getChordId(), AppConfig.myServentInfo.getRequestMap().get(AppConfig.myServentInfo.getChordId()) + 1);

						Socket bsSocket = null;
						PrintWriter bsWriter = null;
						Scanner bsScanner = null;
						String[] parts;

						try {
							bsSocket = new Socket("localhost", AppConfig.BOOTSTRAP_PORT);
							bsWriter = new PrintWriter(bsSocket.getOutputStream());
							bsWriter.write("Active Servents\n" + AppConfig.myServentInfo.getListenerPort() + "\n");
							bsWriter.flush();

							bsScanner = new Scanner(bsSocket.getInputStream());
							parts = bsScanner.nextLine().split(",");

							AppConfig.activeServents = Arrays.stream(parts).map(Integer::parseInt).toList();
							bsSocket.close();
						} catch (IOException e) {
							throw new RuntimeException(e);
						}

						for (ServentInfo serventInfo : AppConfig.allServents) {
							//send requestToken to all active servents
							if (AppConfig.activeServents.contains(serventInfo.getListenerPort()) && serventInfo.getChordId() != newNodeInfo.getChordId()
									&& serventInfo.getChordId() != AppConfig.myServentInfo.getChordId()) {

								RequestTokenMessage requestCSMessage = new RequestTokenMessage(AppConfig.myServentInfo.getListenerPort(), serventInfo.getListenerPort(),
										AppConfig.myServentInfo.getChordId(), AppConfig.myServentInfo.getRequestMap().get(AppConfig.myServentInfo.getChordId()));

								MessageUtil.sendMessage(requestCSMessage);
							}
						}
					}

					//wait for token
					while (!AppConfig.myServentInfo.haveToken.get()) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							throw new RuntimeException(e);
						}
					}

					AppConfig.timestampedStandardPrint("Node " + AppConfig.myServentInfo.getChordId() + " has token, enters critical section and adds new node " + newNodeInfo.getChordId() + " to the system.");

					// Enter critical section
					ServentInfo hisPred = AppConfig.chordState.getPredecessor();
					if (hisPred == null) {
						hisPred = AppConfig.myServentInfo;
					}

					AppConfig.chordState.setPredecessor(newNodeInfo);

					Map<Integer, Integer> myValues = AppConfig.chordState.getValueMap();
					Map<Integer, Integer> hisValues = new HashMap<>();

					int myId = AppConfig.myServentInfo.getChordId();
					int hisPredId = hisPred.getChordId();
					int newNodeId = newNodeInfo.getChordId();

					for (Entry<Integer, Integer> valueEntry : myValues.entrySet()) {
						if (hisPredId == myId) { //i am first and he is second
							if (myId < newNodeId) {
								if (valueEntry.getKey() <= newNodeId && valueEntry.getKey() > myId) {
									hisValues.put(valueEntry.getKey(), valueEntry.getValue());
								}
							} else {
								if (valueEntry.getKey() <= newNodeId || valueEntry.getKey() > myId) {
									hisValues.put(valueEntry.getKey(), valueEntry.getValue());
								}
							}
						}
						if (hisPredId < myId) { //my old predecesor was before me
							if (valueEntry.getKey() <= newNodeId) {
								hisValues.put(valueEntry.getKey(), valueEntry.getValue());
							}
						} else { //my old predecesor was after me
							if (hisPredId > newNodeId) { //new node overflow
								if (valueEntry.getKey() <= newNodeId || valueEntry.getKey() > hisPredId) {
									hisValues.put(valueEntry.getKey(), valueEntry.getValue());
								}
							} else { //no new node overflow
								if (valueEntry.getKey() <= newNodeId && valueEntry.getKey() > hisPredId) {
									hisValues.put(valueEntry.getKey(), valueEntry.getValue());
								}
							}
						}

					}
					for (Integer key : hisValues.keySet()) { //remove his values from my map
						myValues.remove(key);
					}
					AppConfig.chordState.setValueMap(myValues);

					WelcomeMessage wm = new WelcomeMessage(AppConfig.myServentInfo.getListenerPort(), newNodePort, hisValues);
					MessageUtil.sendMessage(wm);

					AppConfig.isUpdated.set(false);

					while (!AppConfig.isUpdated.get()){
						try {
							UpdateFinishMessage ufm = new UpdateFinishMessage(AppConfig.myServentInfo.getListenerPort(), newNodeInfo.getListenerPort(), null);
							MessageUtil.sendMessage(ufm);
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					AppConfig.timestampedStandardPrint("Update for new node " + newNodeInfo.getChordId() + " is finished.");

					// Release critical section
					AppConfig.myServentInfo.getToken().getTokenMap().put(AppConfig.myServentInfo.getChordId(), AppConfig.myServentInfo.getRequestMap().get(AppConfig.myServentInfo.getChordId()));

					for (ServentInfo serventInfo : AppConfig.allServents) {
						if (!AppConfig.myServentInfo.getToken().getRequestList().contains(serventInfo.getChordId())) {
							int rn = AppConfig.myServentInfo.getRequestMap().get(serventInfo.getChordId());
							int ln = AppConfig.myServentInfo.getToken().getTokenMap().get(serventInfo.getChordId());

							if (rn == ln + 1) {
								AppConfig.myServentInfo.getToken().getRequestList().add(serventInfo.getChordId());
							}
						}
					}

					if (!AppConfig.myServentInfo.getToken().getRequestList().isEmpty()) {
						int nextSite = AppConfig.myServentInfo.getToken().getRequestList().remove(0);

						for (ServentInfo serventInfo : AppConfig.allServents) {
							if (serventInfo.getChordId() == nextSite) {
								TokenMessage tokenMessage = new TokenMessage(AppConfig.myServentInfo.getListenerPort(), serventInfo.getListenerPort(), AppConfig.myServentInfo.getToken());
								MessageUtil.sendMessage(tokenMessage);
								AppConfig.myServentInfo.setToken(null);
								AppConfig.myServentInfo.haveToken.set(false);
								break;
							}
						}
					}

				} else { //if he is not my predecessor, let someone else take care of it
					ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(newNodeInfo.getChordId());
					NewNodeMessage nnm = new NewNodeMessage(newNodePort, nextNode.getListenerPort());
					MessageUtil.sendMessage(nnm);
				}

			} else {
				AppConfig.timestampedErrorPrint("NEW_NODE handler got something that is not new node message.");
			}
		}
	}

}
