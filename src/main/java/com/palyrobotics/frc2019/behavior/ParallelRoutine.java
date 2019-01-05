package com.palyrobotics.frc2018.behavior;

import com.palyrobotics.frc2018.config.Commands;
import com.palyrobotics.frc2018.subsystems.Subsystem;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Nihar on 12/27/16.
 */
public class ParallelRoutine extends Routine {
	private ArrayList<Routine> mRoutines;

	/**
	 * Runs all routines at the same time Finishes when all routines finish
	 * 
	 * @param routines
	 */
	public ParallelRoutine(ArrayList<Routine> routines) {
		this.mRoutines = routines;
	}

    public ParallelRoutine(Routine... routines) {
	    mRoutines = new ArrayList<>(Arrays.asList(routines));
    }

    @Override
	public void start() {
		for(Routine routine : mRoutines) {
			routine.start();
		}
	}

	@Override
	public Commands update(Commands commands) {
		ArrayList<Routine> routinesToRemove = getFinishedAutos();
		if(!routinesToRemove.isEmpty()) {
			for (Routine routine : routinesToRemove) {
				routine.cancel(commands);
				mRoutines.remove(routine);
//				routinesToRemove.remove(routine);
			}
		}

		for(Routine routine : mRoutines) {
			commands = routine.update(commands);
		}

		return commands;
	}

	@Override
	public Commands cancel(Commands commands) {
		return commands;
	}

	public ArrayList<Routine> getFinishedAutos() {
		ArrayList<Routine> routinesToRemove = new ArrayList<>();
		for(Routine routine : mRoutines) {
			if(routine.finished()) {
				routinesToRemove.add(routine);
			}
		}
		return routinesToRemove;
	}

	@Override
	public boolean finished() {
		return mRoutines.size() == 0;
	}

	@Override
	public Subsystem[] getRequiredSubsystems() {
		return RoutineManager.sharedSubsystems(mRoutines);
	}


	@Override
	public ArrayList<Routine> getEnclosingParallelRoutine() {
		return this.mRoutines;
	}

	@Override
	public String getName() {
		String name = "ParallelRoutine of (";
		for(Routine routine : mRoutines) {
			name += (routine.getName() + " ");
		}
		return name + ")";
	}
}
