package com.palyrobotics.frc2019.util;

import edu.wpi.first.wpilibj.Joystick;

/**
 * Class to store Joystick input
 * 
 * @author Nihar
 */
public class JoystickInput {

	protected double x, y, slider;
	protected boolean[] buttons = new boolean[12];

	@Override
	public String toString() {
		return "Joystick X: " + this.x + " Y: " + this.y;
	}

	public void update(Joystick j) {
		x = j.getX();
		y = j.getY();
		slider = j.getThrottle();
		for(int i = 1; i < 12; i++) {
			//getRawButton(1) is the trigger
			buttons[i] = j.getRawButton(i);
		}
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public double getSlider() {return this.slider; }

	public boolean getButtonPressed(int button) {
		return buttons[button];
	}

	public boolean getTriggerPressed() {
		return buttons[1];
	}
}
