package com.palyrobotics.frc2020.util.service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.esotericsoftware.minlog.Log.Logger;

import edu.wpi.first.wpilibj.Timer;

public class NetworkLogger extends Logger implements RobotService {

	private static final String kLoggerTag = "logger";

	private static final int kPort = 5802;

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
				Log.info(kLoggerTag, "Logger connected!");
			}

			@Override
			public void disconnected(Connection connection) {
				Log.warn(kLoggerTag, "Logger disconnected!");
			}
		});
		mServer.start();
		try {
			mServer.bind(kPort, kPort);
			Log.info(kLoggerTag, "Started server", null);
		} catch (IOException exception) {
			Log.error(kLoggerTag, "Server failed to start", exception);
		}
		Log.setLogger(this);
		Log.set(Log.LEVEL_TRACE);
		mTimer.start();
	}

	@Override
	public void log(int level, String category, String message, Throwable exception) {
		if (category != null && (category.equals("kryo") || category.equals("kryo.FieldSerializerConfig"))) {
			// Kryonet can possibly log when this function is called, creating a recursive
			// loop
			return;
		}
		double timeSeconds = mTimer.get();
		long timeMilliseconds = Math.round(timeSeconds * 1000.0);
		var log = new LogEntry(timeMilliseconds, level, category, message, exception);
		mLogs.add(log);
		if (mServer.getConnections().length == 0) {
			// If we aren't connected, forward to system out which goes to driver station
			if (level == Log.LEVEL_ERROR) {
				System.err.println(log);
			} else {
				System.out.println(log);
			}
		} else {
			while (!mLogs.isEmpty()) {
				mServer.sendToAllTCP(mLogs.remove());
			}
		}
	}
}
