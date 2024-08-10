package app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class contains all the global application configuration stuff.
 * @author bmilojkovic
 *
 */
public class AppConfig {

	/**
	 * Convenience access for this servent's information
	 */


	public static List<ServentInfo> allServents = new ArrayList<>();

	public static List<Integer> activeServents = new ArrayList<>();

	public static ServentInfo myServentInfo;

	public static String root;

	public static String bootstrapIpAddress;

	public static int strongFailure;

	public static int weakFailure;

	public static AtomicBoolean isUpdated = new AtomicBoolean(false);



	/**
	 * Print a message to stdout with a timestamp
	 * @param message message to print
	 */
	public static void timestampedStandardPrint(String message) {
		DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		Date now = new Date();
		
		System.out.println(timeFormat.format(now) + " - " + message);
	}
	
	/**
	 * Print a message to stderr with a timestamp
	 * @param message message to print
	 */
	public static void timestampedErrorPrint(String message) {
		DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		Date now = new Date();
		
		System.err.println(timeFormat.format(now) + " - " + message);
	}
	
	public static boolean INITIALIZED = false;
	public static int BOOTSTRAP_PORT;
	public static int SERVENT_COUNT;
	
	public static ChordState chordState;
	
	/**
	 * Reads a config file. Should be called once at start of app.
	 * The config file should be of the following format:
	 * <br/>
	 * <code><br/>
	 * servent_count=3 			- number of servents in the system <br/>
	 * chord_size=64			- maximum value for Chord keys <br/>
	 * bs.port=2000				- bootstrap server listener port <br/>
	 * servent0.port=1100 		- listener ports for each servent <br/>
	 * servent1.port=1200 <br/>
	 * servent2.port=1300 <br/>
	 * 
	 * </code>
	 * <br/>
	 * So in this case, we would have three servents, listening on ports:
	 * 1100, 1200, and 1300. A bootstrap server listening on port 2000, and Chord system with
	 * max 64 keys and 64 nodes.<br/>
	 * 
	 * @param configName name of configuration file
	 * @param serventId id of the servent, as used in the configuration file
	 */
	public static void readConfig(String configName, int serventId){
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(new File(configName)));
			
		} catch (IOException e) {
			timestampedErrorPrint("Couldn't open properties file. Exiting...");
			System.exit(0);
		}
		
		try {
			BOOTSTRAP_PORT = Integer.parseInt(properties.getProperty("bs.port"));
		} catch (NumberFormatException e) {
			timestampedErrorPrint("Problem reading bootstrap_port. Exiting...");
			System.exit(0);
		}

		//Dohvatanje adrese bootstrap servera
		try {
			bootstrapIpAddress = properties.getProperty("bootstrap_ip_address");
		} catch (NumberFormatException e) {
			timestampedErrorPrint("Problem reading bootstrap_ip_address. Exiting...");
			System.exit(0);
		}

		//Dohvatanje radnog korena
		root = properties.getProperty("root");

		// Učitavanje jake granice otkaza
		try {
			strongFailure = Integer.parseInt(properties.getProperty("strong_failure"));
		} catch (NumberFormatException e) {
			timestampedErrorPrint("Problem reading strong_failure_threshold. Exiting...");
			System.exit(0);
		}

		// Učitavanje slabe granice otkaza
		try {
			weakFailure = Integer.parseInt(properties.getProperty("weak_failure"));
		} catch (NumberFormatException e) {
			timestampedErrorPrint("Problem reading weak_failure_threshold. Exiting...");
			System.exit(0);
		}

		try {
			SERVENT_COUNT = Integer.parseInt(properties.getProperty("servent_count"));
		} catch (NumberFormatException e) {
			timestampedErrorPrint("Problem reading servent_count. Exiting...");
			System.exit(0);
		}
		
		try {
			int chordSize = Integer.parseInt(properties.getProperty("chord_size"));
			
			ChordState.CHORD_SIZE = chordSize;
			chordState = new ChordState();
			
		} catch (NumberFormatException e) {
			timestampedErrorPrint("Problem reading chord_size. Must be a number that is a power of 2. Exiting...");
			System.exit(0);
		}

		//Učitavanje informacija o svim serventima
		for (int i = 0; i < SERVENT_COUNT; i++) {
			ServentInfo serventInfo = createServentInfo(properties, i);
			allServents.add(serventInfo);
		}

		myServentInfo = allServents.get(serventId);
	}

	public static ServentInfo createServentInfo(Properties properties, int serventId) {
		String portProperty = "servent" + serventId + ".port";
		int serventPort = -1;

		try {
			serventPort = Integer.parseInt(properties.getProperty(portProperty));
		} catch (NumberFormatException e) {
			timestampedErrorPrint("Problem reading " + portProperty + ". Exiting...");
			System.exit(0);
		}

		return new ServentInfo("localhost", serventPort);
	}

	public static ServentInfo getNodeByPort(int port) {
		for (ServentInfo nodeInfo : AppConfig.allServents) {
			if (nodeInfo.getListenerPort() == port) {
				return nodeInfo;
			}
		}
		return null;
	}
	
}
