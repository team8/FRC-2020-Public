package com.palyrobotics.frc2020.util.autographer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.palyrobotics.frc2020.auto.AutoBase;
import com.palyrobotics.frc2020.behavior.MultipleRoutineBase;
import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveYawRoutine;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;

import org.reflections.Reflections;

public class AutoGrapherSerializer {

	private static final ObjectMapper sMapper = new ObjectMapper();

	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, IOException, NoSuchMethodException, InvocationTargetException {
	}

	public static String autoSerializer(String inputAuto) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
		// retrieve automode class from input string
		Class<?> autoClass = Class.forName(String.format("com.palyrobotics.frc2020.auto.%s", inputAuto));
		ArrayList<RoutineBase> driveRoutineList = new ArrayList<>();

		// split all sequential routines inside auto and retrieve all drive path, odometry, and drive yaw routines
		multipleRoutineSplitter(((AutoBase) autoClass.getDeclaredConstructor().newInstance()).getRoutine(),
				driveRoutineList,
				DrivePathRoutine.class, DriveSetOdometryRoutine.class, DriveYawRoutine.class);

		// take pose in odometry routine and add to start of first drive path routine
		Pose2d lastPose = ((DriveSetOdometryRoutine) driveRoutineList.get(0)).getTargetPose();
		driveRoutineList.remove(0);

		for (int i = 0; i < driveRoutineList.size(); i++) {
			if (driveRoutineList.get(i) instanceof DriveYawRoutine) {
				lastPose = new Pose2d(lastPose.getTranslation(),
						new Rotation2d(Math.toRadians(((DriveYawRoutine) driveRoutineList.get(i)).getYaw())));
				driveRoutineList.remove(i);
				i--;
			} else {
				List<Pose2d> pathList = new ArrayList<>(((DrivePathRoutine) driveRoutineList.get(i)).getWaypoints());
				pathList.add(0, lastPose);
				lastPose = pathList.get(pathList.size() - 1);
				DrivePathRoutine drivePathWithLastPose = ((DrivePathRoutine) driveRoutineList.get(i)).isReversed() ?
						(new DrivePathRoutine(pathList)).driveInReverse() :
						new DrivePathRoutine(pathList);
				driveRoutineList.set(i, drivePathWithLastPose);
			}
		}

		// return string format if routine list length is 1
		return driveRoutineList.size() > 1 ?
				sMapper.writeValueAsString(new SequentialRoutine(driveRoutineList)) :
				String.format("{\"routines\": %s}", sMapper.writeValueAsString(driveRoutineList));
	}

	public static String getAutos() {
		// use reflection library to get all automodes
		var reflections = new Reflections("com.palyrobotics.frc2020.auto");
		Set<Class<? extends AutoBase>> autoClassSet = reflections.getSubTypesOf(AutoBase.class);
		var allAutoClasses = new StringBuilder();
		for (Class<? extends AutoBase> currentAuto : autoClassSet) {
			allAutoClasses.append(currentAuto.getSimpleName()).append("-");
		}
		return allAutoClasses.toString();
	}

	public static SequentialRoutine replaceDrivePaths(String inputJson, String autoName) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, IOException {
		// get auto class
		Class<?> autoClass = Class.forName(String.format("com.palyrobotics.frc2020.auto.%s", autoName));
		List<RoutineBase> routineList = new ArrayList<>();

		// split all sequential routines
		multipleRoutineSplitter(((AutoBase) autoClass.getDeclaredConstructor().newInstance()).getRoutine(),
				routineList);

		// read json array of drive paths
		DrivePathRoutine[] drivePathRoutines = sMapper.readValue(inputJson, DrivePathRoutine[].class);

		// remove starting pose to help with trajectory
		for (int i = 0; i < drivePathRoutines.length; i++) {
			DrivePathRoutine currentDrive = new DrivePathRoutine(
					new ArrayList<>(drivePathRoutines[i].getWaypoints()).subList(1,
							drivePathRoutines[i].getWaypoints().size()));
			drivePathRoutines[i] = drivePathRoutines[i].isReversed() ? currentDrive.driveInReverse() : currentDrive;
		}

		// place drive paths in preset automode with drive paths from the autographer
		int a = 0;
		for (int i = 0; i < routineList.size(); i++) {
			if (routineList.get(i) instanceof DrivePathRoutine) {
				routineList.set(i, drivePathRoutines[a]);
				a++;
			}

		}

//		if (Robot.isSimulation()) {
//			var fileWriter = new BufferedWriter(new FileWriter("bebeb.txt"));
//			fileWriter.write(objectMapper.writeValueAsString(new SequentialRoutine(routineList)));
//			fileWriter.close();
//		}
		System.out.println(sMapper.writeValueAsString(routineList));
		return new SequentialRoutine(routineList);
	}

	// recursively splits multipleRoutines and places its contents in a list
	private static void multipleRoutineSplitter(RoutineBase routines, List<RoutineBase> routineList, Class... routineClass) {
		if (routines instanceof MultipleRoutineBase) {
			List<RoutineBase> decomposedRoutine = ((MultipleRoutineBase) routines).getRoutines();
			for (RoutineBase current : decomposedRoutine) {
				multipleRoutineSplitter(current, routineList, routineClass);
			}
		} else {
			if (routineClass.length > 0) {
				for (Class currentClass : routineClass) {
					if (currentClass.isInstance(routines)) {
						routineList.add(routines);
						break;
					}
				}
			} else {
				routineList.add(routines);
			}
		}
	}

	public static String autoTrajectoryGenerator(String autoJson) throws JsonProcessingException {
		var objectMapper = new ObjectMapper();
		DrivePathRoutine[] drivePathRoutines = objectMapper.readValue(autoJson, DrivePathRoutine[].class);
		var json = new StringBuilder();
		for (DrivePathRoutine currentDrivePath : drivePathRoutines) {
			json.append(objectMapper.writeValueAsString(currentDrivePath.generateTrajectory()));
		}
		return json.toString();
	}
}
