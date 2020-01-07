package com.palyrobotics.frc2020.config;

import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.util.Color;

/**
 * Holds all hardware input, such as sensors. <br />
 * Can be simulated
 *
 * @author Nihar
 */
public class RobotState {

    public static final int kUltrasonicBufferSize = 10;
    private static RobotState sInstance = new RobotState();
    private final DifferentialDriveOdometry m_Odometry = new DifferentialDriveOdometry(new Rotation2d()); // TODO use gyro properly to set this
    // Updated by autoInit, teleopInit, disabledInit
    public GamePeriod gamePeriod = GamePeriod.DISABLED;
    public double robotVelocity, robotAcceleration;
    public boolean isQuickTurning;
    public double leftDriveVelocity, rightDriveVelocity, leftDrivePosition, rightDrivePosition;


    public String gameData;

    // TODO: ultrasonics
    // public CircularBuffer
    //         leftIntakeReadings = new CircularBuffer(kUltrasonicBufferSize),
    //         rightIntakeReadings = new CircularBuffer(kUltrasonicBufferSize);
    // // Pusher
    // public boolean hasPusherCargo, hasPusherCargoFar;
    // public double cargoPusherDistance;
    // public CircularBuffer pusherReadings = new CircularBuffer(kUltrasonicBufferSize);
    // Pose stores drivetrain sensor data
    public Pose2d drivePose = new Pose2d();
    // Pusher sensor data
    public double pusherPosition, pusherVelocity;
    // Elevator sensor data
    public double elevatorPosition, elevatorVelocity;
    // Vision drive data
    public boolean atVisionTargetThreshold;

    public static RobotState getInstance() {
        return sInstance;
    }

    public String closestColorString;
    public double closestColorConfidence;
    public Color detectedRGBVals;
    public ColorMatchResult closestColorRGB;

    // TODO: ultrasonics
    // public void resetUltrasonics() {
    //     for (CircularBuffer buffer : List.of(leftIntakeReadings, rightIntakeReadings, pusherReadings)) {
    //         for (int i = 0; i < kUltrasonicBufferSize; i++) {
    //             buffer.addFirst(Double.MAX_VALUE);
    //         }
    //     }
    // }

    public void resetOdometry() {
        m_Odometry.resetPosition(new Pose2d(), new Rotation2d());
    }

    public void updateOdometry(double headingDegrees, double leftMeters, double rightMeters) {
        drivePose = m_Odometry.update(Rotation2d.fromDegrees(headingDegrees), leftMeters, rightMeters);
    }

    public enum GamePeriod {
        AUTO, TELEOP, TESTING, DISABLED
    }
}
