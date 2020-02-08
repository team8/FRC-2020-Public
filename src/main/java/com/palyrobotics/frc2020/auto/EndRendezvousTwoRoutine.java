package com.palyrobotics.frc2020.auto;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;
import static com.palyrobotics.frc2020.vision.Limelight.kOneTimesZoomPipelineId;

import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveAlignYawAssistedRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.IntakeStowRoutine;

public abstract class EndRendezvousTwoRoutine extends FriendlyTrenchRoutine {

	@Override
	SequentialRoutine endRoutine() {
		return new SequentialRoutine(
				new IntakeStowRoutine(),
				new DrivePathRoutine(newWaypoint(100, -8, -65)),
				new DriveAlignYawAssistedRoutine(180, kOneTimesZoomPipelineId));
	}
}
