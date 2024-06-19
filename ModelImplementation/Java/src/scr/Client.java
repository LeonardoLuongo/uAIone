package scr;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;


import scr.Controller.Stage;

public class Client {

	private static long counter = 0;

	private static int UDP_TIMEOUT = 15000;
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
	 */

	public static void main(String[] args) {
		parseParameters(args);
		SocketHandler mySocket = new SocketHandler(host, port, verbose);
		String inMsg;
		
		Controller driver = load(args[0]);
		driver.setStage(stage);
		driver.setTrackName(trackName);

		/* Build init string */
		float[] angles = driver.initAngles();
		String initStr = clientId + "(init";
		for (int i = 0; i < angles.length; i++) {
			initStr = initStr + " " + angles[i];
		}
		initStr = initStr + ")";

		Peer pySocket = null;
		Host remoteHost = null;
		if(Arrays.asList(args).contains("sampler:on"))
		{
			try {
				// Command to execute
				String command = "cmd /k java DatasetWriter.java";

				// Execute the command
				// Runtime.getRuntime().exec(command);
				
				Map<String, String> propertiesNames = new HashMap<>();
				propertiesNames.put("knn_ip", "knn_to_client.ip");
				propertiesNames.put("knn_port", "knn_to_client.port");
				propertiesNames.put("this_ip", "client_to_knn.ip");
				propertiesNames.put("this_port", "client_to_knn.port");

				
				Collection<String> values = propertiesNames.values();
				String[] stringArray = new String[values.size()];
				int index = 0;
				for (Object obj : values) {
					if (obj instanceof String) {
						stringArray[index++] = (String) obj;
					} else {
						System.err.println("a propertyName isn't a sting.");
					}
				}
				HashMap<String, String> props = Utility.readProperties(stringArray);
				System.out.println(propertiesNames);
				System.out.println(props);
				Host thisHost = new Host(
					InetAddress.getByName(props.get(propertiesNames.get("this_ip"))), 
					Integer.parseInt(props.get(propertiesNames.get("this_port")))
				);
				remoteHost = new Host(
					InetAddress.getByName(props.get(propertiesNames.get("knn_ip"))), 
					Integer.parseInt(props.get(propertiesNames.get("knn_port")))
				);

				pySocket = new Peer(thisHost.getAddress(), thisHost.getPort());

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

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
					String str = ",";
					Action action = new Action();
					if (currStep < maxSteps || maxSteps == 0)
					{
						MessageBasedSensorModel msg = new MessageBasedSensorModel(inMsg);
	
						String sampleToPredict = formatDouble(msg.getSpeed()) + "," +
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
						
						try {
							pySocket.sendString(sampleToPredict, remoteHost);
							// Prediction result
							System.out.print("\n\tWaiting for Model response...\n\n");
							str = pySocket.receiveString();

							System.out.println(str);

							SimpleDriver _driver = new SimpleDriver();

							action.accelerate = Double.parseDouble(str.split(",")[0]);
							action.brake = Double.parseDouble(str.split(",")[1]);
							action.clutch = 0.0;
							action.gear =  _driver.getGear(msg);
							action.steering = Double.parseDouble(str.split(",")[2]);
							action.restartRace = false;
							action.focus = 360;

							counter = counter + 1;
							if(counter % 100 == 0)
								System.out.println(action);

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
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
			controller = (Controller) (Object) Class.forName(name);
		} catch (ClassNotFoundException e) {
			System.out.println(name + " is not a class name");
			System.exit(0);
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
