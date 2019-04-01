package com.palyrobotics.frc2019.auto;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.AllianceDistances;
import com.palyrobotics.frc2019.config.AutoDistances;

public abstract class AutoModeBase {
	protected boolean active = false;

	public abstract String toString();

	//Will be run before the routine is taken
	public abstract void prestart();

	public AutoModeBase() {
		loadDistances();
	}

	public enum Alliance {
		RED,
		BLUE
	}

	
	// To set the auto mode, set these variables in code!
	public static Alliance mAlliance = Alliance.BLUE;
	public static AllianceDistances mDistances;

	private static void loadDistances() {
		if(mAlliance == Alliance.BLUE) {
			mDistances = AutoDistances.blue;
		} else {
			mDistances = AutoDistances.red;
		}

	}

	public abstract Routine getRoutine();

	public void stop() {
		active = false;
	}

	public boolean isActive() {
		return active;
	}
	
	public abstract String getKey();
}