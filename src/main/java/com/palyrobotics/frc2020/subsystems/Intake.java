package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.control.ControllerOutput;

public class Intake extends SubsystemBase {

	public enum State {
		RUNNING, STOWED
	}

	private static final Intake sInstance = new Intake();
	private final ControllerOutput mOutput = new ControllerOutput();
	private boolean mSolenoidOutput;

	private Intake() {
	}

	public static Intake getInstance() {
		return sInstance;
	}

	public ControllerOutput getOutput() {
		return mOutput;
	}

	public boolean getSolenoidOutput() {
		return mSolenoidOutput;
	}

	@Override
	public void update(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		switch (commands.getIntakeWantedState()) {
			case RUNNING:
				if (!state.intakeTransitioning && state.intakeExtended) {
					mOutput.setPercentOutput(commands.getIntakeWantedPo());
				} else {
					mOutput.setIdle();
				}
				mSolenoidOutput = true;
				break;
			case STOWED:
				mOutput.setIdle();
				mSolenoidOutput = false;
				break;
		}
	}
}
