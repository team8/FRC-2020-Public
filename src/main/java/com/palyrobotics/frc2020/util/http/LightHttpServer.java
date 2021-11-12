package com.palyrobotics.frc2020.util.http;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Iterator;

import org.json.JSONObject;

public class LightHttpServer implements Runnable {

	private static final String newLine = "\r\n";
	private static ServerSocket socket;
	private Thread httpThread;
	private JSONObject lastInput = new JSONObject();
	private static boolean connected = false;
	private static int port;
	private static LightHttpServer sInstance = new LightHttpServer();

	@Override
	public void run() {
		try {
			runServer();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void setServer(int newPort) {
		port = newPort;
		try {
			socket = new ServerSocket(port);
			connected = true;
		} catch (Throwable tr) {
			System.err.println("Could not start server: " + tr);
		}
	}

	public static LightHttpServer getServer() {
		return sInstance;
	}

	private LightHttpServer() {

	}

	public void runServer() throws IOException, InterruptedException {
		//while (true) {
		Socket connection = socket.accept();

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			OutputStream out = new BufferedOutputStream(connection.getOutputStream());
			PrintStream pout = new PrintStream(out);

			// read first line of request
			String request = in.readLine();
			//if (request == null) continue;

			// we ignore the rest
			while (true) {
				String ignore = in.readLine();
				if (ignore == null || ignore.length() == 0) break;
			}

			if (!request.startsWith("GET ") || !(request.endsWith(" HTTP/1.0") || request.endsWith(" HTTP/1.1"))) {
				// bad request
				pout.print("HTTP/1.0 400 Bad Request" + newLine + newLine);
			} else {
				JSONObject response = HttpInput.getInstance().getInput();

				Iterator<String> responseKeys = response.keys();

				while (responseKeys.hasNext()) {
					String key = responseKeys.next();
					lastInput.put(key, response.get(key));
				}

				pout.print(
						"HTTP/1.0 200 OK" + newLine +
								"Access-Control-Allow-Origin: http://localhost:3000" + newLine +
								"Content-Type: text/plain" + newLine +
								"Date: " + new Date() + newLine +
								"Content-length: " + lastInput.toString().length() + newLine + newLine +
								lastInput.toString());
			}

			pout.close();
		} catch (Throwable tri) {
			System.err.println("Error handling request: " + tri);
		}
		//}
	}

	public void start() {
		if (httpThread == null) {
			httpThread = new Thread(this, "serverThread");
			httpThread.start();
		}
	}

	public static boolean getConnected() {
		return connected;
	}
}
