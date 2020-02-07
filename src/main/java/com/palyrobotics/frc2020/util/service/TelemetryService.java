package com.palyrobotics.frc2020.util.service;

import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.config.Configs;

public class TelemetryService extends ServerServiceBase {

	static class Update {

		RobotState state;
		Commands commands;
		HashMap<String, Object> arbitrary;
	}

	@Override
	int getPort() {
		return 5807;
	}

	@Override
	public void update(@ReadOnly RobotState state, @ReadOnly Commands commands) {
		if (mServer.getConnections().length > 0) {
			var telemetry = new Update();
			telemetry.state = state;
			telemetry.commands = commands;
			try {
				String json = Configs.getMapper().writeValueAsString(telemetry);
				mServer.sendToAllUDP(json);
			} catch (JsonProcessingException ignored) {
			}
		}
	}
}
