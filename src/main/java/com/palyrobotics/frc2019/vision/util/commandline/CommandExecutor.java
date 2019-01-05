package com.palyrobotics.frc2018.vision.util.commandline;

import com.palyrobotics.frc2018.config.Constants;
import com.palyrobotics.frc2018.util.logger.Logger;

import java.util.logging.Level;

public class CommandExecutor {

	private static boolean s_IsFlashOn = false;

	public static String startVisionApp() {
		return RuntimeExecutor.exec(String.format("adb shell monkey -p %s 1", Constants.kPackageName));
	}

	public static String setUpADB() {
		String output = RuntimeExecutor.exec("adb kill-server && adb start-server");
		reversePorts();
		return output;
	}

	public static String reversePorts() {
		Logger.getInstance().logRobotThread(Level.INFO, "[Command Executor] Starting TCP port reversal...");
		final String output = RuntimeExecutor.exec(String.format("adb reverse tcp:%d tcp:%d", Constants.kVisionVideoReceiverSocketPort, Constants.kVisionVideoReceiverSocketPort));
		Logger.getInstance().logRobotThread(Level.INFO, String.format("[Command Executor] Reversed ports with output: %s", output));
		return output;
	}

	/**
	 * Detects the status of phone with the command "adb devices" and looking at the output.
	 *
	 * @return Whether or not a nexus device is connected
	 */
	public static DeviceStatus getNexusStatus() {
		final String allDeviceOutputs = RuntimeExecutor.exec("adb devices");
		final String[] deviceOutputs = allDeviceOutputs.split("\\n");
		if (deviceOutputs.length > 1) {
			final String firstDeviceOutput = deviceOutputs[1];
			if (firstDeviceOutput.contains("device")) {
				return DeviceStatus.DEVICE;
			} else if (firstDeviceOutput.contains("offline")) {
				return DeviceStatus.OFFLINE;
			} else if (firstDeviceOutput.contains("unauthorized")) {
				return DeviceStatus.UNAUTHORIZED;
			}
		}
		return DeviceStatus.NOT_FOUND;
	}

	public static void restartADBServer() {
		Logger.getInstance().logRobotThread(Level.INFO, "Restarting server...");
		final String restartOut = RuntimeExecutor.exec("adb kill-server && adb start-server");
		Logger.getInstance().logRobotThread(Level.INFO, String.format("Restarted with output: %s", restartOut));
	}

	public static String toggleFlash() {
		return RuntimeExecutor.exec(String.format("adb shell am broadcast -a %s.GET_DATA --es type flash --ez isFlash %s", Constants.kPackageName, Boolean.toString(s_IsFlashOn)));
	}

	public static String catFile(String fileName) {
		return RuntimeExecutor.exec(String.format("adb shell run-as %s cat /data/data/%s/files/%s", Constants.kPackageName, Constants.kPackageName, fileName));
	}

	/**
	 * @return the PID of the vision app
	 */
	public static String getAppPID() {
		return RuntimeExecutor.exec(String.format("adb shell pidof %s", Constants.kPackageName));
	}
}
