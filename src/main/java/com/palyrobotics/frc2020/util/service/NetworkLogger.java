package com.palyrobotics.frc2020.util.service;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.esotericsoftware.minlog.Log.Logger;
import edu.wpi.first.wpilibj.Timer;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class NetworkLogger extends Logger implements RobotService {

	private static final String LOGGER_CATEGORY = "logger";

	private static final int PORT = 5802;

	private Server mServer;
	private Queue<LogEntry> mLogs = new LinkedList<>();
	private Timer mTimer = new Timer();

	@Override
	public void start() {
		mServer = new Server();
		mServer.getKryo().register(LogEntry.class);
		mServer.addListener(new Listener() {

			@Override
			public void connected(Connection connection) {
				Log.info(LOGGER_CATEGORY, "Logger connected!");
			}

			@Override
			public void disconnected(Connection connection) {
				Log.warn(LOGGER_CATEGORY, "Logger disconnected!");
			}
		});
		mServer.start();
		try {
			mServer.bind(PORT, PORT);
			log(Log.LEVEL_INFO, LOGGER_CATEGORY, "Started Log Server", null);
		} catch (IOException exception) {
			log(Log.LEVEL_ERROR, LOGGER_CATEGORY, "Server failed to start", exception);
		}
		Log.setLogger(this);
		Log.set(Log.LEVEL_TRACE);
		mTimer.start();
	}

	@Override
	public void log(int level, String category, String message, Throwable exception) {
		if (category != null && (category.equals("kryo") || category.equals("kryo.FieldSerializerConfig"))) {
			// Kryonet can possibly log when logging, creating a recursive loop
			return;
		}
		double timeSeconds = mTimer.get();
		long timeMilliseconds = Math.round(timeSeconds * 1000.0);
		var log = new LogEntry(timeMilliseconds, level, category, message, exception);
		if (exception != null) {
			exception.printStackTrace();
		}
		mLogs.add(log);
		if (mServer.getConnections().length == 0) {
			System.out.println(log.toString()); // If we aren't connected, forward to system out which goes to driver station
		} else {
			while (!mLogs.isEmpty()) {
				mServer.sendToAllTCP(mLogs.remove());
			}
		}
	}
}
