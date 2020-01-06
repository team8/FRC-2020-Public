//package com.palyrobotics.frc2020.behavior.routines.waits;
//
//import com.palyrobotics.frc2020.behavior.Routine;
//import com.palyrobotics.frc2020.config.Commands;
//import com.palyrobotics.frc2020.subsystems.Subsystem;
//
//public class WaitForHatchIntakeUp extends Routine {
//
//    @Override
//    public void start() {
//
//    }
//
//    @Override
//    public Commands update(Commands commands) {
//        commands.blockFingers = true;
//        return commands;
//    }
//
//    @Override
//    public Commands cancel(Commands commands) {
//        commands.blockFingers = false;
//        return commands;
//    }
//
//    @Override
//    public boolean finished() {
//        return mRobotState.hatchIntakeUp;
//    }
//
//    @Override
//    public Subsystem[] getRequiredSubsystems() {
//        return new Subsystem[]{mShovel, mFingers};
//    }
//
//    @Override
//    public String getName() {
//        return "HatchIntake";
//    }
//}
