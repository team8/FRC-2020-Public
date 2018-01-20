package com.palyrobotics.frc2018.util.logger;

import com.palyrobotics.frc2018.config.RobotState;
import com.palyrobotics.frc2018.robot.Robot;
import com.palyrobotics.frc2018.subsystems.Drive;
import org.junit.Test;

import java.util.logging.Level;

public class LoggerTest {
	@Test
	public void testFileCreation() {
		Logger logger = Logger.getInstance();
		logger.setFileName("test");
		logger.start();
	}

	@Test
	public void testWriting() {
		Logger logger = Logger.getInstance();
		logger.start();
		logger.logRobotThread(Level.INFO, "Testing");
		logger.logRobotThread(Level.INFO,"asdf");
		logger.logRobotThread(Level.INFO,"number", 1);
		logger.cleanup();
		logger.start();
		logger.logRobotThread(Level.INFO,"New message");
		// messages should be flushed
		logger.cleanup();
		try {
			Thread.sleep(100);
		} catch (Exception e) {

		}
	}

	@Test
	public void testLogRunThrough() {
		Logger logger = Logger.getInstance();
		logger.start();
		Drive.getInstance().start();
		Robot.getRobotState().gamePeriod = RobotState.GamePeriod.TELEOP;
		Drive.getInstance().update(Robot.getCommands(), Robot.getRobotState());
//		logger.logSubsystemThread(Drive.getInstance().getStatus());
//		logger.logSubsystemThread(Slider.getInstance().getStatus());
		logger.logRobotThread(Level.INFO,"Robot start");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		logger.logRobotThread(Level.INFO,"Pre cleanup 1");
		logger.cleanup();
		logger.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		logger.logRobotThread(Level.INFO,"Post cleanup 1");
		logger.cleanup();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testRandomFileStuff() {
	}

	@Test
	public void testCrashTracker() {
		// Crash tracker crashes on non roboRIO
		//CrashTracker.logRobotInit();
	}
}
