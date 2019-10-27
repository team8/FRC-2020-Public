package com.palyrobotics.frc2019.behavior.routines.drive;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.subsystems.Drive;
import com.palyrobotics.frc2019.subsystems.Subsystem;
import com.palyrobotics.frc2019.vision.Limelight;
import com.palyrobotics.frc2019.vision.LimelightControlMode;
import edu.wpi.first.wpilibj.Timer;

public class BBTurnAngleRoutineRequireVision extends Routine {

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[]{mDrive};
    }

    private double mAngle;

    private GyroBBState mState = GyroBBState.START;
    private double mStartTime;

    private enum GyroBBState {
        START, TURNING, TIMED_OUT, DONE
    }

    public BBTurnAngleRoutineRequireVision(double angle) {
        mAngle = angle;
    }

    @Override
    public void start() {
        mDrive.setNeutral();
        mState = GyroBBState.START;
        mStartTime = Timer.getFPGATimestamp();
        Limelight.getInstance().setCamMode(LimelightControlMode.CamMode.VISION);
        Limelight.getInstance().setLEDMode(LimelightControlMode.LedMode.FORCE_ON); // Limelight LED on
    }

    @Override
    public Commands update(Commands commands) {
        if (mState != GyroBBState.TIMED_OUT && (Timer.getFPGATimestamp() - mStartTime > 5.0)) {
//			Logger.getInstance().logRobotThread(Level.WARNING, "Timed Out!");
            mState = GyroBBState.TIMED_OUT;
        }
        switch (mState) {
            case START:
//				Logger.getInstance().logRobotThread(Level.FINE, "Set set point", mAngle);
                mDrive.setTurnAngleSetPoint(mAngle);
                commands.wantedDriveState = Drive.DriveState.ON_BOARD_CONTROLLER;
                mState = GyroBBState.TURNING;
                break;
            case TURNING:
                if (mDrive.controllerOnTarget()) {
                    mState = GyroBBState.DONE;
                }
                break;
            case TIMED_OUT:
                mDrive.setNeutral();
                break;
            case DONE:
                mDrive.resetController();
                break;
        }

        return commands;
    }

    @Override
    public Commands cancel(Commands commands) {
        mState = GyroBBState.DONE;
        commands.wantedDriveState = Drive.DriveState.NEUTRAL;
        mDrive.setNeutral();
        return commands;
    }

    @Override
    public boolean isFinished() {
        return mState == GyroBBState.DONE || ((Timer.getFPGATimestamp() - mStartTime > 450) && Limelight.getInstance().isTargetFound());
    }

    @Override
    public String getName() {
        return "BangBangGyroTurnAngleRoutine";
    }

}
