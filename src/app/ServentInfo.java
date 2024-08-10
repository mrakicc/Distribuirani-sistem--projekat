package app;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This is an immutable class that holds all the information for a servent.
 *
 * @author bmilojkovic
 */
public class ServentInfo implements Serializable {

	private static final long serialVersionUID = 5304170042791281555L;
	private final String ipAddress;
	private final int listenerPort;
	private final int chordId;
	private Map<Integer, Integer> requestMap;
	private SuzukiKasamiToken token;
	public AtomicBoolean haveToken;


	public ServentInfo(String ipAddress, int listenerPort) {
		this.ipAddress = ipAddress;
		this.listenerPort = listenerPort;
		this.chordId = ChordState.chordHash(listenerPort);
		this.requestMap = new HashMap<>();
		this.token = null;
		this.haveToken = new AtomicBoolean(false);
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public int getListenerPort() {
		return listenerPort;
	}

	public int getChordId() {
		return chordId;
	}

	public SuzukiKasamiToken getToken() {
		return token;
	}

	public void setToken(SuzukiKasamiToken token) {
		this.token = token;
	}

	public Map<Integer, Integer> getRequestMap() {
		return requestMap;
	}

	@Override
	public String toString() {
		return "[" + chordId + "|" + ipAddress + "|" + listenerPort + "]";
	}

}
