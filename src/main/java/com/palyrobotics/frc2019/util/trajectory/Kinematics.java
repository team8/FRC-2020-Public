package com.palyrobotics.frc2019.util.trajectory;

import com.palyrobotics.frc2019.config.constants.DrivetrainConstants;

/**
 * Provides forward and inverse kinematics equations for the robot modeling the wheelbase as a differential drive (with a corrective factor to account for the
 * inherent skidding of the center 4 wheels quasi-kinematically).
 */

public class Kinematics {
    private static final double kEpsilon = 1E-9;

    public static class DriveVelocity {
        public final double left;
        public final double right;

        public DriveVelocity(double left, double right) {
            this.left = left;
            this.right = right;
        }
    }

    /**
     * Forward kinematics using only encoders, rotation is implicit (less accurate than below, but useful for predicting motion)
     */
    public static RigidTransform2d.Delta forwardKinematics(double leftWheelDelta, double rightWheelDelta) {
        double linearVelocity = (leftWheelDelta + rightWheelDelta) / 2;
        double velocityDelta = (rightWheelDelta - leftWheelDelta) / 2;
        double rotationDelta = velocityDelta * 2 * DrivetrainConstants.kTrackScrubFactor / DrivetrainConstants.kTrackEffectiveDiameter;
        return new RigidTransform2d.Delta(linearVelocity, 0, rotationDelta);
    }

    /**
     * Forward kinematics using encoders and explicitly measured rotation (ex. from gyro)
     */
    public static RigidTransform2d.Delta forwardKinematics(double leftWheelDelta, double rightWheelDelta, double deltaRotationRadians) {
        return new RigidTransform2d.Delta((leftWheelDelta + rightWheelDelta) / 2, 0, deltaRotationRadians);
    }

    /**
     * Forward kinematics from existing DriveVelocity
     */
    public static RigidTransform2d.Delta forwardKinematics(DriveVelocity velocity) {
        return forwardKinematics(velocity.left, velocity.right);
    }

    /**
     * Append the result of forward kinematics to a previous pose.
     */
    public static RigidTransform2d integrateForwardKinematics(RigidTransform2d currentPose, double leftWheelDelta, double rightWheelDelta,
                                                              Rotation2d currentHeading) {
        RigidTransform2d.Delta withGyro = forwardKinematics(leftWheelDelta, rightWheelDelta,
                currentPose.getRotation().inverse().rotateBy(currentHeading).getRadians());
        return currentPose.transformBy(RigidTransform2d.fromVelocity(withGyro));
    }

    public static DriveVelocity inverseKinematics(RigidTransform2d.Delta velocity) {
        if (Math.abs(velocity.dTheta) < kEpsilon) {
            return new DriveVelocity(velocity.dX, velocity.dX);
        }
        double velocityDelta = DrivetrainConstants.kTrackEffectiveDiameter * velocity.dTheta / (2 * DrivetrainConstants.kTrackScrubFactor);
        return new DriveVelocity(velocity.dX - velocityDelta, velocity.dX + velocityDelta);
    }
}