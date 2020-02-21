package com.palyrobotics.frc2020.util.control;

import static com.palyrobotics.frc2020.util.Util.clamp;

import edu.wpi.first.hal.util.BoundaryException;
import edu.wpi.first.wpilibj.Timer;

/**
 * This class implements a PID Control Loop.
 * <p>
 * Does all computation synchronously (i.e. the calculate() function must be called by the user from
 * their own thread)
 */
public class SynchronousPIDF {

	private double mP; // factor for "proportional" control
	private double mI; // factor for "integral" control
	private double mD; // factor for "derivative" control
	private double mF; // factor for feed forward gain
	private double mMaximumOutput = 1.0; // |maximum output|
	private double mMinimumOutput = -1.0; // |minimum output|
	private double mMaximumInput; // maximum input - limit setpoint to this
	private double mMinimumInput; // minimum input - limit setpoint to this
	private boolean mContinuous = false; // do the endpoints wrap around? eg. absolute encoder
	private double mLastError; // the prior sensor input (used to compute velocity)
	private double mTotalError; // the sum of the errors for use in the integral calc
	private double mSetPoint;
	private double mError;
	private double mResult;
	private double mLastInput = Double.NaN;
	private double mDeadBand; // If the absolute error is less than dead band then treat error for the proportional term as 0
	private double mLastTimestamp = Timer.getFPGATimestamp();

	public SynchronousPIDF() {
	}

	/**
	 * Allocate a PID object with the given constants for P, I, D
	 *
	 * @param p the proportional coefficient
	 * @param i the integral coefficient
	 * @param d the derivative coefficient
	 */
	public SynchronousPIDF(double p, double i, double d) {
		setPIDF(p, i, d, 0.0);
	}

	/**
	 * Allocate a PID object with the given constants for P, I, D
	 *
	 * @param p the proportional coefficient
	 * @param i the integral coefficient
	 * @param d the derivative coefficient
	 * @param f the feed forward gain coefficient
	 */
	public SynchronousPIDF(double p, double i, double d, double f) {
		setPIDF(p, i, d, f);
	}

	/**
	 * Checks if the given input is within the range (min, max), both exclusive.
	 */
	public static boolean inRange(double v, double min, double max) {
		return v > min && v < max;
	}

	public double getDt() {
		double timestamp = Timer.getFPGATimestamp();
		double dt = timestamp - mLastTimestamp;
		mLastTimestamp = timestamp;
		return Math.max(dt, 1e-6);
	}

	public double calculate(double input) {
		double dt = getDt();
		return calculate(input, (mError - mLastError) / dt, dt);
	}

	public double calculate(double input, double inputDerivative) {
		return calculate(input, inputDerivative, getDt());
	}

	/**
	 * Read the input, calculate the output accordingly, and write to the output. This should be called
	 * at a constant rate by the user (ex. in a timed thread)
	 */
	public double calculate(double input, double inputDerivative, double dt) {
		mLastInput = input;
		mError = mSetPoint - input;
		if (mContinuous) {
			if (Math.abs(mError) > (mMaximumInput - mMinimumInput) / 2) {
				if (mError > 0) {
					mError = mError - mMaximumInput + mMinimumInput;
				} else {
					mError = mError + mMaximumInput - mMinimumInput;
				}
			}
		}

		if (inRange(mError * mP, mMinimumOutput, mMaximumOutput)) {
			mTotalError += mError * dt;
		} else {
			mTotalError = 0;
		}

		// Don't blow away m_error so as to not break derivative
		double proportionalError = Math.abs(mError) < mDeadBand ? 0 : mError;

		mResult = (mP * proportionalError + mI * mTotalError + mD * inputDerivative + mF * mSetPoint);
		mLastError = mError;

		return clamp(mResult, mMinimumOutput, mMaximumOutput);
	}

	/**
	 * Set the PID controller gain parameters. Set the proportional, integral, and differential
	 * coefficients.
	 *
	 * @param p Proportional coefficient
	 * @param i Integral coefficient
	 * @param d Differential coefficient
	 */
	public void setPID(double p, double i, double d) {
		mP = p;
		mI = i;
		mD = d;
	}

