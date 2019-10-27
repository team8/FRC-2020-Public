//package com.palyrobotics.frc2019.auto;
//
//import com.palyrobotics.frc2019.auto.modes.Fanlin;
//import com.palyrobotics.frc2019.auto.testautos.FTestAuto;
//import org.json.simple.JSONArray;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
///**
// * @author Nihar, based off Team 254 2015
// */
//public class AutoModeSelector {
//	private static AutoModeSelector instance = null;
//	private ArrayList<AutoModeBase> mAutoModes = new ArrayList<>();
//	private HashMap<String, Integer> mAutoMap = new HashMap<>();
//
//	public static AutoModeSelector getInstance() {
//		if(instance == null) {
//			instance = new AutoModeSelector();
//		}
//		return instance;
//	}
//
//	protected AutoModeSelector() {
//        // left to right, blue alliance to red alliance
//        /* 0 */ registerAutonomous(new FTestAuto(), 0);
//
//	}
//
//	/**
//	 * Add an AutoMode to list to choose from
//	 *
//	 * @param auto
//	 *            AutoMode to add
//	 */
//	public void registerAutonomous(AutoModeBase auto, int id) {
//		mAutoModes.add(auto);
//		mAutoMap.put(auto.getKey(), id);
//	}
//
//	/**
//	 * Get the currently selected AutoMode
//	 *
//	 * @return AutoMode currently selected
//	 */
//	public AutoModeBase getAutoMode() {
//		return new Fanlin();
//	}
//
//	/**
//	 * Gets the names of all registered AutoModes
//	 *
//	 * @return ArrayList of AutoModes string name
//	 * @see AutoModeBase#toString()
//	 */
//	public ArrayList<String> getAutoModeList() {
//		ArrayList<String> list = new ArrayList<String>();
//		for(AutoModeBase autoMode : mAutoModes) {
//			list.add(autoMode.toString());
//		}
//		return list;
//	}
//
//	public JSONArray getAutoModeJSONList() {
//		JSONArray list = new JSONArray();
//		list.addAll(getAutoModeList());
//		return list;
//	}
//
//	/**
//	 * Attempt to set
//	 *
//	 * @return false if unable to find appropriate AutoMode
//	 * @see AutoModeBase#toString()
//	 */
//	public AutoModeBase getAutoModeByName(String name) {
//		if(!(mAutoMap.containsKey(name))) {
////			Logger.getInstance().logRobotThread(Level.WARNING, "AutoModeSelector does not contain auto mode", name);
//			return null;
//		}
//		int index = mAutoMap.get(name);
////		Logger.getInstance().logRobotThread(Level.INFO, "Setting auto mode by name", name);
//		return mAutoModes.get(index);
//	}
//
//	/**
//	 * Called during disabled in order to access dashboard and set auto mode
//	 *
//	 * @return
//	 */
//	public AutoModeBase getAutoModeFromDashboard(String selection) {
//		return null;
//	}
//
//	public AutoModeBase getAutoModeByIndex(int index) {
//		if(index < 0 || index >= mAutoModes.size()) {
////			Logger.getInstance().logRobotThread(Level.WARNING, "Invalid AutoMode index, defautling to 0", index);
//			index = 0;
//		}
////		Logger.getInstance().logRobotThread(Level.INFO, "Selected AutoMode by index", mAutoModes.get(index));
//		return mAutoModes.get(index);
//	}
//}
//
