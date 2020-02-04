package com.palyrobotics.frc2020.util.service;

import java.io.IOException;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;

public abstract class ServerServiceBase extends Listener implements RobotService {

	protected Server mServer;
	protected String mLoggerTag = getConfigName();

	@Override
	public void start() {
		int port = getPort();
		mServer = new Server(100000, 100000);
		mServer.getKryo().register(LogEntry.class);
		mServer.addListener(this);
		mServer.start();
		try {
			mServer.bind(port, port);
			Log.info(mLoggerTag, "Started server");
		} catch (IOException exception) {
			Log.error(mLoggerTag, "Server failed to start", exception);
		}
	}

	abstract int getPort();

	@Override
	public void connected(Connection connection) {
		Log.info(mLoggerTag, "Connected!");
	}

	@Override
	public void disconnected(Connection connection) {
		Log.info(mLoggerTag, "Logger connected!");
	}
}
