package com.palyrobotics.frc2020.util.input;

import java.util.EnumMap;
import java.util.Map;

public class XboxController extends edu.wpi.first.wpilibj.XboxController {

	private static final double kTriggerThreshold = 0.8;
	public static final int kPovUp = 0, kPovRight = 90, kPovDown = 180, kPovLeft = 270;
	private int mLastPOV = -1;
	private Map<Hand, Boolean> mLastTriggers = new EnumMap<>(Map.of(Hand.kLeft, false, Hand.kRight, false));

	public XboxController(int port) {
		super(port);
	}

	public void updateLastInputs() {
		mLastPOV = getPOV();
		mLastTriggers.replaceAll((hand, b) -> getTrigger(hand));
	}

	public void clearLastInputs() {
		mLastPOV = -1;
		mLastTriggers.replaceAll((hand, b) -> false);
	}

	public boolean getDPadRightPressed() {
		return getPOV() != mLastPOV && getDPadRight();
	}

	public boolean getDPadRightReleased() {
		return getPOV() != mLastPOV && mLastPOV == kPovRight;
	}

	public boolean getDPadRight() {
		return getPOV() == kPovRight;
	}

	public boolean getDPadUpPressed() {
		return getPOV() != mLastPOV && getDPadUp();
	}

	public boolean getDPadUPReleased() {
		return getPOV() != mLastPOV && mLastPOV == kPovUp;
	}

	public boolean getDPadUp() {
		return getPOV() == kPovUp;
	}

	public boolean getDPadDownPressed() {
		return getPOV() != mLastPOV && getDPadDown();
	}

	public boolean getDPadDownReleased() {
		return getPOV() != mLastPOV && mLastPOV == kPovDown;
	}

	public boolean getDPadDown() {
		return getPOV() == kPovDown;
	}

	public boolean getDPadLeftPressed() {
		return getPOV() != mLastPOV && getDPadLeft();
	}

	public boolean getDPadLeftReleased() {
		return getPOV() != mLastPOV && mLastPOV == kPovLeft;
	}

	public boolean getDPadLeft() {
		return getPOV() == kPovLeft;
	}

	public boolean getRightBumper() {
		return getBumper(Hand.kRight);
	}

	public boolean getLeftBumper() {
		return getBumper(Hand.kLeft);
	}

	public boolean getTrigger(Hand hand) {
		return getTriggerAxis(hand) > kTriggerThreshold;
	}

	public boolean getTriggerPressed(Hand hand) {
		return mLastTriggers.get(hand) != getTrigger(hand) && getTrigger(hand);
	}

	public boolean getTriggerReleased(Hand hand) {
		return getTrigger(hand) != mLastTriggers.get(hand) && mLastTriggers.get(hand);
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

	public void setRumble(boolean isOn) {
		setRumble(RumbleType.kRightRumble, isOn ? 1.0 : 0.0);
		setRumble(RumbleType.kLeftRumble, isOn ? 1.0 : 0.0);
	}

	public boolean getRightTrigger() {
		return getTrigger(Hand.kRight);
	}

	public boolean getLeftTrigger() {
		return getTrigger(Hand.kLeft);
	}
}
