package com.palyrobotics.frc2020.behavior.routines.drive;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wpi.first.wpilibj.trajectory.Trajectory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class DrivePathPremadeRoutine extends DrivePathRoutine{
    public DrivePathPremadeRoutine(String trajectoryFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Trajectory obj = mapper.readValue(new File(trajectoryFile), Trajectory.class);
        var states = new ArrayList<Trajectory.State>();


        //json open trajfile, read it
    }
}
