package com.palyrobotics.frc2020.util.service;

import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2020.util.http.LightHttpServer;

public abstract class ServerServiceBase implements RobotService {

	protected String mLoggerTag = getConfigName();
	//protected LightHttpServer mServer = null;

	@Override
	public void start() {
		int port = getPort();
		try {
			LightHttpServer.setServer(port);
			LightHttpServer.getServer().start();
		} catch (Exception e) {
			System.out.println(e);
		}
		Log.info(mLoggerTag, "Started server");
	}

	abstract int getPort();
}
