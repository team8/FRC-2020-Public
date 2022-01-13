package com.palyrobotics.frc2020.util.http;

import static com.palyrobotics.frc2020.util.http.HttpInput.getInstance;

import java.io.*;
import java.net.*;
import java.util.*;

import com.palyrobotics.frc2020.util.config.ConfigUploadManager;

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
			ConfigUploadManager.getInstance().update();
			runServer();
		} catch (IOException | InterruptedException | IllegalAccessException | NoSuchFieldException e) {
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
			String ignore = "";
			boolean isPost = request.startsWith("POST");
			int contentLength = 0;
			while (!(ignore = in.readLine()).equals("")) {
				if (ignore.startsWith("Content-Length: ")) {
					contentLength = Integer.parseInt(ignore.substring("Content-Length: ".length()));
				}
			}
			StringBuilder body = new StringBuilder();
			JSONObject config = new JSONObject();
			if (isPost) {
				int c = 0;
				for (int i = 0; i < contentLength; i++) {
					c = in.read();
					body.append((char) c);
				}
				config = new JSONObject(body.toString());

				ConfigUploadManager.getInstance().updateConfig(config);
			}

			// src/main/deploy/ << deploy folder

			if (!(request.startsWith("GET ") || request.startsWith("POST ")) || !(request.endsWith(" HTTP/1.0") || request.endsWith(" HTTP/1.1"))) {
				// bad request
				pout.print("HTTP/1.0 400 Bad Request" + newLine + newLine);
			} else {
				if (isPost) {
					pout.print(
							"HTTP/1.0 200 OK" + newLine +
									"Access-Control-Allow-Origin: http://10.0.8.2:8000" + newLine +
									"Content-Type: application/json" + newLine +
									"Date: " + new Date() + newLine +
									"Content-length: " + config.toString().length() + newLine + newLine +
									config.toString());
				} else {
					System.out.println("GET REQUEST");
					JSONObject response = getInstance().getInput();

					Iterator<String> responseKeys = response.keys();

					while (responseKeys.hasNext()) {
						String key = responseKeys.next();
						lastInput.put(key, response.get(key));
					}

					pout.print(
							"HTTP/1.0 200 OK" + newLine +
									"Access-Control-Allow-Origin: *" + newLine +
									"Content-Type: text/plain" + newLine +
									"Date: " + new Date() + newLine +
									"Content-length: " + lastInput.toString().length() + newLine + newLine +
									lastInput.toString());
				}
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
