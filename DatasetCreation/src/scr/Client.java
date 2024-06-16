package scr;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.function.Predicate;

import javax.swing.SwingUtilities;

import scr.Controller.Stage;
import scr.Host;
import scr.Peer;


public class Client 
{
	/*
	* Client per interfacciarsi con un server TORCS (The Open Racing Car Simulator).
	* Il client gestisce la connessione tramite socket UDP, riceve lo stato di gioco
	* dal server, elabora le azioni del driver tramite un controller specificato,
	* e invia le azioni al server per controllare il veicolo virtuale.
	*/
	private static int UDP_TIMEOUT = 10000;
	private static int port;
	private static String host;
	private static String clientId;
	private static boolean verbose;
	private static int maxEpisodes;
	private static int maxSteps;
	private static Stage stage;
	private static String trackName;

	private static final String FILENAME = "../dataset/dataset.csv";

	/**
	 * @param args viene utilizzato per definire tutte le opzioni del client.
	 *             - port:N viene utilizzato per specificare la porta per la connessione (il valore predefinito è 3001).
	 *             - host:INDIRIZZO viene utilizzato per specificare l'indirizzo dell'host dove il server è in esecuzione (il valore predefinito è localhost).
	 *             - id:ClientID viene utilizzato per specificare l'ID del client inviato al server (il valore predefinito è championship2009).
	 *             - verbose:on viene utilizzato per attivare la modalità verbose (il valore predefinito è spento).
	 *             - maxEpisodes:N viene utilizzato per impostare il numero di episodi (il valore predefinito è 1).
	 *             - maxSteps:N viene utilizzato per impostare il numero massimo di passaggi per ogni episodio (il valore predefinito è 0, che significa numero illimitato di passaggi).
	 *             - stage:N viene utilizzato per impostare lo stadio corrente: 0 è WARMUP, 1 è QUALIFYING, 2 è RACE, altri valori significano UNKNOWN (il valore predefinito è UNKNOWN).
	 *             - trackName:nome viene utilizzato per impostare il nome della pista attuale.
	 * @throws Exception 
	 */

