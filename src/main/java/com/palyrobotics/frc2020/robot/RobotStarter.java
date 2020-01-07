package com.palyrobotics.frc2020.robot;

import edu.wpi.first.wpilibj.RobotBase;

public final class RobotStarter {

    public static void main(String... args) {
        RobotBase.startRobot(Robot::new);
    }
}
