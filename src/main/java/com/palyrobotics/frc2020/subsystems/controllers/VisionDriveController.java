 package com.palyrobotics.frc2020.subsystems.controllers;

 import com.palyrobotics.frc2020.config.VisionConfig;
 import com.palyrobotics.frc2020.config.constants.DrivetrainConstants;
 import com.palyrobotics.frc2020.robot.Commands;
 import com.palyrobotics.frc2020.robot.ReadOnly;
 import com.palyrobotics.frc2020.robot.RobotState;
 import com.palyrobotics.frc2020.util.config.Configs;
 import com.palyrobotics.frc2020.vision.Limelight;
 import edu.wpi.first.wpilibj.controller.PIDController;


 public class VisionDriveController extends ChezyDriveController {

     private static final double kMaxAngularPower = 0.4;
     private final Limelight mLimelight = Limelight.getInstance();
     private VisionConfig mConfig = Configs.get(VisionConfig.class);
     private int leftOutput, rightOutput;
     private final PIDController mPidController = new PIDController(mConfig.gains.p, mConfig.gains.i, mConfig.gains.d);
     @Override
     public void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState robotState) {
         boolean targetFound = mLimelight.isTargetFound();
         if (targetFound) {

             mDriveOutputs.leftOutput.setPercentOutput(leftOutput);
             mDriveOutputs.rightOutput.setPercentOutput(rightOutput);
         } else {
             super.update(commands, robotState);
         }
     }
 }