	public static void main(String[] args) throws Exception 
	{
		Logger logger = new Logger(null, "[Client]");

		parseParameters(args);
		SocketHandler mySocket = new SocketHandler(host, port, verbose);
		String inMsg;

		Controller driver = load(args[0]);
		driver.setStage(stage);
		driver.setTrackName(trackName);

		/* Build init string */
		float[] angles = driver.initAngles();
		String initStr;
		initStr=buildInitString(angles);
		
		OurContinuousCharReaderUI charReaderUI = new OurContinuousCharReaderUI();

		Peer dswSocket = null;
		Host remoteHost = null;
		try {
			dswSocket = new Peer();
			remoteHost = new Host();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.log(e.getMessage() + "\n");
		}
		
		initSocket(dswSocket,remoteHost, args);

		SwingUtilities.invokeLater(() -> charReaderUI.start());

		long curEpisode = 0;
		boolean shutdownOccurred = false;
		do {

			/*
			 * Client identification
			 */

			do {
				mySocket.send(initStr);
				inMsg = mySocket.receive(UDP_TIMEOUT);
			} while (inMsg == null || inMsg.indexOf("***identified***") < 0);

			/*
			 * Start to drive
			 */

			long currStep = 0;
			while (true) {
				/*
				 * Receives from TORCS the game state
				 */
				inMsg = mySocket.receive(UDP_TIMEOUT);

				if (inMsg != null) {

					/*
					 * Check if race is ended (shutdown)
					 */
					if (inMsg.indexOf("***shutdown***") >= 0) {
						shutdownOccurred = true;
						System.out.println("Server shutdown!");
						break;
					}

					/*
					 * Check if race is restarted
					 */
					if (inMsg.indexOf("***restart***") >= 0) {
						driver.reset();
						if (verbose)
							System.out.println("Server restarting!");
						break;
					}

					Action action = new Action();
					if (currStep < maxSteps || maxSteps == 0)
					{
						MessageBasedSensorModel msg = new MessageBasedSensorModel(inMsg);
						action = driver.control(msg, charReaderUI.getKeysPressed());
												
						if (dswSocket != null)
						{
							String sample = formatMsgToSample(msg, action);
							
							try {
								// Si aggiunge "#!" come carattere delimitatore della stringa poiché 
								// la dimensione del pacchetto UDP è fissato ad un valore che 
								// sarà sicuramente superiore. Quindi il client provvederà a troncare 
								// il nuovo messaggio fino a "#!". Questo raggionamento è valido solo
								// per la comunicazione con la classe DatasetWriter. 
								dswSocket.sendString(sample + "#!", remoteHost);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					else
						action.restartRace = true;

					currStep++;
					mySocket.send(action.toString());
				} else
					System.out.println("Server did not respond within the timeout");
			}

		} while (++curEpisode < maxEpisodes && !shutdownOccurred);

		/*
		 * Shutdown the controller
		 */
		driver.shutdown();
		mySocket.close();
		System.out.println("Client shutdown.");
		System.out.println("Bye, bye!");

	}

	private static String formatMsgToSample(MessageBasedSensorModel msg, Action action) 
	{
		String textMsg = formatDouble(msg.getSpeed()) + "," +
							formatDouble(msg.getAngleToTrackAxis()) + "," +
							arrayToString(msg.getTrackEdgeSensors()) + "," +
							arrayToString(msg.getFocusSensors()) + "," +
							formatDouble(msg.getGear()) + "," +
							arrayToString(msg.getOpponentSensors()) + "," +
							formatDouble(msg.getRacePosition()) + "," +
							formatDouble(msg.getLateralSpeed()) + "," +
							formatDouble(msg.getCurrentLapTime()) + "," +
							formatDouble(msg.getDamage()) + "," +
							formatDouble(msg.getDistanceFromStartLine()) + "," +
							formatDouble(msg.getDistanceRaced()) + "," +
							formatDouble(msg.getFuelLevel()) + "," +
							formatDouble(msg.getLastLapTime()) + "," +
							formatDouble(msg.getRPM()) + "," +
							formatDouble(msg.getTrackPosition()) + "," +
							arrayToString(msg.getWheelSpinVelocity()) + "," +
							formatDouble(msg.getZ()) + "," +
							formatDouble(msg.getZSpeed());
		String sample = textMsg + action;
		sample = sample.replace("accel", "")
						.replace("brake", "")
						.replace("gear", "")
						.replace("steer", "")
						.replace("clutch", "")
						.replace("meta", "")
						.replace("focus", "")
						.replaceAll("\\(", ",")
						.replaceAll("\\)", "")
						.replaceAll(" ", "");
		return sample;
	}

	private static void initSocket(Peer dswSocket, Host remoteHost, String[] args) throws Exception
	{
		if(dswSocket == null){
			throw new Exception("dswSocket was null");
		}

		if (remoteHost == null) {
			throw new Exception("remoteHost was null");
		}
	
		if(Arrays.asList(args).contains("sampler:on"))
		{
			try 
			{
				// Command to execute
				String command = "cmd /k java DatasetWriter.java";

				// Execute the command
				// Runtime.getRuntime().exec(command);
				
				Map<String, String> propertiesNames = new HashMap<>();
				propertiesNames.put("dsw_ip", "dsw_to_client.ip");
				propertiesNames.put("dsw_port", "dsw_to_client.port");
				propertiesNames.put("this_ip", "client_to_dsw.ip");
				propertiesNames.put("this_port", "client_to_dsw.port");

				
				Collection<String> values = propertiesNames.values();
				String[] stringArray = new String[values.size()];
				int index = 0;
				for (Object obj : values) 
				{
					if (obj instanceof String) 
					{
						stringArray[index++] = (String) obj;
					} 
					else 
					{
						System.err.println("a propertyName isn't a sting.");
					}
				}
				HashMap<String, String> props = Utility.readProperties(stringArray);
				Host thisHost = new Host(
					InetAddress.getByName(props.get(propertiesNames.get("this_ip"))), 
					Integer.parseInt(props.get(propertiesNames.get("this_port")))
				);
				remoteHost.setAddress(InetAddress.getByName(props.get(propertiesNames.get("dsw_ip"))));
				remoteHost.setPort(Integer.parseInt(props.get(propertiesNames.get("dsw_port"))));

				dswSocket.setHost(thisHost.getAddress(), thisHost.getPort());

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static String buildInitString(float[] angles)
	{
		String initStr = clientId + "(init";
		for (int i = 0; i < angles.length; i++) 
		{
			initStr = initStr + " " + angles[i];
		}
		initStr = initStr + ")";
		return initStr;
	}

	private static void parseParameters(String[] args) {
		/*
		 * Set default values for the options
		 */
		port = 3001;
		host = "localhost";
		clientId = "SCR";
		verbose = false;
		maxEpisodes = 1;
		maxSteps = 0;
		stage = Stage.UNKNOWN;
		trackName = "unknown";

		for (int i = 1; i < args.length; i++) {
			StringTokenizer st = new StringTokenizer(args[i], ":");
			String entity = st.nextToken();
			String value = st.nextToken();
			if (entity.equals("port")) {
				port = Integer.parseInt(value);
			}
			if (entity.equals("host")) {
				host = value;
			}
			if (entity.equals("id")) {
				clientId = value;
			}
			if (entity.equals("verbose")) {
				if (value.equals("on"))
					verbose = true;
				else if (value.equals(false))
					verbose = false;
				else {
					System.out.println(entity + ":" + value + " is not a valid option");
					System.exit(0);
				}
			}
			if (entity.equals("id")) {
				clientId = value;
			}
			if (entity.equals("stage")) {
				stage = Stage.fromInt(Integer.parseInt(value));
			}
			if (entity.equals("trackName")) {
				trackName = value;
			}
			if (entity.equals("maxEpisodes")) {
				maxEpisodes = Integer.parseInt(value);
				if (maxEpisodes <= 0) {
					System.out.println(entity + ":" + value + " is not a valid option");
					System.exit(0);
				}
			}
			if (entity.equals("maxSteps")) {
				maxSteps = Integer.parseInt(value);
				if (maxSteps < 0) {
					System.out.println(entity + ":" + value + " is not a valid option");
					System.exit(0);
				}
			}
		}
	}

	private static Controller load(String name) {
		Controller controller = null;
		try {
			controller = (Controller) (Object) Class.forName(name).newInstance();
		} catch (ClassNotFoundException e) {
			System.out.println(name + " is not a class name");
			System.exit(0);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return controller;
	}
	
	private static String arrayToString(double[] array) {
		if (array == null || array.length == 0) {
			return "[]";
		}
	
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			sb.append(formatDouble(array[i]));
			if (i < array.length - 1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}

    private static String formatDouble(double value) {
        return String.format(Locale.US, "%4.8f", value); // Format to 6 decimal places
    }
}
