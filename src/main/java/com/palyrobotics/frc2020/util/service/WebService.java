package com.palyrobotics.frc2020.util.service;

import java.io.File;
import java.io.IOException;

public class WebService implements Runnable {

	private Thread webThread;

	@Override
	public void run() {
		Process process = null;
		try {
			System.out.println("web setup beginning");
			process = Runtime.getRuntime().exec("python3 -m http.server", null, new File("/home/lvuser/deploy/out"));

			System.out.println("web setup complete");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		if (webThread == null) {
			webThread = new Thread(this, "websiteThread");
			webThread.start();
		}
	}
}
