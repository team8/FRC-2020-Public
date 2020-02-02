package com.palyrobotics.frc2020.util.input;

import java.util.HashMap;
import java.util.Map;

public class XboxController extends edu.wpi.first.wpilibj.XboxController {

	private static final double kTriggerThreshold = 0.8;
	private int mLastPOV = -1;
	private Map<Hand, Boolean> mLastTriggers = new HashMap<>(Map.of(Hand.kLeft, false, Hand.kRight, false));

	public XboxController(int port) {
		super(port);
	}

	public void updateLastInputs() {
		mLastPOV = getPOV();
		mLastTriggers.replaceAll((hand, b) -> isTriggerDown(hand));
	}

	private boolean isTriggerDown(Hand hand) {
		return getTriggerAxis(hand) > kTriggerThreshold;
	}

	public boolean getDPadRightPressed() {
		return getPOV() != mLastPOV && getDPadRight();
	}

	public boolean getDPadRightReleased() {
		return mLastPOV == 90;
	}

	public boolean getDPadRight() {
		return getPOV() == 90;
	}

	public boolean getDPadUpPressed() {
		return getPOV() != mLastPOV && getDPadUp();
	}

	public boolean getDPadUPReleased() {
		return mLastPOV == 0;
	}

	public boolean getDPadUp() {
		return getPOV() == 0;
	}

	public boolean getDPadDownPressed() {
		return getPOV() != mLastPOV && getDPadDown();
	}

	public boolean getDPadDownReleased() {
		return mLastPOV == 180;
	}

	public boolean getDPadDown() {
		return getPOV() == 180;
	}

	public boolean getDPadLeftPressed() {
		return getPOV() != mLastPOV && getDPadLeft();
	}

	public boolean getDPadLeftReleased() {
		return mLastPOV == 270;
	}

	public boolean getDPadLeft() {
		return getPOV() == 270;
	}

	public boolean getRightBumper() {
		return getBumper(Hand.kRight);
	}

	public boolean getLeftBumper() {
		return getBumper(Hand.kLeft);
	}

	public boolean getTriggerPressed(Hand hand) {
		return mLastTriggers.get(hand) != getTriggerAxis(hand) > kTriggerThreshold &&
				getTriggerAxis(hand) > kTriggerThreshold;
	}

	public boolean getTriggerReleased(Hand hand) {
		return mLastTriggers.get(hand);
	}

	public boolean getWindowButtonPressed() {
		return getRawButtonPressed(7);
	}

	public boolean getMenuButtonPressed() {
		return getRawButtonPressed(8);
	}

	public boolean getRightTriggerPressed() {
		return getTriggerPressed(Hand.kRight);
	}

	public boolean getRightTriggerReleased() {
		return getTriggerReleased(Hand.kRight);
	}

	public boolean getLeftTriggerPressed() {
		return getTriggerPressed(Hand.kLeft);
	}

	public boolean getLeftTriggerReleased() {
		return getTriggerReleased(Hand.kLeft);
	}

	public boolean getRightBumperPressed() {
		return getBumperPressed(Hand.kRight);
	}

	public boolean getRightBumperReleased() {
		return getBumperReleased(Hand.kRight);
	}

	public boolean getLeftBumperPressed() {
		return getBumperPressed(Hand.kLeft);
	}

	public boolean getLeftBumperReleased() {
		return getBumperReleased(Hand.kLeft);
	}

	public void setRumble(boolean on) {
		setRumble(RumbleType.kRightRumble, on ? 1.0 : 0.0);
		setRumble(RumbleType.kLeftRumble, on ? 1.0 : 0.0);
	}

	public boolean getRightTrigger() {
		return isTriggerDown(Hand.kRight);
	}

	public boolean getLeftTrigger() {
		return isTriggerDown(Hand.kLeft);
	}
}
