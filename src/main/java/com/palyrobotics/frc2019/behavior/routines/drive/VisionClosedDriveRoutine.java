package com.palyrobotics.frc2019.behavior.routines.drive;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.subsystems.Drive;
import com.palyrobotics.frc2019.subsystems.Subsystem;
import com.palyrobotics.frc2019.vision.Limelight;
import com.palyrobotics.frc2019.vision.LimelightControlMode;
import edu.wpi.first.wpilibj.Timer;

public class VisionClosedDriveRoutine extends Routine {

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[]{mDrive};
    }

    private double mAngle;

    private State mState = State.START;
    private double mStartTime;

    private enum State {
        START, DRIVING, TIMED_OUT, DONE
    }

    public VisionClosedDriveRoutine() {
    }

    @Override
    public void start() {
        mDrive.setNeutral();
        mState = State.START;
        mStartTime = Timer.getFPGATimestamp();
        Limelight.getInstance().setCamMode(LimelightControlMode.CamMode.VISION);
        Limelight.getInstance().setLEDMode(LimelightControlMode.LedMode.FORCE_ON); // Limelight LED on
    }

    @Override
    public Commands update(Commands commands) {
        if (mState != State.TIMED_OUT && (Timer.getFPGATimestamp() - mStartTime > 5.0)) {
//			Logger.getInstance().logRobotThread(Level.WARNING, "Timed Out!");
            mState = State.TIMED_OUT;
        }
        switch (mState) {
            case START:
                mDrive.setVisionClosedDriveController();
                commands.wantedDriveState = Drive.DriveState.CLOSED_VISION_ASSIST;
                mState = State.DRIVING;
                break;
            case DRIVING:
                if (mDrive.controllerOnTarget()) {
                    mState = State.DONE;
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
        mState = State.DONE;
        commands.wantedDriveState = Drive.DriveState.NEUTRAL;
        mDrive.setNeutral();
        return commands;
    }

    @Override
    public boolean isFinished() {
        return mState == State.DONE;
    }

    @Override
    public String getName() {
        return "Vision Closed Drive";
    }

}
