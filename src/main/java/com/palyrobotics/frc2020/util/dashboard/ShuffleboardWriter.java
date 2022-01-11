package com.palyrobotics.frc2020.util.dashboard;

import com.palyrobotics.frc2020.auto.ShootThreeLeaveInitiationLine;
import com.palyrobotics.frc2020.auto.StartCenterFriendlyTrenchThreeShootThree;
import com.palyrobotics.frc2020.auto.TrenchStealTwoShootFive;
import com.palyrobotics.frc2020.behavior.RoutineBase;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/*
 * Code to display information to the dashboard on the driver station. Includes: - an auto selector
 * -
 */
public class ShuffleboardWriter {

	private ShuffleboardTab auto1 = Shuffleboard.getTab("Auto modes");
	private SendableChooser<RoutineBase> auto = new SendableChooser<>();
	private ShuffleboardTab velocity = Shuffleboard.getTab("Vision");

	public ShuffleboardWriter() {
		autoMode();
		robotVelocity();
	}

	private void autoMode() {
		auto.addOption("ShootThreeLeaveInitiationLine", new ShootThreeLeaveInitiationLine().getRoutine());
		auto.addOption("TrenchStealTwoShootFive", new TrenchStealTwoShootFive().getRoutine());
		auto.addOption("StartCenterFriendlyTrenchThreeShootThree", new StartCenterFriendlyTrenchThreeShootThree().getRoutine());
		SmartDashboard.putData("Auto modes", auto);
	}

	private void robotVelocity() {
	}

	public RoutineBase getSelectedAuto() {
		return auto.getSelected();
	}

	private double getRobotVelocity() {
		return 0.0;
	}
}
