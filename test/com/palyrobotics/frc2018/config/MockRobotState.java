package com.palyrobotics.frc2018.config;

import com.palyrobotics.frc2018.util.MockJoystickInput;

public class MockRobotState extends RobotState {
    public MockJoystickInput leftStickInput = new MockJoystickInput();
    public MockJoystickInput rightStickInput = new MockJoystickInput();
    public MockJoystickInput operatorStickInput = new MockJoystickInput();
    public MockJoystickInput elevatorStickInput = new MockJoystickInput();
}
