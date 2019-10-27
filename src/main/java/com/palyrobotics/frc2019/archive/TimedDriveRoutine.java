//package com.palyrobotics.frc2019.behavior.routines.drive;
//
//import com.palyrobotics.frc2019.behavior.Routine;
//import com.palyrobotics.frc2019.config.Commands;
//import com.palyrobotics.frc2019.subsystems.Drive;
//import com.palyrobotics.frc2019.subsystems.Subsystem;
//
//public class TimedDriveRoutine extends Routine {
//
//    private double voltage;
//    private double time;
//
//    public TimedDriveRoutine(double voltage, double time) {
//        this.voltage = voltage;
//        this.time = time;
//    }
//
//    @Override
//    public Subsystem[] getRequiredSubsystems() {
//        return new Subsystem[]{mDrive};
//    }
//
//    /*
//     * START = Set new drive set point DRIVING = Waiting to reach drive set point DONE = reached target or not operating
//     */
//    private enum DriveStraightRoutineState {
//        START, DRIVING, DONE
//    }
//
//    DriveStraightRoutineState state = DriveStraightRoutineState.START;
//
//    @Override
//    public void start() {
//        mDrive.setNeutral();
//        state = DriveStraightRoutineState.START;
//    }
//
//    @Override
//    public Commands update(Commands commands) {
//        switch (state) {
//            case START:
//                mDrive.setTimedDrive(voltage, time);
//                commands.wantedDriveState = Drive.DriveState.ON_BOARD_CONTROLLER;
//                state = DriveStraightRoutineState.DRIVING;
//                break;
//            case DRIVING:
//                commands.wantedDriveState = Drive.DriveState.ON_BOARD_CONTROLLER;
//                if (mDrive.controllerOnTarget() && mDrive.hasController()) {
//                    state = DriveStraightRoutineState.DONE;
//                }
//                break;
//            case DONE:
//                mDrive.resetController();
//                break;
//            default:
//                break;
//        }
//        return commands;
//    }
//
//    @Override
//    public Commands cancel(Commands commands) {
////		Logger.getInstance().logRobotThread(Level.FINE, "Cancelling TimedDriveRoutine");
//        state = DriveStraightRoutineState.DONE;
//        commands.wantedDriveState = Drive.DriveState.NEUTRAL;
//        mDrive.resetController();
//        return commands;
//    }
//
//    @Override
//    public boolean finished() {
//        return state == DriveStraightRoutineState.DONE;
//    }
//
//    @Override
//    public String getName() {
//        return "DriveStraightRoutine";
//    }
//
//}
