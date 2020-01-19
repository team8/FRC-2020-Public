package com.palyrobotics.frc2020.util.control;

import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

public class Falcon extends WPI_TalonFX {

	static class FalconController extends Talon.TalonController {

		protected FalconController(BaseTalon talon) {
			super(talon);
		}
	}

	public Falcon(int deviceNumber) {
		super(deviceNumber);
	}

	private final FalconController mController = new FalconController(this);

	public boolean setOutput(ControllerOutput output) {
		return mController.setOutput(output);
	}
}
