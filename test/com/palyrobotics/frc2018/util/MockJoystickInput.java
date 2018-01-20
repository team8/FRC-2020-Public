package com.palyrobotics.frc2018.util;

public class MockJoystickInput extends JoystickInput {
    public MockJoystickInput() {
        super();
    }

    public void setY(double y) {
	super.y = y;
    }

    public void setX(double x) {
	super.x = x;
    }

    public void setButton(int button, boolean pressed) {
        super.buttons[button] = pressed;
    }

    public void setTrigger(boolean pressed) {
        super.buttons[0] = pressed;
    }
}
