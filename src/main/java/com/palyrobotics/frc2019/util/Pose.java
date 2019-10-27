package com.palyrobotics.frc2019.util;

import java.util.Objects;

/**
 * Represents the translational and rotational state of the robot.
 */
public class Pose {
	public double heading;
	public double lastHeading;
	public double headingVelocity;

	public double leftEncoderPosition, lastLeftEncoderPosition;
	public double leftEncoderVelocity, lastRightEncoderPosition;

	public double rightEncoderPosition;
	public double rightEncoderVelocity;

	public void copyTo(Pose other) {
		other.heading = this.heading;
		other.lastHeading = this.lastHeading;
		other.headingVelocity = this.headingVelocity;
		other.leftEncoderPosition = this.leftEncoderPosition;
		other.leftEncoderVelocity = this.leftEncoderVelocity;
		other.rightEncoderPosition = this.rightEncoderPosition;
		other.rightEncoderVelocity = this.rightEncoderVelocity;
	}

	@Override // Auto-generated
	public boolean equals(Object other) {
		if (this == other) return true;
		if (other == null || getClass() != other.getClass()) return false;
		Pose otherPose = (Pose) other;
		return Double.compare(otherPose.heading, heading) == 0 &&
				Double.compare(otherPose.lastHeading, lastHeading) == 0 &&
				Double.compare(otherPose.headingVelocity, headingVelocity) == 0 &&
				Double.compare(otherPose.leftEncoderPosition, leftEncoderPosition) == 0 &&
				Double.compare(otherPose.lastLeftEncoderPosition, lastLeftEncoderPosition) == 0 &&
				Double.compare(otherPose.leftEncoderVelocity, leftEncoderVelocity) == 0 &&
				Double.compare(otherPose.lastRightEncoderPosition, lastRightEncoderPosition) == 0 &&
				Double.compare(otherPose.rightEncoderPosition, rightEncoderPosition) == 0 &&
				Double.compare(otherPose.rightEncoderVelocity, rightEncoderVelocity) == 0;
	}

	@Override // Auto-generated
	public int hashCode() {
		return Objects.hash(heading, lastHeading, headingVelocity, leftEncoderPosition, lastLeftEncoderPosition, leftEncoderVelocity, lastRightEncoderPosition, rightEncoderPosition, rightEncoderVelocity);
	}

	@Override // Auto-generated
	public String toString() {
		return String.format("Pose{heading=%s, lastHeading=%s, headingVelocity=%s, leftEncoderPosition=%s, lastLeftEncoderPosition=%s, leftEncoderVelocity=%s, lastRightEncoderPosition=%s, rightEncoderPosition=%s, rightEncoderVelocity=%s}", heading, lastHeading, headingVelocity, leftEncoderPosition, lastLeftEncoderPosition, leftEncoderVelocity, lastRightEncoderPosition, rightEncoderPosition, rightEncoderVelocity);
	}
}