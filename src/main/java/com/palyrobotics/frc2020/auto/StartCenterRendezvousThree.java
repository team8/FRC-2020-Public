package com.palyrobotics.frc2020.auto;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveYawRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.IntakeStowRoutine;
import com.palyrobotics.frc2020.behavior.routines.vision.VisionAlignRoutine;

/**
 * @author Alexis
 */
@SuppressWarnings ("Duplicates")
public class StartCenterRendezvousThree extends RendezvousRoutine {

	//TODO: fix this, needs clear starting pos documented along with fixing the pathing. Also var names need a change

	@Override
	public RoutineBase getRoutine() {


		return new SequentialRoutine(CenterRendezvousThree());
	}
}
