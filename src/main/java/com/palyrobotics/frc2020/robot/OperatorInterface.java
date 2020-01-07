package com.palyrobotics.frc2020.robot;

import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.config.Commands;
import com.palyrobotics.frc2020.config.RobotState;
import com.palyrobotics.frc2020.config.constants.DrivetrainConstants;
import com.palyrobotics.frc2020.config.constants.OtherConstants;
import com.palyrobotics.frc2020.subsystems.*;
import com.palyrobotics.frc2020.util.input.Joystick;
import com.palyrobotics.frc2020.util.input.XboxController;
import com.palyrobotics.frc2020.vision.Limelight;
import com.palyrobotics.frc2020.vision.LimelightControlMode;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;

import java.util.List;

/**
 * Used to produce {@link Commands}'s from human input. Should only be used in robot package.
 *
 * @author Nihar
 */
public class OperatorInterface {

    private static OperatorInterface sInstance = new OperatorInterface();
    private final Limelight mLimelight = Limelight.getInstance();
    private final Joystick mDriveStick = HardwareAdapter.getInstance().getJoysticks().driveStick, mTurnStick = HardwareAdapter.getInstance().getJoysticks().turnStick;
    private final XboxController mOperatorXboxController = HardwareAdapter.getInstance().getJoysticks().operatorXboxController;
    // Timestamp when a vision routine was last activated; helps us know when to turn LEDs off
    private final Timer mVisionLEDTimer = new Timer();

    private OperatorInterface() {
        mVisionLEDTimer.start();
    }

    public static OperatorInterface getInstance() {
        return sInstance;
    }

    /**
     * Helper method to only add routines that aren't already in wantedRoutines
     *
     * @param commands      Current set of commands being modified
     * @param wantedRoutine Routine to add to the commands
     * @return whether or not wantedRoutine was successfully added
     */
    private boolean addWantedRoutine(Commands commands, Routine wantedRoutine) {
        for (Routine routine : commands.wantedRoutines) {
            if (routine.getClass().equals(wantedRoutine.getClass())) {
                return false;
            }
        }
        commands.wantedRoutines.add(wantedRoutine);
        return true;
    }

    /**
     * Returns modified commands
     *
     * @param commands Last commands
     */
    Commands updateCommands(Commands commands) {

        commands.cancelCurrentRoutines = false;

        updateDriveCommands(commands);

        if (mOperatorXboxController.getDPadUp()) {
            commands.addWantedRoutine(new DrivePathRoutine(
                    new Pose2d(0.0, 0.0, new Rotation2d()),
                    new Pose2d(2.0, 0.0, new Rotation2d()),
                    new Pose2d(2.5, 0.5, Rotation2d.fromDegrees(90)),
                    new Pose2d(2.5, 2.5, Rotation2d.fromDegrees(90)
                    )));
//            commands.addWantedRoutine(new DrivePathRoutine(
//                    new Pose2d(0.0, 0.0, new Rotation2d()),
//                    new Pose2d(2.5, 0.0, new Rotation2d())
//            ));
        } else if (mOperatorXboxController.getDPadDown()) {
//            commands.addWantedRoutine(new DrivePathRoutine(
//                    new Pose2d(2.5, 2.5, Rotation2d.fromDegrees(90)),
//                    new Pose2d(2.5, 0.5, Rotation2d.fromDegrees(90)),
//                    new Pose2d(2.0, 0.0, new Rotation2d()),
//                    new Pose2d(0.0, 0.0, new Rotation2d()
//                    )));
        }

        mOperatorXboxController.updateLastInputs();

        return commands;
    }

    private void updateDriveCommands(Commands commands) {
        if (commands.wantedDriveState != Drive.DriveState.OFF_BOARD_CONTROLLER && commands.wantedDriveState != Drive.DriveState.ON_BOARD_CONTROLLER) {
            commands.wantedDriveState = Drive.DriveState.CHEZY;
        }
        // More safety
        if (Math.abs(mDriveStick.getY()) > DrivetrainConstants.kDeadband || Math.abs(mTurnStick.getX()) > DrivetrainConstants.kDeadband) {
            commands.wantedDriveState = Drive.DriveState.CHEZY;
        }
        commands.driveThrottle = -mDriveStick.getY();
        commands.driveWheel = mTurnStick.getX();
        commands.isQuickTurn = mTurnStick.getTrigger();
        commands.isBraking = mDriveStick.getTrigger();

//        boolean wantsAssistedVision = mTurnStick.getRawButton(3) || mDriveStick.getRawButton(3);
        boolean wantsAssistedVision = mTurnStick.getRawButton(3);
        if (wantsAssistedVision) {
            // Limelight vision tracking on
            setVision(true);
            commands.wantedDriveState = Drive.DriveState.VISION_ASSIST;
        } else {
            if (!mTurnStick.getRawButton(4)) {
                RobotState.getInstance().atVisionTargetThreshold = false;
            }
            if (mVisionLEDTimer.get() > OtherConstants.kVisionLEDTimeoutSeconds) {
                setVision(false);
            }
        }

        if (mTurnStick.getRawButton(4)) {
            mVisionLEDTimer.reset();
            // Limelight vision tracking on
            setVision(true);
            Drive.getInstance().setVisionClosedDriveController();
            commands.wantedDriveState = Drive.DriveState.CLOSED_VISION_ASSIST;
        } else {
            if (!wantsAssistedVision) {
                RobotState.getInstance().atVisionTargetThreshold = false;
            }
            if (mVisionLEDTimer.get() > OtherConstants.kVisionLEDTimeoutSeconds) {
                setVision(false);
            }
        }

        if (mDriveStick.getTriggerPressed()) {
            commands.cancelCurrentRoutines = true;
        }
    }

    private void setVision(boolean on) {
        mLimelight.setCamMode(on ? LimelightControlMode.CamMode.VISION : LimelightControlMode.CamMode.DRIVER); // Limelight LED off
        mLimelight.setLEDMode(on ? LimelightControlMode.LedMode.FORCE_ON : LimelightControlMode.LedMode.FORCE_OFF);
    }
}