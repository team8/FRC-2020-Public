//package com.palyrobotics.frc2019.archive;
//
//import edu.wpi.first.wpilibj.Timer;
//
///**
// * Wait Timer
// *
// * @author Ailyn Tong Checks if a certain amount of time has elapsed. Uses FPGA Timer Everything is handled by timeout() for ease of use.
// */
//public class WaitTimer {
//	//Milliseconds
//	private double kWaitTime;
//	private double t_0, t_f;
//
//	/**
//	 * Constructor
//	 *
//	 * @param waitTime
//	 *            Desired amount of time elapsed in seconds
//	 */
//	public WaitTimer(double waitTime) {
//		kWaitTime = waitTime;
//		reset();
//	}
//
//	/**
//	 * Resets time variables
//	 */
//	public void reset() {
//		t_0 = -1;
//		t_f = -1;
//	}
//
//	/**
//	 * Starts the timer if not already started Otherwise record the current time
//	 */
//	public void register() {
//		if(t_0 == -1)
//			t_0 = Timer.getFPGATimestamp();
//		else
//			t_f = Timer.getFPGATimestamp();
//	}
//
//	/**
//	 * @return True if the correct amount of time has elapsed
//	 */
//	public boolean timeout() {
//		register();
//
//		//Pass if nothing has been registered
//		if(t_0 == -1 || t_f == -1)
//			return false;
//		//Reset timer if done
//		if(t_f - t_0 >= kWaitTime) {
//			reset();
//			return true;
//		} else
//			return false;
//	}
//}