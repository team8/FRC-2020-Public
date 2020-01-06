 package com.palyrobotics.frc2019.behavior.routines.drive;

 import com.palyrobotics.frc2019.behavior.Routine;
 import com.palyrobotics.frc2019.config.Commands;
 import com.palyrobotics.frc2019.config.subsystem.DriveConfig;
 import com.palyrobotics.frc2019.subsystems.Drive;
 import com.palyrobotics.frc2019.subsystems.Subsystem;
 import com.palyrobotics.frc2019.util.config.Configs;
 import edu.wpi.first.wpilibj.geometry.Pose2d;
 import edu.wpi.first.wpilibj.trajectory.Trajectory;
 import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig;
 import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;

 import java.util.List;

 public class DrivePathRoutine extends Routine {

     private final Trajectory mTrajectory;
     private final DriveConfig mDriveConfig = Configs.get(DriveConfig.class);

     public DrivePathRoutine(List<Pose2d> waypoints) {
         mTrajectory = TrajectoryGenerator.generateTrajectory(
                 waypoints,
                 new TrajectoryConfig(mDriveConfig.maxPathVelocityMetersPerSecond, mDriveConfig.maxPathAccelerationMetersPerSecondSquared)
         );
     }

     @Override
     public void start() {
         mDrive.setTrajectoryController(mTrajectory);
     }

     @Override
     public Commands update(Commands commands) {
         commands.wantedDriveState = Drive.DriveState.ON_BOARD_CONTROLLER;
         return commands;
     }

     @Override
     public Commands cancel(Commands commands) {
         mDrive.setNeutral();
         commands.wantedDriveState = Drive.DriveState.NEUTRAL;
         return commands;
     }

     @Override
     public boolean isFinished() {
         return mDrive.isOnTarget();
     }

     @Override
     public Subsystem[] getRequiredSubsystems() {
         return new Subsystem[]{mDrive};
     }
 }
