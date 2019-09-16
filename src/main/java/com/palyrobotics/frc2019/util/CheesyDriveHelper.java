package com.palyrobotics.frc2019.util;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.Constants.DrivetrainConstants;
import com.palyrobotics.frc2019.config.Constants.OtherConstants;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.subsystems.Drive;

/**
 * CheesyDriveHelper implements the calculations used in CheesyDrive for teleop control. Returns a DriveSignal for the motor output
 */
public class CheesyDriveHelper {
	private double mOldWheel, mQuickStopAccumulator;
	private boolean mInitialBrake;
	private double mOldThrottle = 0.0, mBrakeRate;

	public SparkSignal cheesyDrive(Commands commands, RobotState robotState) {
		double throttle = -robotState.leftStickInput.getY();
		double wheel = robotState.rightStickInput.getX();

		if (commands.wantedDriveState == Drive.DriveState.CHEZY) {
			wheel = wheel * .75;
		}
//
//		SparkMaxOutput c = new SparkMaxOutput();
//		c.setPercentOutput(throttle);
//
////		mSignal.leftMotor.setPercentOutput(throttle);
////		mSignal.rightMotor.setPercentOutput(throttle);
//
//		return new SparkSignal(c,c);

		//Quickturn if right trigger is pressed
		boolean isQuickTurn = robotState.rightStickInput.getTriggerPressed();
		robotState.isQuickTurning = isQuickTurn;

		//Braking if left trigger is pressed
		boolean isBraking = robotState.leftStickInput.getTriggerPressed();

		double wheelNonLinearity;

		wheel = ChezyMath.handleDeadband(wheel, DrivetrainConstants.kDeadband);
		throttle = ChezyMath.handleDeadband(throttle, DrivetrainConstants.kDeadband);

		double negInertia = wheel - mOldWheel;
		mOldWheel = wheel;

		wheelNonLinearity = 0.5;

		//Applies a sin function that is scaled
		wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / Math.sin(Math.PI / 2.0 * wheelNonLinearity);
		wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / Math.sin(Math.PI / 2.0 * wheelNonLinearity);
		wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / Math.sin(Math.PI / 2.0 * wheelNonLinearity);

		double leftPower, rightPower, overPower;
		double sensitivity;

		double angularPower;

		//linear power is what's actually sent to motor, throttle is input
		double linearPower = throttle;

		//Negative inertia
		double negInertiaAccumulator = 0.0;
		double negInertiaScalar;

		if(wheel * negInertia > 0) {
			negInertiaScalar = 2.5;
		} else {
			if(Math.abs(wheel) > 0.65) {
				negInertiaScalar = 5.0;
			} else {
				negInertiaScalar = 3.0;
			}
		}

		sensitivity = DrivetrainConstants.kDriveSensitivity;

		//neginertia is difference in wheel
		double negInertiaPower = negInertia * negInertiaScalar;
		negInertiaAccumulator += negInertiaPower;

		//possible source of occasional overturn
		wheel = wheel + negInertiaAccumulator;

		// //Handle braking
		// if(isBraking) {
		// 	//Set up braking rates for linear deceleration in a set amount of time
		// 	if(mInitialBrake) {
		// 		mInitialBrake = false;
		// 		//Old throttle initially set to throttle
		// 		mOldThrottle = linearPower;
		// 		//Braking rate set
		// 		mBrakeRate = mOldThrottle / DrivetrainConstants.kCyclesUntilStop;
		// 	}

		// 	//If braking is not complete, decrease by the brake rate
		// 	if(Math.abs(mOldThrottle) >= Math.abs(mBrakeRate)) {
		// 		//reduce throttle
		// 		mOldThrottle -= mBrakeRate;
		// 		linearPower = mOldThrottle;
		// 	} else {
		// 		linearPower = 0;
		// 	}
		// } else {
		// 	mInitialBrake = true;
		// }

		//Quickturn
		if(isQuickTurn) {
			if(Math.abs(robotState.rightStickInput.getX()) < DrivetrainConstants.kQuickTurnSensitivityThreshold) {
				sensitivity = DrivetrainConstants.kPreciseQuickTurnSensitivity;
			} else {
				sensitivity = DrivetrainConstants.kQuickTurnSensitivity;
			}

			angularPower = wheel * sensitivity;

			//Can be tuned
			double alpha = DrivetrainConstants.kAlpha;
			mQuickStopAccumulator = (1 - alpha) * mQuickStopAccumulator + alpha * angularPower * 6.5;

			overPower = 1.0;
		} else {
			overPower = 0.0;

			//Sets turn amount
			angularPower = Math.abs(throttle) * wheel - mQuickStopAccumulator;

			if(mQuickStopAccumulator > DrivetrainConstants.kQuickStopAccumulatorDecreaseThreshold) {
				mQuickStopAccumulator -= DrivetrainConstants.kQuickStopAccumulatorDecreaseRate;
			} else if(mQuickStopAccumulator < -DrivetrainConstants.kQuickStopAccumulatorDecreaseThreshold) {
				mQuickStopAccumulator += DrivetrainConstants.kQuickStopAccumulatorDecreaseRate;
			} else {
				mQuickStopAccumulator = 0.0;
			}
		}

		rightPower = leftPower = mOldThrottle = linearPower;
		leftPower += angularPower;
		rightPower -= angularPower;

		if(leftPower > 1.0) {
			rightPower -= overPower * (leftPower - 1.0);
			leftPower = 1.0;
		} else if(rightPower > 1.0) {
			leftPower -= overPower * (rightPower - 1.0);
			rightPower = 1.0;
		} else if(leftPower < -1.0) {
			rightPower += overPower * (-1.0 - leftPower);
			leftPower = -1.0;
		} else if(rightPower < -1.0) {
			leftPower += overPower * (-1.0 - rightPower);
			rightPower = -1.0;
		}

		SparkSignal mSignal = SparkSignal.getNeutralSignal();

		mSignal.leftMotor.setPercentOutput(leftPower);
		mSignal.rightMotor.setPercentOutput(rightPower);
		return mSignal;
	}

	/**
	 * Throttle tuning functions
	 */
	public double remapThrottle(double initialThrottle) {
		double x = Math.abs(initialThrottle);
		switch(OtherConstants.kDriverName) {
			case BRYAN:
				//Reversal of directions
				//Stick a 0 cycle in between
				if(initialThrottle * mOldThrottle < 0) {
					return 0.0;
				}

				//Increase in magnitude, deceleration is fine. This misses rapid direction switches, but that's up to driver
				if(x > Math.abs(mOldThrottle)) {
					x = mOldThrottle + Math.signum(initialThrottle) * DrivetrainConstants.kMaxAccelRate;
				} else {
					x = initialThrottle;
				}

//				x = initialThrottle;
				break;
		}
		return x;
	}

	/**
	 * Limits the given input to the given magnitude.
	 */
	public double limit(double v, double limit) {
		return (Math.abs(v) < limit) ? v : limit * (v < 0 ? -1 : 1);
	}
}