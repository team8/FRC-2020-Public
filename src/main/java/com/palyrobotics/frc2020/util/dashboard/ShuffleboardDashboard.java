package com.palyrobotics.frc2020.util.dashboard;

import com.palyrobotics.frc2020.auto.*;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

import com.palyrobotics.frc2020.behavior.RoutineBase;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ShuffleboardDashboard {
    private ShuffleboardTab tab = Shuffleboard.getTab("Main");
    /*private SendableChooser<RoutineBase> auto = new SendableChooser<>();

    private RoutineBase autoPicker() {
        auto.addOption("Move and Shoot One", new MoveandShootOne().getRoutine());
        auto.addOption("Shoot Three Leave Initiation Line", new ShootThreeLeaveInitiationLine().getRoutine());
        auto.addOption("Start Center Friendly Trench Three Shoot Three", new StartCenterFriendlyTrenchThreeShootThree().getRoutine());
        auto.addOption("Start Center Shoot Three Rendezvous Five Shoot Five", new StartCenterShootThreeRendezvousFiveShootFive().getRoutine());
        auto.addOption("Start Left Corner Move Turn Drive", new StartLeftCornerMoveTurnDrive().getRoutine());
        auto.addOption("Trench Steal Two Shoot Five", new TrenchStealTwoShootFive().getRoutine());
        SmartDashboard.putData("Autonomous", auto);

        /*public RoutineBase getSelectedAuto() {
            return auto.getSelected();
        }

    }*/


}
