package com.palyrobotics.frc2019.auto;

import com.palyrobotics.frc2019.auto.AutoFMS.Side;
import com.palyrobotics.frc2019.auto.AutoModeBase.*;
import com.palyrobotics.frc2019.auto.testautos.FTestAuto;
import com.palyrobotics.frc2019.auto.testautos.TestAutoMode;
import com.palyrobotics.frc2019.util.logger.Logger;
import org.json.simple.JSONArray;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * @author Nihar, based off Team 254 2015
 */
public class AutoModeSelector {
	private static AutoModeSelector instance = null;
	private ArrayList<AutoModeBase> mAutoModes = new ArrayList<>();
	private HashMap<String, Integer> mAutoMap = new HashMap<>();

	private AutoFMS autoFMS = AutoFMS.getInstance();

	public static AutoModeSelector getInstance() {
		if(instance == null) {
			instance = new AutoModeSelector();
		}
		return instance;
	}

	protected AutoModeSelector() {
        // left to right, blue alliance to red alliance
        /* 0 */ registerAutonomous(new FTestAuto(), 0);

	}

	/**
	 * Add an AutoMode to list to choose from
	 *
	 * @param auto
	 *            AutoMode to add
	 */
	public void registerAutonomous(AutoModeBase auto, int id) {
		mAutoModes.add(auto);
		mAutoMap.put(auto.getKey(), id);
	}

	/**
	 * Get the currently selected AutoMode
	 *
	 * @return AutoMode currently selected
	 */
	public AutoModeBase getAutoMode() {
		return new FTestAuto();
	}

	/**
	 * Get the AutoMode at specified index
	 *
	 * @return AutoMode at specified index
	 */

	public AutoModeBase getAutoMode(Alliance alliance, StartingPosition startingPosition, Decision scaleDecision, Decision switchDecision, SecondSideDecision secondScaleDecision, SecondSideDecision secondSwitchDecision, Priority priority, Priority secondPriority, boolean multiCube) {
		//Final index
		int selectedIndex;

		//Index of the ideal scale and switch autos; the robot's priority determines which one is run
		int scaleIndex = -1;
		int switchIndex = -1;

		AutoFMS.Side scaleSide = autoFMS.getScaleSide();
		AutoFMS.Side switchSide = autoFMS.getSwitchSide();
		
		//Find ideal scale auto, or the auto it would execute if it had to score on the scale
		if(scaleDecision != Decision.NEVER) {
			if(scaleDecision == Decision.BOTH || (scaleDecision == Decision.LEFT && scaleSide == Side.LEFT) || scaleDecision == Decision.RIGHT && scaleSide == Side.RIGHT) {
				String key;
				if(multiCube && startingPosition != StartingPosition.CENTER) {
					key = alliance + " " + startingPosition + " SCALE " + scaleSide;
					String secondPartScale = "";
					String secondPartSwitch = "";
					if(secondScaleDecision != SecondSideDecision.NEVER) {
					    secondPartScale = " SCALE " + scaleSide;
                    }
                    if(secondSwitchDecision != SecondSideDecision.NEVER) {
					    if(secondSwitchDecision == SecondSideDecision.BOTH || (secondSwitchDecision == SecondSideDecision.SAME && scaleSide == switchSide) ||
                            (secondSwitchDecision == SecondSideDecision.OPPOSITE && scaleSide != switchSide)) {
                                secondPartSwitch = " SWITCH " + switchSide;
                        }
                    }
                    if(secondPriority == Priority.SWITCH) {
					    if(!secondPartSwitch.equals("")) {
					        if(mAutoMap.get(key+secondPartSwitch) != null) {
                                key += secondPartSwitch;
                            }
                        } else if(!secondPartScale.equals("")) {
                            if(mAutoMap.get(key+secondPartScale) != null) {
                                key += secondPartScale;
                            }
                        } else {
                            Logger.getInstance().logSubsystemThread(Level.WARNING, "Error in selecting multi cube, could not find a multi cube auto with the given specifications");
                        }
                    } else if(secondPriority == Priority.SCALE) {
					    if(!secondPartScale.equals("")) {
                            if(mAutoMap.get(key+secondPartScale) != null) {
                                key += secondPartScale;
                            }
                        } else if(!secondPartSwitch.equals("")) {
                            if(mAutoMap.get(key+secondPartSwitch) != null) {
                                key += secondPartSwitch;
                            }
                        } else {
                            Logger.getInstance().logSubsystemThread(Level.WARNING, "Error in selecting auto, could not find a multi cube auto with the given specifications");
                        }
                    }
				} else {
					key = alliance + " " + startingPosition + " SCALE " + scaleSide;
				}
				try {
					scaleIndex = mAutoMap.get(key);
				} catch (Exception e) {
					scaleIndex = 0;
					Logger.getInstance().logSubsystemThread(Level.WARNING, "Error in selecting auto, defaulting to baseline");
				}
				Logger.getInstance().logRobotThread(Level.INFO, "Attempted to find scale auto mode with this key: " + key);
			}
		}

		//Find ideal switch auto, or the auto it would execute if it had to score on the switch
		if(switchDecision != Decision.NEVER) {
            if (switchDecision == Decision.BOTH || (switchDecision == Decision.LEFT && switchSide == Side.LEFT) || switchDecision == Decision.RIGHT && switchSide == Side.RIGHT) {
                String key;
                if (multiCube && startingPosition != StartingPosition.CENTER) {
                    key = alliance + " " + startingPosition + " SWITCH " + switchSide;
                    String secondPartScale = "";
                    String secondPartSwitch = "";
                    if (secondScaleDecision != SecondSideDecision.NEVER) {
                        secondPartScale = " SCALE " + scaleSide;
                    }
                    if (secondSwitchDecision != SecondSideDecision.NEVER) {
                        if (secondSwitchDecision == SecondSideDecision.BOTH || (secondSwitchDecision == SecondSideDecision.SAME && scaleSide == switchSide) ||
                                (secondSwitchDecision == SecondSideDecision.OPPOSITE && scaleSide != switchSide)) {
                            secondPartSwitch = " SWITCH " + switchSide;
                        }
                    }
                    if (secondPriority == Priority.SWITCH) {
                        if (!secondPartSwitch.equals("")) {
                            if(mAutoMap.get(key+secondPartSwitch) != null) {
                                key += secondPartSwitch;
                            }
                        } else if (!secondPartScale.equals("")) {
                            if(mAutoMap.get(key+secondPartScale) != null) {
                                key += secondPartScale;
                            }
                        } else {
                            Logger.getInstance().logSubsystemThread(Level.WARNING, "Error in selecting multi cube auto, could not find a multi cube auto with the given specifications");
                        }
                    } else if (secondPriority == Priority.SCALE) {
                        if (!secondPartScale.equals("")) {
                            if(mAutoMap.get(key+secondPartScale) != null) {
                                key += secondPartScale;
                            }
                        } else if (!secondPartSwitch.equals("")) {
                            if(mAutoMap.get(key+secondPartSwitch) != null) {
                                key += secondPartSwitch;
                            }
                        } else {
                            Logger.getInstance().logSubsystemThread(Level.WARNING, "Error in selecting multi cube auto, could not find a multi cube auto with the given specifications");
                        }
                    } else {
                        key = alliance + " " + startingPosition + " SWITCH " + switchSide;
                    }
                    try {
                        switchIndex = mAutoMap.get(key);
                    } catch (Exception e) {
                        switchIndex = 0;
                        Logger.getInstance().logSubsystemThread(Level.WARNING, "Error in selecting auto, defaulting to baseline");
                    }
                    Logger.getInstance().logRobotThread(Level.INFO, "Attempted to find switch auto mode with this key: " + key);
                } else if(multiCube && startingPosition == StartingPosition.CENTER) {
                    key = alliance + " " + startingPosition + " SWITCH " + switchSide;
                    if(mAutoMap.get(key + " SWITCH " + switchSide) != null) {
                        key += " SWITCH " + switchSide;
                    }
                    try {
                        switchIndex = mAutoMap.get(key);
                    } catch (Exception e) {
                        switchIndex = 0;
                        Logger.getInstance().logSubsystemThread(Level.WARNING, "Error in selecting auto, defaulting to baseline");
                    }
                }
            }
        }

		//Priority checking
		if(priority == Priority.SCALE) {
			if(scaleIndex != -1) {
				selectedIndex = scaleIndex;
			} else if(switchIndex != -1) {
				selectedIndex = switchIndex;
			} else {
				Logger.getInstance().logRobotThread(Level.WARNING, "No auto mode possible, defaulting to baseline!");
				selectedIndex = mAutoMap.get(alliance.toString());
			}
		} else if(priority == Priority.SWITCH) {
			if(switchIndex != -1) {
				selectedIndex = switchIndex;
			} else if(scaleIndex != -1) {
				selectedIndex = scaleIndex;
			} else {
				Logger.getInstance().logRobotThread(Level.WARNING, "No auto mode possible, defaulting to baseline!");
				selectedIndex = mAutoMap.get(alliance.toString());
			}
		} else {
			selectedIndex = (alliance == Alliance.BLUE) ? 0 : 1;
			Logger.getInstance().logRobotThread(Level.WARNING, "Priority not set, defaulting to baseline!");
		}

		AutoModeBase selectedAuto = mAutoModes.get(selectedIndex);
		Logger.getInstance().logRobotThread(Level.INFO, "Selected", selectedAuto);
		return selectedAuto;
	}

