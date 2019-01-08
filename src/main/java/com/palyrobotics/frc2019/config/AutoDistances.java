package com.palyrobotics.frc2019.config;

import java.io.File;
import java.util.logging.Level;

import com.palyrobotics.frc2019.util.logger.Logger;
import com.palyrobotics.frc2019.util.JSONFormatter;

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
			case DMR:
				field = JSONFormatter.loadFileDirectory("constants/fields", "DMRField.json");
				break;
			case DMR_PRACTICE:
				field = JSONFormatter.loadFileDirectory("constants/fields", "DMRPracticeField.json");
				break;
            case GNR:
                field = JSONFormatter.loadFileDirectory("constants/fields", "GNRField.json");
                break;
            case GNR_PRACTICE:
                field = JSONFormatter.loadFileDirectory("constants/fields", "GNRPracticeField.json");
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

        blue.kDepotFromLeftY = getDoubleValue("kBlueDepotFromLeftY");
        red.kDepotFromLeftY = getDoubleValue("kRedDepotFromLeftY");
        blue.kDepotFromRightY =	getDoubleValue("kBlueDepotFromRightY");
        red.kDepotFromRightY = getDoubleValue("kRedDepotFromRightY");
        blue.kLevel2FromRightY = getDoubleValue("kBlueLevel2FromRightY");
        red.kLevel2FromRightY = getDoubleValue("kRedLevel2FromRightY");
        blue.kLevel2FromLeftY = getDoubleValue("kBlueLevel2FromLeftY");
        red.kLevel2FromLeftY	= getDoubleValue("kRedLevel2FromLeftY");
        blue.kLevel1FromLeftY = getDoubleValue("kBlueLevel1FromLeftY");
        red.kLevel1FromLeftY	= getDoubleValue("kRedLevel1FromLeftY");
        blue.kLevel1FromRightY = getDoubleValue("kBlueLevel1FromRightY");
        red.kLevel1FromRightY = getDoubleValue("kRedLevel1FromRightY");
        blue.kCargoOffsetX = getDoubleValue("kBlueCargoOffsetX");
        red.kCargoOffsetX = getDoubleValue("kRedCargoOffsetX");
        blue.kCargoOffsetY = getDoubleValue("kBlueCargoOffsetY");
        red.kCargoOffsetY = getDoubleValue("kRedCargoOffsetY");
        blue.kLevel1CargoX = getDoubleValue("kBlueLevel1CargoX");
        red.kLevel1CargoX = getDoubleValue("kRedLevel1CargoX");
        blue.kCargoLeftY = getDoubleValue("kBlueCargoLeftY");
        red.kCargoLeftY	= getDoubleValue("kRedCargoLeftY");
        blue.kCargoRightY = getDoubleValue("kBlueCargoRightY");
        red.kCargoRightY = getDoubleValue("kRedCargoRightY");
        blue.kMidlineLeftRocketFarX = getDoubleValue("kBlueMidlineLeftRocketFarX");
        red.kMidlineLeftRocketFarX = getDoubleValue("kRedMidlineLeftRocketFarX");
        blue.kMidlineRightRocketFarX = getDoubleValue("kBlueMidlineRightRocketFarX");
        red.kMidlineRightRocketFarX = getDoubleValue("kRedMidlineRightRocketFarX");
        blue.kHabLeftRocketCloseX = getDoubleValue("kBlueHabLeftRocketCloseX");
        red.kHabLeftRocketCloseX = getDoubleValue("kRedHabLeftRocketCloseX");
        blue.kHabRightRocketCloseX = getDoubleValue("kBlueHabRightRocketCloseX");
        red.kHabRightRocketCloseX = getDoubleValue("kRedHabRightRocketCloseX");
        blue.kHabLeftRocketMidX = getDoubleValue("kBlueHabLeftRocketMidX");
        red.kHabLeftRocketMidX = getDoubleValue("kRedHabLeftRocketMidX");
        blue.kHabRightRocketMidX = getDoubleValue("kBlueHabRightRocketMidX");
        red.kHabRightRocketMidX = getDoubleValue("kRedHabRightRocketMidX");
        blue.kLeftRocketFarY = getDoubleValue("kBlueLeftRocketFarY");
        red.kLeftRocketFarY = getDoubleValue("kRedLeftRocketFarY");
        blue.kRightRocketFarY = getDoubleValue("kBlueRightRocketFarY");
        red.kRightRocketFarY = getDoubleValue("kRedRightRocketFarY");
        blue.kLeftRocketMidY = getDoubleValue("kBlueLeftRocketMidY");
        red.kLeftRocketMidY	= getDoubleValue("kRedLeftRocketMidY");
        blue.kRightRocketMidY = getDoubleValue("kBlueRightRocketMidY");
        red.kRightRocketMidY = getDoubleValue("kRedRightRocketMidY");
        blue.kLeftRocketCloseY = getDoubleValue("kBlueLeftRocketCloseY");
        red.kLeftRocketCloseY = getDoubleValue("kRedLeftRocketCloseY");
        blue.kRightRocketCloseY = getDoubleValue("kBlueRightRocketCloseY");
        red.kRightRocketCloseY = getDoubleValue("kRedRightRocketCloseY");
        blue.kLeftLoadingY = getDoubleValue("kBlueLeftLoadingY");
        red.kLeftLoadingY = getDoubleValue("kRedLeftLoadingY");
        blue.kRightLoadingY = getDoubleValue("kBlueRightLoadingY");
        red.kRightLoadingY = getDoubleValue("kRedRightLoadingY");
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
