package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.subsystem.DriveConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.controllers.DriveRamseteController;
import com.palyrobotics.frc2020.util.CheesyDriveController;
import com.palyrobotics.frc2020.util.SparkDriveSignal;
import com.palyrobotics.frc2020.util.config.Configs;

/**
 * Represents the drivetrain.
 * Uses controllers or cheesy drive helper/proportional drive helper to calculate a drive signal.
 */
public class Drive extends Subsystem {

    public enum DriveState {
        NEUTRAL, TELEOP, SIGNAL, FOLLOW_PATH, VISION_ALIGN
    }

    public abstract static class DriveController {

        protected final DriveConfig mDriveConfig = Configs.get(DriveConfig.class);

        protected SparkDriveSignal mSignal = new SparkDriveSignal();

        public abstract SparkDriveSignal update(@ReadOnly Commands commands, @ReadOnly RobotState state);

        public boolean onTarget() {
            return false;
        }
    }

    private static Drive sInstance = new Drive();

    private Drive.DriveController mController;
    private DriveState mState;
    private SparkDriveSignal mSignal = new SparkDriveSignal();

    public static Drive getInstance() {
        return sInstance;
    }

    private Drive() {
        super("drive");
    }

    public SparkDriveSignal getDriveSignal() {
        return mSignal;
    }

    @Override
    public void update(@ReadOnly Commands commands, @ReadOnly RobotState state) {
        DriveState wantedState = commands.getDriveState();
        boolean isNewState = mState != wantedState;
        mState = wantedState;
        if (isNewState) {
            switch (wantedState) {
                case NEUTRAL:
                    mController = null;
                    break;
                case TELEOP:
                    mController = new CheesyDriveController();
                    break;
                case SIGNAL:
                    mController = new DriveController() {
                        @Override
                        public SparkDriveSignal update(Commands commands, RobotState state) {
                            return commands.getWantedDriveSignal();
                        }
                    };
                    break;
                case FOLLOW_PATH:
                    mController = new DriveRamseteController(commands.getDriveTrajectory());
                    break;
            }
        }
        if (mController == null) {
            mSignal.leftOutput.setIdle();
            mSignal.rightOutput.setIdle();
        } else {
            mSignal = mController.update(commands, state);
        }
    }

    public boolean isOnTarget() {
        return mController == null || mController.onTarget();
    }
}
