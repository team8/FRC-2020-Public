package com.palyrobotics.frc2020.subsystems.turret;

import com.palyrobotics.frc2020.config.subsystem.TurretConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Turret;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.vision.Limelight;
import com.palyrobotics.frc2020.vision.LimelightControlMode;

public class TurretAlignController extends Turret.TurretController {
    private Limelight mLimelight = Limelight.getInstance();
    private TurretConfig mConfig = Configs.get(TurretConfig.class);

    TurretAlignController() {
    }

    @Override
    public void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState state) {
        if (state.drivePose.getTranslation().getX() > mConfig.distanceToMiddleOfField) {
            mLimelight.setCamMode(LimelightControlMode.CamMode.VISION);
            mLimelight.setLEDMode(LimelightControlMode.LedMode.FORCE_ON);
            if (mLimelight.isTargetFound()) {

            }
        }
        else {
            //TODO run align to fron of field
        }
    }
}
