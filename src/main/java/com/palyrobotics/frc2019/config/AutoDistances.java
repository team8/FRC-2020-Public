package com.palyrobotics.frc2018.config;

import java.io.File;
import java.util.logging.Level;

import com.palyrobotics.frc2018.util.logger.Logger;
import com.palyrobotics.frc2018.util.JSONFormatter;

/**
 * Created by Eric on 2/12/18
 */
public class AutoDistances {

	public static AllianceDistances red = new AllianceDistances();
	public static AllianceDistances blue = new AllianceDistances();

	public static void updateAutoDistances() {
		loadField();
		setAutoDistances();
	}
	
	private static File field;
	
	private static void loadField() { // Make sure to ant deploy constants to roboRIO
		switch (Constants.kFieldName) {
			case AZN:
				field = JSONFormatter.loadFileDirectory("constants/fields", "AZNField.json");
				break;
			case AZN_PRACTICE:
				field = JSONFormatter.loadFileDirectory("constants/fields", "AZNPracticeField.json");
				break;
			case CMP:
				field = JSONFormatter.loadFileDirectory("constants/fields", "CMPField.json");
				break;
			case CMP_PRACTICE:
				field = JSONFormatter.loadFileDirectory("constants/fields", "CMPPracticeField.json");
				break;
			case SVR:
				field = JSONFormatter.loadFileDirectory("constants/fields", "SVRField.json");
				break;
			case SVR_PRACTICE:
				field = JSONFormatter.loadFileDirectory("constants/fields", "SVRPracticeField.json");
				break;
			case TEAM_254:
				field = JSONFormatter.loadFileDirectory("constants/fields", "Team254Field.json");
				break;
			case TEAM_8:
				field = JSONFormatter.loadFileDirectory("constants/fields", "Team8Field.json");
				break;
		}
	}
	
	private static void setAutoDistances() { // run after loading chosen field
		if (field == null) { // If the field file doesn't exist, keep default values
			Logger.getInstance().logRobotThread(Level.FINE, "Field json file not found");
			return;
		}
		
		red.kBaseLineDistanceInches = getDoubleValue("kRedBaseLineDistanceInches");
		blue.kBaseLineDistanceInches = getDoubleValue("kBlueBaseLineDistanceInches");
		red.kRightSwitchX = getDoubleValue("kRedRightSwitchX");
		red.kRightSwitchY = getDoubleValue("kRedRightSwitchY");
		blue.kRightSwitchY = getDoubleValue("kBlueRightSwitchX");
		blue.kRightSwitchY = getDoubleValue("kBlueRightSwitchY");
		red.kLeftSwitchX= getDoubleValue("kRedLeftSwitchX");
		red.kLeftSwitchY= getDoubleValue("kRedLeftSwitchY");
		blue.kLeftSwitchX = getDoubleValue("kBlueLeftSwitchX");
		blue.kLeftSwitchY = getDoubleValue("kBlueLeftSwitchY");
		blue.kPyramidFromRightY = getDoubleValue("kBluePyramidFromRightY");
		red.kPyramidFromRightY = getDoubleValue("kRedPyramidFromRightY");
		blue.kLeftScaleX = getDoubleValue("kBlueLeftScaleX");
		blue.kLeftScaleY = getDoubleValue("kBlueLeftScaleY");
		blue.kRightScaleX = getDoubleValue("kBlueRightScaleX");
		blue.kRightScaleY = getDoubleValue("kBlueRightScaleY");
		red.kLeftScaleX = getDoubleValue("kRedLeftScaleX");
		red.kLeftScaleY = getDoubleValue("kRedLeftScaleY");
		red.kRightScaleX = getDoubleValue("kRedRightScaleX");
		red.kRightScaleY = getDoubleValue("kRedRightScaleY");
		blue.kPyramidWidth = getDoubleValue("kBluePyramidWidth");
		blue.kPyramidLength = getDoubleValue("kBluePyramidLength");
		red.kPyramidWidth = getDoubleValue("kRedPyramidWidth");
		red.kPyramidLength = getDoubleValue("kRedPyramidLength");
		red.kLeftCornerOffset = getDoubleValue("kRedLeftCornerOffset");
		red.kRightCornerOffset = getDoubleValue("kRedRightCornerOffset");
		blue.kLeftCornerOffset = getDoubleValue("kBlueLeftCornerOffset");
		blue.kRightCornerOffset = getDoubleValue("kBlueRightCornerOffset");
		red.kFieldWidth = getDoubleValue("kFieldWidth");
		blue.kFieldWidth = getDoubleValue("kFieldWidth");
		blue.kLeftToCenterY = getDoubleValue("kBlueLeftToCenterY");
		red.kLeftToCenterY = getDoubleValue("kRedLeftToCenterY");
		blue.kScaleSwitchMidlineX = getDoubleValue("kBlueScaleSwitchMidlineX");
		red.kScaleSwitchMidlineX = getDoubleValue("kRedScaleSwitchMidlineX");
		red.kSwitchPlateWidth = getDoubleValue("kSwitchPlateWidth");
		red.kSwitchPlateLength = getDoubleValue("kSwitchPlateLength");
		red.kScalePlateWidth = getDoubleValue("kScalePlateWidth");
		red.kScalePlateLength = getDoubleValue("kScalePlateLength");
		blue.kSwitchPlateWidth = getDoubleValue("kSwitchPlateWidth");
		blue.kSwitchPlateLength = getDoubleValue("kSwitchPlateLength");
		blue.kScalePlateWidth = getDoubleValue("kScalePlateWidth");
		blue.kScalePlateLength = getDoubleValue("kScalePlateLength");
		blue.kRightPlatformY = getDoubleValue("kBlueRightPlatformY");
		blue.kLeftPlatformY = getDoubleValue("kBlueLeftPlatformY");
		red.kLeftPlatformY = getDoubleValue("kRedLeftPlatformY");
		red.kRightPlatformY = getDoubleValue("kRedRightPlatformY");
	}
	
	/**
	 * Return value of given key in the given field
	 * @param key
	 * @return
	 */
	private static Double getDoubleValue(String key) {
		Object value = JSONFormatter.getValueInFile(field, key);
		return (Double) value;
	}
}
