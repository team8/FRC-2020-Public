package com.palyrobotics.frc2019.util;

import edu.wpi.first.wpilibj.Joystick;

/**
 * Note that WPILib has their own XboxController class as of 2017 but doesn't allow inversion <br />
 * Implements an Xbox Controller as Joystick
 * @author Nihar based on Eric's code
 *
 */
public class XboxController extends Joystick {
	// 1 or -1 to invert y values
	private int invertLeftY = 1;
	private int invertRightY = 1;

	private double triggerThreshold = 0.9;

	/**
	 * Construct the default Xbox joystick
	 * @param port USB port for joystick
	 */
	public XboxController(int port) {
		super(port);
	}

	/**
	 * Construct Xbox joystick with both y sticks inverted
	 * @param port USB port for joystick
	 * @param invertBothY true to invert the y orientations for both sticks
	 */
	public XboxController(int port, boolean invertBothY) {
		super(port);
		this.invertLeftY = invertBothY ? -1:1;
		this.invertRightY = invertLeftY;
	}

	/**
	 * Construct Xbox joystick, choose which sticks are inverted
	 * @param port USB port for joystick
	 * @param invertLeftY true to invert the y orientation of left stick
	 * @param invertRightY true to invert the y orientation of right stick
	 */
	public XboxController(int port, boolean invertLeftY, boolean invertRightY) {
		super(port);
		this.invertLeftY = invertLeftY ? -1:1;
		this.invertRightY = invertRightY ? -1:1;
	}

	public void setTriggerThreshold(double threshold) {
		this.triggerThreshold = threshold;
	}

	/**
	 * X value of the left stick
	 * @return -1 to 1 for x-axis
	 */
	public double getLeftX() {
		return super.getRawAxis(0);
	}

	/**
	 * Y value of the left stick
	 * Returns inverted values if constructed
	 * @return -1 to 1 for left y-axis
	 */
	public double getLeftY() {
		return super.getRawAxis(1) * invertLeftY;
	}

	/**
	 * X value of the right stick
	 * @return -1 to 1 for right x-axis
	 */
	public double getRightX() {
		return super.getRawAxis(4);
	}

	/**
	 * Y value of the right stick
	 * Returns inverted values if constructed
	 * @return -1 to 1 for right y-axis
	 */
	public double getRightY() {
		return super.getRawAxis(5) * invertRightY;
	}

	/**
	 * "A" Button of Xbox
	 * @return true if A button is pressed
	 */
	public boolean getButtonA() {
		return super.getRawButton(1);
	}

	/**
	 * "B" Button of Xbox
	 * @return true if B button is pressed
	 */
	public boolean getButtonB() {
		return super.getRawButton(2);
	}

	/**
	 * "X" Button of Xbox
	 * @return true if X button is pressed
	 */
	public boolean getButtonX() {
		return super.getRawButton(3);
	}

	/**
	 * "Y" Button of Xbox
	 * @return true if Y button is pressed
	 */
	public boolean getButtonY() {
		return super.getRawButton(4);
	}

	/**
	 * Left stick pressed in
	 * @return true if left stick is pressed
	 */
	public boolean getLeftStickPressed() {
		return super.getRawButton(9);
	}

	/**
	 * Right stick pressed in
	 * @return true if right stick is pressed
	 */
	public boolean getRightStickPressed() {
		return super.getRawButton(10);
	}

	/**
	 * Get right bumper triggered
	 * @return true if right bumper pressed
	 */
	public boolean getRightBumper() {
		return super.getRawButton(6);
	}

	/**
	 * Get left bumper triggered
	 * @return true if left bumper pressed
	 */
	public boolean getLeftBumper() {
		return super.getRawButton(5);
	}

	/**
	 * "Start" Button of Xbox
	 * @return true if Start button is pressed
	 */
	public boolean getButtonStart() {
		return super.getRawButton(9);
	}

	/**
	 * "Back" Button of Xbox
	 * @return true if Back button is pressed
	 */
	public boolean getButtonBack() {
		return super.getRawButton(10);
	}

	/**
	 * Status of Left Trigger pressed (LT)
	 * Scales from 0 to 1 depending on how much it is pressed
	 * @return 0 (no press) to 1 (fully pressed)
	 */
	public double getLeftTrigger() {
		return super.getRawAxis(2);
	}

	/**
	 * Status of Right Trigger pressed (RT)
	 * Scales from 0 to 1 depending on how much it is pressed
	 * @return 0 (no press) to 1 (fully pressed)
	 */
	public double getRightTrigger() {
		return super.getRawAxis(3);
	}

	/**
	 * Status of Left Trigger pressed (LT)
	 * If pressed beyond threshold, returns true
	 * @return true if pressed beyond threshold
	 */
	public boolean getLeftTriggerPressed() {
		return getLeftTrigger() > triggerThreshold;
	}

	/**
	 * Status of Right Trigger pressed (RT)
	 * If pressed beyond threshold, returns true
	 * @return true if pressed beyond threshold
	 */
	public boolean getRightTriggerPressed() {
		return getRightTrigger() > triggerThreshold;
	}

	/**
	 * DPad up pressed
	 * @return true if pressed up
	 */
	public boolean getDpadUp() {
		return super.getPOV() == 0;
	}

	/**
	 * DPad down pressed
	 * @return true if pressed down
	 */
	public boolean getDpadDown() {
		return super.getPOV() == 180;
	}

	/**
	 * DPad left pressed
	 * @return true if pressed left
	 */
	public boolean getDpadLeft() {
		return super.getPOV() == 270;
	}

	/**
	 * DPad right pressed
	 * @return true if pressed right
	 */
	public boolean getDpadRight() {
		return super.getPOV() == 90;
	}

	public void setRumble(boolean on) {
	    setRumble(RumbleType.kRightRumble, on ? 1.0 : 0.0);
	    setRumble(RumbleType.kLeftRumble, on ? 1.0 : 0.0);
    }
}