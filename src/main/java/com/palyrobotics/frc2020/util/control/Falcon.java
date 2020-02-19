package com.palyrobotics.frc2020.util.control;

import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.ctre.phoenix.motorcontrol.can.TalonFX;

public class Falcon extends TalonFX implements Controller {

	static class FalconController extends Talon.TalonController {

		protected FalconController(BaseTalon talon) {
			super(talon);
		}
	}

	private final FalconController mController = new FalconController(this);
	private final String mName;

	public Falcon(int deviceNumber, String name) {
		super(deviceNumber);
		mName = name;
	}

	public boolean setOutput(ControllerOutput output) {
		return mController.setOutput(output);
	}

	/**
	 * @param positionConversion Units per native encoder ticks
	 * @param velocityConversion Velocity units per native encoder ticks per 100ms
	 */
	public void configSensorConversions(double positionConversion, double velocityConversion) {
		mController.mPositionConversion = positionConversion;
		mController.mVelocityConversion = velocityConversion;
	}

	public String getName() {
		return String.format("(Falcon #%d), %s", getDeviceID(), mName);
	}

	public double getConvertedPosition() {
		return getSelectedSensorPosition() * mController.mPositionConversion;
	}

	public double getConvertedVelocity() {
		return getSelectedSensorVelocity() * mController.mVelocityConversion;
	}
}
