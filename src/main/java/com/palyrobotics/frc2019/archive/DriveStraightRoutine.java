//package com.palyrobotics.frc2019.behavior.routines.drive;
//
//import com.palyrobotics.frc2019.behavior.Routine;
//import com.palyrobotics.frc2019.config.Commands;
//import com.palyrobotics.frc2019.subsystems.Drive;
//import com.palyrobotics.frc2019.subsystems.Subsystem;
//
//public class DriveStraightRoutine extends Routine {
//
//	private double distance;
//
//	public DriveStraightRoutine(double distance) {
//		this.distance = distance;
//	}
//
//	@Override
//	public Subsystem[] getRequiredSubsystems() {
//		return new Subsystem[] {mDrive};
//	}
//
//	/*
//	 * START = Set new drive set point DRIVING = Waiting to reach drive set point DONE = reached target or not operating
//	 */
//	private enum DriveStraightRoutineState {
//		START, DRIVING, DONE
//	}
//
//	private DriveStraightRoutineState mState = DriveStraightRoutineState.START;
//
//	@Override
//	public void start() {
//		mDrive.setNeutral();
//		mState = DriveStraightRoutineState.START;
//	}
//
//	@Override
//	public Commands update(Commands commands) {
//		switch(mState) {
//			case START:
//				mDrive.setDriveStraight(distance);
//				commands.wantedDriveState = Drive.DriveState.ON_BOARD_CONTROLLER;
//				mState = DriveStraightRoutineState.DRIVING;
//				break;
//			case DRIVING:
//				commands.wantedDriveState = Drive.DriveState.ON_BOARD_CONTROLLER;
//				if(mDrive.controllerOnTarget() && mDrive.hasController()) {
//					mState = DriveStraightRoutineState.DONE;
//				}
//				break;
//			case DONE:
//				mDrive.resetController();
//				break;
//			default:
//				break;
//		}
//		return commands;
//	}
//
//	@Override
//	public Commands cancel(Commands commands) {
//		mState = DriveStraightRoutineState.DONE;
//		commands.wantedDriveState = Drive.DriveState.NEUTRAL;
//		mDrive.resetController();
//		return commands;
//	}
//
//	@Override
//	public boolean finished() {
//		return mState == DriveStraightRoutineState.DONE;
//	}
//
//	@Override
//	public String getName() {
//		return "DriveStraightRoutine";
//	}
//
//}
