package com.palyrobotics.frc2019.config;

import com.palyrobotics.frc2019.util.Pose;
import com.palyrobotics.frc2019.util.trajectory.*;
import edu.wpi.first.wpilibj.CircularBuffer;

import java.util.List;
import java.util.Map;

/**
 * Holds all hardware input, such as sensors. <br />
 * Can be simulated
 *
 * @author Nihar
 */
public class RobotState {

    public static final int kUltrasonicBufferSize = 10;

    public enum GamePeriod {
        AUTO, TELEOP, DISABLED
    }

    private static RobotState sInstance = new RobotState();

    public double matchStartTimeSeconds;

    public static RobotState getInstance() {
        return sInstance;
    }

    protected RobotState() {
    }

    // Updated by autoInit, teleopInit, disabledInit
    public GamePeriod gamePeriod = GamePeriod.DISABLED;

    public double robotVelocity, robotAcceleration;

    public boolean isQuickTurning;

    public double leftDriveVelocity, rightDriveVelocity;

    // Intake
    public boolean hasIntakeCargo;
    public double
            cargoDistance,
            intakeStartAngle,  // Angle in degrees
            intakeAngle,  // Angle in degrees
            intakeAppliedOutput,
            intakeVelocity; // RPM
    public CircularBuffer
            leftIntakeReadings = new CircularBuffer(kUltrasonicBufferSize),
            rightIntakeReadings = new CircularBuffer(kUltrasonicBufferSize);

    // Pusher
    public boolean hasPusherCargo, hasPusherCargoFar;

    public double cargoPusherDistance;
    public CircularBuffer pusherReadings = new CircularBuffer(kUltrasonicBufferSize);

    // Tracks total current from kPDP
    public double shovelCurrentDraw;

    // Pose stores drivetrain sensor data
    public Pose drivePose = new Pose();

    // Pusher sensor data
    public double pusherPosition, pusherVelocity, pusherAppliedOutput;

    // Elevator sensor data
    public double elevatorPosition, elevatorVelocity, elevatorAppliedOutput;

    // Vision drive data
    public boolean atVisionTargetThreshold;

    // FPGATimestamp -> RigidTransform2d or Rotation2d
    private RigidTransform2d.Delta vehicleVelocity;
    private InterpolatingTreeMap<InterpolatingDouble, RigidTransform2d> fieldToVehicle;

    public void reset(double startTime, RigidTransform2d initialFieldToVehicle) {
        fieldToVehicle = new InterpolatingTreeMap<>(100);
        fieldToVehicle.put(new InterpolatingDouble(startTime), initialFieldToVehicle);
        vehicleVelocity = new RigidTransform2d.Delta(0, 0, 0);
    }

    public void resetUltrasonics() {
        for (CircularBuffer buffer : List.of(leftIntakeReadings, rightIntakeReadings, pusherReadings)) {
            for (int i = 0; i < kUltrasonicBufferSize; i++) {
                buffer.addFirst(Double.MAX_VALUE);
            }
        }
    }

    public RigidTransform2d getFieldToVehicle(double timestamp) {
        return fieldToVehicle.getInterpolated(new InterpolatingDouble(timestamp));
    }

    public Map.Entry<InterpolatingDouble, RigidTransform2d> getLatestFieldToVehicle() {
        return fieldToVehicle.lastEntry();
    }

    public RigidTransform2d getPredictedFieldToVehicle(double lookAheadTime) {
        return getLatestFieldToVehicle().getValue().transformBy(
                RigidTransform2d.fromVelocity(new RigidTransform2d.Delta(vehicleVelocity.dX * lookAheadTime, vehicleVelocity.dY * lookAheadTime, vehicleVelocity.dTheta * lookAheadTime)));
    }

    public void addFieldToVehicleObservation(double timestamp, RigidTransform2d observation) {
        fieldToVehicle.put(new InterpolatingDouble(timestamp), observation);
    }

    public void addObservations(double timestamp, RigidTransform2d fieldToVehicle, RigidTransform2d.Delta velocity) {
        addFieldToVehicleObservation(timestamp, fieldToVehicle);
        vehicleVelocity = velocity;
    }

    public RigidTransform2d generateOdometryFromSensors(double leftEncoderDelta, double rightEncoderDelta, Rotation2d gyroAngle) {
        RigidTransform2d lastMeasurement = getLatestFieldToVehicle().getValue();
        return Kinematics.integrateForwardKinematics(lastMeasurement, leftEncoderDelta, rightEncoderDelta, gyroAngle);
    }

    public int getNumObservations() {
        return fieldToVehicle.size();
    }
}
