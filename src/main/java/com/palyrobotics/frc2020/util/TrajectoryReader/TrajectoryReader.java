package com.palyrobotics.frc2020.util.TrajectoryReader;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.wpi.first.wpilibj.trajectory.Trajectory;

public class TrajectoryReader {

	private String jsonString;
//TODO: fix the pos2d thing, use custom assigner
	public TrajectoryReader(String jsonString) {
		this.jsonString = jsonString;
	}

	public List<Trajectory.State> getTrajectory() throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		List<Trajectory.State> out = objectMapper.readValue(jsonString, new TypeReference<List<Trajectory.State>>() {
		});
		System.out.println(out);
		return out;
	}
}
