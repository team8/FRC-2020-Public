package com.palyrobotics.frc2020.util.service;

import java.util.Deque;
import java.util.LinkedList;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;
import com.esotericsoftware.minlog.Log.Logger;

import edu.wpi.first.wpilibj.Timer;

public class NetworkLoggerService extends ServerServiceBase implements RobotService {

	private static final int kPort = 5802;
	private static final int kMaxLogs = 500;

	private Deque<LogEntry> mLogs = new LinkedList<>();
	private Timer mTimer = new Timer();

	private Logger mLogger = new Logger() {

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
			mLogs.addLast(log);
			while (mLogs.size() > kMaxLogs) {
				mLogs.removeFirst();
			}
			if (mServer.getConnections().length == 0) {
				// If we aren't connected, forward to system out which goes to driver station
				if (level == Log.LEVEL_ERROR) {
					System.err.println(log);
				} else {
					System.out.println(log);
				}
			} else {
				mServer.sendToAllTCP(log);
			}
		}
	};

	@Override
	int getPort() {
		return kPort;
	}

	@Override
	public void connected(Connection connection) {
		super.connected(connection);
		for (LogEntry log : mLogs) {
			mServer.sendToTCP(connection.getID(), log);
		}
	}

	@Override
	public void start() {
		super.start();
		mServer.getKryo().register(LogEntry.class);
		Log.setLogger(mLogger);
		Log.set(Log.LEVEL_TRACE);
		mTimer.start();
	}
}
