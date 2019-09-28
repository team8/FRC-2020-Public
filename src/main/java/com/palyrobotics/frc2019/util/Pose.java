package com.palyrobotics.frc2019.util;

import java.util.Optional;

/**
 * Created by Nihar on 2/12/17. Represents the drivetrain state <br />
 * Holds sensor data through CANTalon and gyroscope <br />
 * Optional used for values that may not always be present
 */
public class Pose {
	public double heading;
	public double lastHeading;
	public double headingVelocity;

	public double leftEnc;
	public double lastLeftEnc;
	public double leftEncVelocity;

	public double rightEnc;
	public double lastRightEnc;
	public double rightEncVelocity;

	public Optional<Integer> leftError;
	public Optional<Integer> rightError;

	public Optional<Integer> leftMotionMagicPos = Optional.empty();
	public Optional<Integer> rightMotionMagicPos = Optional.empty();
	public Optional<Integer> leftMotionMagicVel = Optional.empty();
	public Optional<Integer> rightMotionMagicVel = Optional.empty();

	public Pose() {
		this.leftEnc = 0;
		this.lastLeftEnc = 0;
		this.leftEncVelocity = 0;
		this.rightEnc = 0;
		this.lastRightEnc = 0;
		this.rightEncVelocity = 0;
		this.heading = 0;
		this.lastHeading = 0;
		this.headingVelocity = 0;
		this.leftError = Optional.empty();
		this.rightError = Optional.empty();
	}

	public Pose(double leftEnc, double lastLeftEnc, double leftEncVelocity, double rightEnc, double lastRightEnc, double rightEncVelocity, int leftError,
			int rightError, double heading, double headingVelocity) {
		this.leftEnc = leftEnc;
		this.lastLeftEnc = lastLeftEnc;
		this.leftEncVelocity = leftEncVelocity;
		this.rightEnc = rightEnc;
		this.lastRightEnc = lastRightEnc;
		this.rightEncVelocity = rightEncVelocity;
		this.heading = heading;
		this.headingVelocity = headingVelocity;
		this.leftError = Optional.of(leftError);
		this.rightError = Optional.of(rightError);
	}

	public Pose(double leftEnc, double lastLeftEnc, double leftEncVelocity, double rightEnc, double lastRightEnc, double rightEncVelocity, double heading,
			double headingVelocity) {
		this.leftEnc = leftEnc;
		this.lastLeftEnc = lastLeftEnc;
		this.leftEncVelocity = leftEncVelocity;
		this.rightEnc = rightEnc;
		this.lastRightEnc = lastRightEnc;
		this.rightEncVelocity = rightEncVelocity;
		this.heading = heading;
		this.headingVelocity = headingVelocity;
		this.leftError = Optional.empty();
		this.rightError = Optional.empty();
	}

	//TODO: Copy and equals methods
	public Pose copy() {
		Pose copy = new Pose();
		copy.leftEnc = this.leftEnc;
		copy.lastLeftEnc = this.lastLeftEnc;
		copy.leftEncVelocity = this.leftEncVelocity;
		copy.heading = this.heading;
		copy.lastHeading = this.lastHeading;
		copy.headingVelocity = this.headingVelocity;
		copy.rightEnc = this.rightEnc;
		copy.lastRightEnc = this.lastRightEnc;
		copy.rightEncVelocity = this.rightEncVelocity;
		copy.leftError = this.leftError;
		copy.rightError = this.rightError;
		copy.leftMotionMagicPos = this.leftMotionMagicPos;
		copy.rightMotionMagicPos = this.rightMotionMagicPos;
		copy.leftMotionMagicVel = this.leftMotionMagicVel;
		copy.rightMotionMagicVel = this.rightMotionMagicVel;

		return copy;
	}

	public boolean equals(Pose other) {
		return this.leftEnc == other.leftEnc && this.lastLeftEnc == other.lastLeftEnc && this.leftEncVelocity == other.leftEncVelocity
				&& this.rightEnc == other.rightEnc && this.lastRightEnc == other.lastRightEnc && this.rightEncVelocity == other.rightEncVelocity
				&& this.leftError.equals(other.leftError) && this.rightError.equals(other.rightError) && this.heading == other.heading
				&& this.lastHeading == other.lastHeading && this.headingVelocity == other.headingVelocity && this.leftMotionMagicPos == other.leftMotionMagicPos
				&& this.rightMotionMagicPos == other.rightMotionMagicPos && this.leftMotionMagicVel == other.leftMotionMagicVel
				&& this.rightMotionMagicVel == other.rightMotionMagicVel;
	}
}