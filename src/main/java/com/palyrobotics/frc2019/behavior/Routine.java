package com.palyrobotics.frc2019.behavior;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.subsystems.*;

import java.util.ArrayList;

/**
 * Abstract superclass for a routine, which specifies an autonomous series of actions <br />
 * Each routine takes in Commands and returns modified Setpoints Requires the specific subsystems
 * 
 * @author Nihar; Team 254
 *
 */
public abstract class Routine {
	/**
	 * Keeps access to all subsystems to modify their output and read their status
	 */
	protected final Drive drive = Drive.getInstance();
	protected final Shovel shovel = Shovel.getInstance();
	protected final Shooter shooter = Shooter.getInstance();
	protected final Pusher pusher = Pusher.getInstance();
	protected final Elevator elevator = Elevator.getInstance();
	protected final Fingers fingers = Fingers.getInstance();
	protected final Intake intake = Intake.getInstance();
	protected final RobotState robotState = RobotState.getInstance();

	//Called to start a routine
	public abstract void start();

	//Update method, returns modified commands
	public abstract Commands update(Commands commands);

	//Called to stop a routine, should return modified commands if needed
	public abstract Commands cancel(Commands commands);

	//Notifies routine manager when routine is complete
	public abstract boolean finished();

	//Store subsystems which are required by this routine, preventing routines from overlapping
	public abstract Subsystem[] getRequiredSubsystems();

	//Force override of getName()
	public abstract String getName();

	public ArrayList<Routine> getEnclosingSequentialRoutine() {
		return null;
	}

	public ArrayList<Routine> getEnclosingParallelRoutine() {
		return null;
	}

	@Override
	public String toString() {
		return getName();
	}
}
