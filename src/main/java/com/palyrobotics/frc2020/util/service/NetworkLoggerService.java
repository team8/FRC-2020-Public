package com.palyrobotics.frc2020.util.service;

import com.esotericsoftware.minlog.Log;
import com.esotericsoftware.minlog.Log.Logger;
import com.palyrobotics.frc2020.util.http.InputThread;
import com.palyrobotics.frc2020.util.http.LightHttpServer;

import edu.wpi.first.wpilibj.Timer;

public class NetworkLoggerService extends ServerServiceBase implements RobotService {

	private final int kPort = 4000;
	private InputThread tThread = new InputThread();
	private Timer mTimer = new Timer();

	private Log.Logger mLogger = new Logger() {

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
			tThread.addLog(log);
			if (LightHttpServer.getServer().getConnected() == false) {

				// If we aren't connected, forward to system out which goes to driver station

				if (level == Log.LEVEL_ERROR) {
					System.err.println(log);
				} else {
					System.out.println(log);
				}
			} else {
				tThread.run();
				//LightHttpServer.getServer().run();
			}
		}
	};

	@Override
	public int getPort() {
		return kPort;
	}

	@Override
	public void start() {
		//super.start();
		mTimer.start();
		Log.setLogger(mLogger);
		Log.set(Log.LEVEL_TRACE);
		tThread.start("logs");
		//mServer.start();

	}

}
