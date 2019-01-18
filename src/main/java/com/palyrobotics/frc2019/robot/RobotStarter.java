package com.palyrobotics.frc2019.robot;

import edu.wpi.first.wpilibj.RobotBase;

public final class RobotStarter {

    private RobotStarter() {
    }

    /**
     * Main initialization function. Do not perform any initialization here.
     *
     * <p>If you change your main robot class, change the parameter type.
     */
    public static void main(String... args) {
        RobotBase.startRobot(Robot::new);
    }
}
