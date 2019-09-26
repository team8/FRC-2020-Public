package com.palyrobotics.frc2019.util;

/**
 * Class implements a PID Control SubsystemLoop.
 * <p>
 * Does all computation synchronously (i.e. the calculate() function must be called by the user from his own thread)
 */
public class SynchronousPID {
    private double mP; //factor for "proportional" control
    private double mI; //factor for "integral" control
    private double mIZone; //error needs to be within iZone to kick in "integral" control
    private double mD; //factor for "derivative" control
    private double mMaximumOutput = 1.0; //|maximum output|
    private double mMinimumOutput = -1.0; //|minimum output|
    private double mMaximumInput = 0.0; //maximum input - limit setpoint to this
    private double mMinimumInput = 0.0; //minimum input - limit setpoint to this
    private boolean mContinuous = false; //do the endpoints wrap around? eg. Absolute encoder
    private double mPrevError = 0.0; //the prior sensor input (used to compute velocity)
    private double mTotalError = 0.0; //the sum of the errors for use in the integral calc
    private double mSetpoint = 0.0;
    private double mError = 0.0;
    private double mResult = 0.0;
    private double mLastInput = Double.NaN;

    public SynchronousPID() {
    }

    /**
     * Allocate a PID object with the given constants for P, I, D
     *
     * @param p the proportional coefficient
     * @param i the integral coefficient
     * @param d the derivative coefficient
     */
    public SynchronousPID(double p, double i, double d) {
        mP = p;
        mI = i;
        mD = d;
        mIZone = Double.MAX_VALUE;
    }

    /**
     * Allocate a PID object with the given constants for P, I, D
     *
     * @param p the proportional coefficient
     * @param i the integral coefficient
     * @param d the derivative coefficient
     */
    public SynchronousPID(double p, double i, double d, double iZone) {
        mP = p;
        mI = i;
        mD = d;
        mIZone = iZone;
    }

    /**
     * Read the input, calculate the output accordingly, and write to the output. This should be called at a constant rate by the user (ex. in a timed thread)
     *
     * @param input the input
     */
    public double calculate(double input) {
        mLastInput = input;
        mError = mSetpoint - input;
        if (mContinuous) {
            if (Math.abs(mError) > (mMaximumInput - mMinimumInput) / 2) {
                if (mError > 0) {
                    mError = mError - mMaximumInput + mMinimumInput;
                } else {
                    mError = mError + mMaximumInput - mMinimumInput;
                }
            }
        }

        if ((mError * mP < mMaximumOutput) && (mError * mP > mMinimumOutput) && mError < mIZone) {
            mTotalError += mError;
        } else {
            mTotalError = 0;
        }

        mResult = (mP * mError + mI * mTotalError + mD * (mError - mPrevError));
        mPrevError = mError;

        if (mResult > mMaximumOutput) {
            mResult = mMaximumOutput;
        } else if (mResult < mMinimumOutput) {
            mResult = mMinimumOutput;
        }
        return mResult;
    }

    /**
     * Set the PID controller gain parameters. Set the proportional, integral, and differential coefficients.
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
     * Return the current PID result This is always centered on zero and constrained the the max and min outs
     *
     * @return the latest calculated output
     */
    public double get() {
        return mResult;
    }

    /**
     * Set the PID controller to consider the input to be continuous, Rather then using the max and min in as constraints, it considers them to be the same
     * point and automatically calculates the shortest route to the setpoint.
     *
     * @param continuous Set to true turns on continuous, false turns off continuous
     */
    public void setContinuous(boolean continuous) {
        mContinuous = continuous;
    }

    /**
     * Set the PID controller to consider the input to be continuous, Rather then using the max and min in as constraints, it considers them to be the same
     * point and automatically calculates the shortest route to the setpoint.
     */
    public void setContinuous() {
        setContinuous(true);
    }

    /**
     * Sets the maximum and minimum values expected from the input.
     *
     * @param minimumInput the minimum value expected from the input
     * @param maximumInput the maximum value expected from the output
     */
    public void setInputRange(double minimumInput, double maximumInput) {
        if (minimumInput > maximumInput) {
            throw new IllegalArgumentException("Lower bound is greater than upper bound");
        }
        mMinimumInput = minimumInput;
        mMaximumInput = maximumInput;
        setSetpoint(mSetpoint);
    }

    /**
     * Sets the minimum and maximum values to write.
     *
     * @param minimumOutput the minimum value to write to the output
     * @param maximumOutput the maximum value to write to the output
     */
    public void setOutputRange(double minimumOutput, double maximumOutput) {
        if (minimumOutput > maximumOutput) {
            throw new IllegalArgumentException("Lower bound is greater than upper bound");
        }
        mMinimumOutput = minimumOutput;
        mMaximumOutput = maximumOutput;
    }

    /**
     * Set the setpoint for the PID controller
     *
     * @param setpoint the desired setpoint
     */
    public void setSetpoint(double setpoint) {
        if (mMaximumInput > mMinimumInput) {
            if (setpoint > mMaximumInput) {
                mSetpoint = mMaximumInput;
            } else {
                mSetpoint = Math.max(setpoint, mMinimumInput);
            }
        } else {
            mSetpoint = setpoint;
        }
    }

    /**
     * Returns the current setpoint of the PID controller
     *
     * @return the current setpoint
     */
    public double getSetpoint() {
        return mSetpoint;
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
        return !Double.isNaN(mLastInput) && Math.abs(mLastInput - mSetpoint) < tolerance;
    }

    /**
     * Reset all internal terms.
     */
    public void reset() {
        mLastInput = Double.NaN;
        mPrevError = 0;
        mTotalError = 0;
        mResult = 0;
        mSetpoint = 0;
    }

    public void resetIntegrator() {
        mTotalError = 0;
    }

    public String getType() {
        return "PIDController";
    }
}
