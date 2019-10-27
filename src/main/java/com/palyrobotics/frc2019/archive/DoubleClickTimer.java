//package com.palyrobotics.frc2019.archive;
//
//import edu.wpi.first.wpilibj.Timer;
//
///**
// * Double Click Timer
// *
// * @author Ailyn Tong Differentiates between a single and double click. A tolerance window ensures a double click isn't registered immediately after a single
// *         click due to fast update times. A WaitTimer ensures a double click can be acted upon and not immediately overridden by a single click.
// *
// *         Everything is handled by twice() for ease of use
// */
//public class DoubleClickTimer {
//	//Default Values in seconds
//	public static final double DEFAULT_MIN_TOLERANCE = 0.1;
//	public static final double DEFAULT_MAX_TOLERANCE = 0.5;
//	public static final double DEFAULT_WAIT_TIME = 0.5;
//
//	//Seconds
//	private double kMinTolerance, kMaxTolerance, kWaitTime;
//
//	private double start, now;
//	private boolean twice = false;
//
//	private WaitTimer wait;
//
//	/**
//	 * Default Constructor
//	 */
//	public DoubleClickTimer() {
//		kMinTolerance = DEFAULT_MIN_TOLERANCE;
//		kMaxTolerance = DEFAULT_MAX_TOLERANCE;
//		kWaitTime = DEFAULT_WAIT_TIME;
//
//		reset();
//		wait = new WaitTimer(kWaitTime);
//	}
//
//	/**
//	 * Constructor
//	 *
//	 * @param minTolerance
//	 *            Lower limit for registering double click
//	 * @param maxTolerance
//	 *            Upper limit for registering double click
//	 */
//	public DoubleClickTimer(double minTolerance, double maxTolerance) {
//		kMinTolerance = minTolerance;
//		kMaxTolerance = maxTolerance;
//		kWaitTime = DEFAULT_WAIT_TIME;
//
//		reset();
//		wait = new WaitTimer(kWaitTime);
//	}
//
//	/**
//	 * Constructor
//	 *
//	 * @param minTolerance
//	 *            Lower limit for registering double click
//	 * @param maxTolerance
//	 *            Upper limit for registering double click
//	 * @param waitTime
//	 *            Cooldown time before double click can be registered again
//	 */
//	public DoubleClickTimer(double minTolerance, double maxTolerance, double waitTime) {
//		kMinTolerance = minTolerance;
//		kMaxTolerance = maxTolerance;
//		kWaitTime = waitTime;
//
//		reset();
//		wait = new WaitTimer(waitTime);
//	}
//
//	/**
//	 * Resets time variables
//	 */
//	private void reset() {
//		start = -1;
//		now = -1;
//	}
//
//	/**
//	 * Starts the timer if not already started Otherwise record the current time
//	 */
//	private void register() {
//		if(start == -1)
//			start = Timer.getFPGATimestamp();
//		else
//			now = Timer.getFPGATimestamp();
//	}
//
//	/**
//	 * Checks if double click has been registered Also handles reset and wait
//	 *
//	 * @return true if double click, false otherwise
//	 */
//	public boolean twice() {
//		/*
//		 * If a double click has already been registered, wait timer will override double click timer
//		 */
//		if(twice) {
//			if(wait.timeout()) {
//				twice = false;
//				return false;
//			} else
//				return true;
//		}
//
//		register();
//
//		//Reset timer if tolerance was passed
//		if(now - start > kMaxTolerance) {
//			reset();
//			twice = false;
//		}
//		//Register as double click if within tolerance
//		else if(now - start >= kMinTolerance && now - start <= kMaxTolerance) {
//			reset();
//			twice = true;
//		}
//		//Register as single click
//		else
//			twice = false;
//
//		return twice;
//	}
//}