	/**
	 * Set the PID controller gain parameters. Set the proportional, integral, and differential
	 * coefficients.
	 *
	 * @param p Proportional coefficient
	 * @param i Integral coefficient
	 * @param d Differential coefficient
	 * @param f Feed forward coefficient
	 */
	public void setPIDF(double p, double i, double d, double f) {
		setPID(p, i, d);
		mF = f;
	}

	/**
	 * Get the Proportional coefficient
	 *
	 * @return proportional coefficient
	 */
	public double getP() {
		return mP;
	}

	/**
	 * Get the Integral coefficient
	 *
	 * @return integral coefficient
	 */
	public double getI() {
		return mI;
	}

	/**
	 * Get the Differential coefficient
	 *
	 * @return differential coefficient
	 */
	public double getD() {
		return mD;
	}

	/**
	 * Get the Feed forward coefficient
	 *
	 * @return feed forward coefficient
	 */
	public double getF() {
		return mF;
	}

	/**
	 * Return the current PID result This is always centered on zero and constrained the the max and min
	 * outs
	 *
	 * @return the latest calculated output
	 */
	public double get() {
		return mResult;
	}

	/**
	 * Set the PID controller to consider the input to be continuous, Rather then using the max and min
	 * in as constraints, it considers them to be the same point and automatically calculates the
	 * shortest route to the setpoint.
	 *
	 * @param continuous Set to true turns on continuous, false turns off continuous
	 */
	public void setContinuous(boolean continuous) {
		mContinuous = continuous;
	}

	public void setDeadBand(double deadBand) {
		mDeadBand = deadBand;
	}

	/**
	 * Set the PID controller to consider the input to be continuous, Rather then using the max and min
	 * in as constraints, it considers them to be the same point and automatically calculates the
	 * shortest route to the setpoint.
	 */
	public void setContinuous() {
		this.setContinuous(true);
	}

	/**
	 * Sets the maximum and minimum values expected from the input.
	 *
	 * @param minimumInput the minimum value expected from the input
	 * @param maximumInput the maximum value expected from the output
	 */
	public void setInputRange(double minimumInput, double maximumInput) {
		if (minimumInput > maximumInput) {
			throw new BoundaryException("Lower bound is greater than upper bound");
		}
		mMinimumInput = minimumInput;
		mMaximumInput = maximumInput;
		setSetpoint(mSetPoint);
	}

	/**
	 * Sets the minimum and maximum values to write.
	 *
	 * @param minimumOutput the minimum value to write to the output
	 * @param maximumOutput the maximum value to write to the output
	 */
	public void setOutputRange(double minimumOutput, double maximumOutput) {
		if (minimumOutput > maximumOutput) {
			throw new BoundaryException("Lower bound is greater than upper bound");
		}
		mMinimumOutput = minimumOutput;
		mMaximumOutput = maximumOutput;
	}

	public void setMaxAbsoluteOutput(double maxAbsoluteOutput) {
		setOutputRange(-maxAbsoluteOutput, maxAbsoluteOutput);
	}

	/**
	 * Set the setpoint for the PID controller
	 *
	 * @param setpoint the desired setpoint
	 */
	public void setSetpoint(double setpoint) {
		if (mMaximumInput > mMinimumInput) {
			mSetPoint = clamp(mSetPoint, mMinimumInput, mMaximumInput);
		} else {
			mSetPoint = setpoint;
		}
	}

	/**
	 * Returns the current setpoint of the PID controller
	 *
	 * @return the current setpoint
	 */
	public double getSetpoint() {
		return mSetPoint;
	}

	/**
	 * Returns the current difference of the input from the setpoint
	 *
	 * @return the current error
	 */
	public double getError() {
		return mError;
	}

	/**
	 * Return true if the error is within the tolerance
	 *
	 * @return true if the error is less than the tolerance
	 */
	public boolean onTarget(double tolerance) {
		return !Double.isNaN(mLastInput) && Math.abs(mLastInput - mSetPoint) < tolerance;
	}

	/**
	 * Reset all internal terms.
	 */
	public void reset() {
		mLastInput = Double.NaN;
		mLastError = 0;
		mTotalError = 0;
		mResult = 0;
		mSetPoint = 0;
	}

	public void resetIntegrator() {
		mTotalError = 0;
	}
}
