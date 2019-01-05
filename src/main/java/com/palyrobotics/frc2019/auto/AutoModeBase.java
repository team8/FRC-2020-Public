package com.palyrobotics.frc2018.auto;

import com.palyrobotics.frc2018.behavior.Routine;
import com.palyrobotics.frc2018.config.AllianceDistances;
import com.palyrobotics.frc2018.config.AutoDistances;

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

	public enum StartingPosition {
		LEFT,
		CENTER,
		RIGHT
	}

	public enum Decision {
		NEVER,
		LEFT,
		RIGHT,
		BOTH
	}

	public enum SecondSideDecision {
	    NEVER,
	    OPPOSITE,
        SAME,
        BOTH
    }

	public enum Priority {
		SCALE,
		SWITCH
	}
	
	// To set the auto mode, set these variables in code!
	public static Alliance mAlliance = Alliance.BLUE;
	public static StartingPosition mStartingPosition = StartingPosition.CENTER;
	public static Decision mScaleDecision = Decision.NEVER;
	public static Decision mSwitchDecision = Decision.BOTH;
    public static SecondSideDecision mSecondScaleSideDecision = SecondSideDecision.NEVER;
    public static SecondSideDecision mSecondSwitchSideDecision = SecondSideDecision.BOTH;
	public static Priority mPriority = Priority.SWITCH;
	public static Priority mSecondCubePriority = Priority.SWITCH;
	public static boolean mMultiCube = true;
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