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
			process = Runtime.getRuntime().exec("npm i", null, new File("src/main/java/com/palyrobotics/frc2020/util/http/Control-Center-2"));
			process = Runtime.getRuntime().exec("npm run dev", null, new File("src/main/java/com/palyrobotics/frc2020/util/http/Control-Center-2"));
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
