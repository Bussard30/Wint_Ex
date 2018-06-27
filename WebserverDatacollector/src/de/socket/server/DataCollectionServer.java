package de.socket.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class DataCollectionServer {

	public static final int PORT_INCOMING_DATA = 7637;

//	public static final File dataStoreDir = new File("arduinoDatenWebserver/");
	public static final File dataStoreDir = new File("H:/MintExWebserver/data/");
	
	public DataCollectionServer() {
		this.start();
	}

	public void start() {
		(new Thread() {
			public void run() {
				this.setName("data collector - Thread");
				
				while(true) {
				
					try {
						
						ServerSocket ss = new ServerSocket(PORT_INCOMING_DATA);
						BufferedReader reader;
						Logger.log("DCS-BOOT", "waiting for incoming data from Arduino");
						while (true) {
							Socket socket = ss.accept();
	
							reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	
							processData(reader.readLine());
	
							socket.close();
						}
	
					} catch (Exception exc) {
					}
					
				}
			}
		}).start();
	}

	/**
	 * 
	 * @param inputLine
	 */
	public void processData(String inputLine) {
		
		Logger.log("INFO", "line received: '" + inputLine + "'");
//		// Reihenfolge: 'start, Luftdruck, Luftfeuchtigkeit, Lufttemperatur, Windgeschwindigkeit(km/h), Wetter, Regenzustand,end' 

		Logger.log("INFO (internal)", "before: '" + inputLine + "'");
		//inputLine = inputLine.replace("; ", " ");
		inputLine = inputLine.replace(";", ":");
		inputLine = inputLine.substring(0, inputLine.length()-1);
		Logger.log("INFO (internal)", "after: '" + inputLine + "'");
		
		storeData(inputLine);
	}

	private void storeData(String inputLine) {
		dataStoreDir.mkdirs();
		File dataFile = new File(dataStoreDir + "/data_" + Logger.getCurrentTime("yyyy_MM_dd") + ".txt");

		try {

			Logger.log("INFO", "adding data to file: '" + dataFile.getAbsolutePath() + "'");
			// creates file IF FILE DOESNT YET EXIST
			dataFile.createNewFile();

			PrintWriter writer = new PrintWriter(new FileWriter(dataFile, true));

			writer.println("[" + Logger.getCurrentTime() + "]:'" + inputLine + "'");
			writer.flush();

			writer.close();
		} catch (Exception exc) {
			exc.printStackTrace();
		}

	}

	

}
