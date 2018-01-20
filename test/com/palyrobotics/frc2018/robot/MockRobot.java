package com.palyrobotics.frc2018.robot;

import com.palyrobotics.frc2018.config.MockRobotState;

/**
 * Created by EricLiu on 11/12/17.
 */
public class MockRobot extends Robot {
    private static MockRobotState robotState = new MockRobotState();
    public static MockRobotState getRobotState() {return robotState;};
}