	/**
	 * Gets the names of all registered AutoModes
	 *
	 * @return ArrayList of AutoModes string name
	 * @see AutoModeBase#toString()
	 */
	public ArrayList<String> getAutoModeList() {
		ArrayList<String> list = new ArrayList<String>();
		for(AutoModeBase autoMode : mAutoModes) {
			list.add(autoMode.toString());
		}
		return list;
	}

	public JSONArray getAutoModeJSONList() {
		JSONArray list = new JSONArray();
		list.addAll(getAutoModeList());
		return list;
	}

	/**
	 * Attempt to set
	 *
	 * @return false if unable to find appropriate AutoMode
	 * @see AutoModeBase#toString()
	 */
	public AutoModeBase getAutoModeByName(String name) {
		if(!(mAutoMap.containsKey(name))) {
			Logger.getInstance().logRobotThread(Level.WARNING, "AutoModeSelector does not contain auto mode", name);
			return null;
		}
		int index = mAutoMap.get(name);
		Logger.getInstance().logRobotThread(Level.INFO, "Setting auto mode by name", name);
		return mAutoModes.get(index);
	}

	//TODO: what's this supposed to do
	/**
	 * Called during disabled in order to access dashboard and set auto mode
	 *
	 * @return
	 */
	public AutoModeBase getAutoModeFromDashboard(String selection) {
		return null;
	}

	public AutoModeBase getAutoModeByIndex(int index) {
		if(index < 0 || index >= mAutoModes.size()) {
			Logger.getInstance().logRobotThread(Level.WARNING, "Invalid AutoMode index, defautling to 0", index);
			index = 0;
		}
		Logger.getInstance().logRobotThread(Level.INFO, "Selected AutoMode by index", mAutoModes.get(index));
		return mAutoModes.get(index);
	}
}

