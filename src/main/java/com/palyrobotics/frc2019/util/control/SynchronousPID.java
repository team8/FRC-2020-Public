package com.palyrobotics.frc2019.util.control;

/**
 * Class implements a PID Control SubsystemLoop.
 * <p>
 * Does all computation synchronously (i.e. the calculate() function must be called by the user from his own thread)
 */
public class SynchronousPID {
    private double
            mP, //factor for "proportional" control
            mI, //factor for "integral" control
            mIZone, //error needs to be within iZone to kick in "integral" control
            mD, //factor for "derivative" control
            mMaximumOutput = 1.0, //|maximum output|
            mMinimumOutput = -1.0, //|minimum output|
            mMaximumInput, //maximum input - limit set point to this
            mMinimumInput; //minimum input - limit set point to this
    private boolean mContinuous = false; //do the endpoints wrap around? eg. Absolute encoder
    private double
            mPrevError, //the prior sensor input (used to compute velocity)
            mTotalError, //the sum of the errors for use in the integral calc
            mSetPoint,
            mError,
            mResult,
            mLastInput = Double.NaN;

    public SynchronousPID(Gains gains) {
        this(gains.p, gains.i, gains.d, gains.iZone);
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
     * point and automatically calculates the shortest route to the set point.
     *
     * @param continuous Set to true turns on continuous, false turns off continuous
     */
    public void setContinuous(boolean continuous) {
        mContinuous = continuous;
    }

    /**
     * Set the PID controller to consider the input to be continuous, Rather then using the max and min in as constraints, it considers them to be the same
     * point and automatically calculates the shortest route to the set point.
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
        setSetPoint(mSetPoint);
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
     * Set the set point for the PID controller
     *
     * @param setPoint the desired set point
     */
    public void setSetPoint(double setPoint) {
        if (mMaximumInput > mMinimumInput) {
            if (setPoint > mMaximumInput) {
                mSetPoint = mMaximumInput;
            } else {
                mSetPoint = Math.max(setPoint, mMinimumInput);
            }
        } else {
            mSetPoint = setPoint;
        }
    }

    /**
     * Returns the current set point of the PID controller
     *
     * @return the current set point
     */
    public double getSetPoint() {
        return mSetPoint;
    }

    /**
     * Returns the current difference of the input from the set point
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
        mPrevError = 0;
        mTotalError = 0;
        mResult = 0;
        mSetPoint = 0;
    }

    public void resetIntegrator() {
        mTotalError = 0;
    }

    public String getType() {
        return "PIDController";
    }
}
