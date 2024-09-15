package com.palyrobotics.frc2020.util.service;

public class ServerService extends ServerServiceBase implements RobotService {

	@Override
	public void start() {
		super.start();
	}

	@Override
	public int getPort() {
		return 4000;
	}
}
