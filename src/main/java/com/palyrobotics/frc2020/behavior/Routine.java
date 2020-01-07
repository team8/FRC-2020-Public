package com.palyrobotics.frc2020.behavior;

import com.palyrobotics.frc2020.config.Commands;
import com.palyrobotics.frc2020.config.RobotState;
import com.palyrobotics.frc2020.subsystems.Drive;
import com.palyrobotics.frc2020.subsystems.Subsystem;

/**
 * Abstract superclass for a routine, which specifies an autonomous series of actions <br />
 * Each routine takes in Commands and returns modified set points Requires the specific subsystems
 *
 * @author Nihar; Team 254
 */
public abstract class Routine {

    // Keeps access to all subsystems to modify their output and read their status
    protected final Drive mDrive = Drive.getInstance();
    protected final RobotState mRobotState = RobotState.getInstance();

    // Called to start a routine
    public abstract void start();

    // Update method, returns modified commands
    public abstract Commands update(Commands commands);

    // Called to stop a routine, should return modified commands if needed
    public abstract Commands cancel(Commands commands);

    // Notifies routine manager when routine is complete
    public abstract boolean isFinished();

    // Store subsystems which are required by this routine, preventing routines from overlapping
    public abstract Subsystem[] getRequiredSubsystems();

    // Force override of getName()
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public String toString() {
        return getName();
    }
}
