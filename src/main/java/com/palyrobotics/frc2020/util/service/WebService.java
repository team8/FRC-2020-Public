package com.palyrobotics.frc2020.util.service;

import java.io.File;
import java.io.IOException;

import com.esotericsoftware.minlog.Log;

public class WebService implements Runnable, RobotService {

	private Thread webThread;

	@Override
	public void run() {
		Process process = null;
		try {
			Log.info("Beginning Website Setup");
			process = Runtime.getRuntime().exec("python3 -m http.server", null, new File("/home/lvuser/deploy/website"));
			Log.info("Finished Website Setup");
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
