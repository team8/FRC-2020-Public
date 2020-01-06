package com.palyrobotics.frc2020.robot;

import edu.wpi.first.wpilibj.RobotBase;

public final class RobotStarter {

    /**
     * Main initialization function. Do not perform any initialization here.
     *
     * <p>If you change your main robot class, change the parameter type.
     */
    public static void main(String... args) {
        RobotBase.startRobot(Robot::new);
    }
}
