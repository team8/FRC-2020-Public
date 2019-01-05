package com.palyrobotics.frc2018.config.dashboard;

import com.palyrobotics.frc2018.util.logger.Logger;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

import java.util.logging.Level;

public class DashboardManager {

	//Usage of cantable or not
	private boolean enableCANTable = true;

	//Allow motion profile gains to be modified over NT
	public final boolean pidTuning = false;

	private static DashboardManager instance = new DashboardManager();

	public static final String TABLE_NAME = "RobotTable";
	public static final String CAN_TABLE_NAME = "data_table";

	public NetworkTableInstance net_instance;

	private NetworkTable robotTable;
	private NetworkTable canTable;

	public static DashboardManager getInstance() {
		return instance;
	}

	private boolean isUnitTest;

	private DashboardManager() {
	}

	public void robotInit() {
		try {
			initializeRobotTable();
			initializeCANTable();
			Logger.getInstance().logRobotThread(Level.FINE, "Successfully initialized cantables");
		} catch (Exception e) {
			isUnitTest = true;
		}
	}

	public void initializeRobotTable() {
		this.net_instance = NetworkTableInstance.getDefault();
		this.robotTable = net_instance.getTable(TABLE_NAME);
	}

	public void initializeCANTable() {
		//Gains.initNetworkTableGains();
		if(enableCANTable) {
			this.canTable = net_instance.getTable(CAN_TABLE_NAME);
			net_instance.setUpdateRate(.005);
		}
	}

	/**
	 * Publishes a KV pair to the Network Table.
	 * 
	 * @param d
	 *            The dashboard value.
	 */
	public void publishKVPair(DashboardValue d) {
		if(robotTable == null) {
			try {
				initializeRobotTable();
			} catch(UnsatisfiedLinkError e) {
				isUnitTest = true;
			} catch(NoClassDefFoundError e) {
				isUnitTest = true;
			}
		}

		//If we are now connected
		if(robotTable != null && !isUnitTest) {
			this.robotTable.getEntry(d.getKey()).setString(d.getValue());
		}
	}

	public void updateCANTable(String key, String value) {
		if(!enableCANTable || isUnitTest) {
			return;
		}
		if(canTable != null) {
			this.canTable.getEntry(key).setString(value + "\n");
		} else {
			//try to reach it again
			try {
				initializeCANTable();
			} catch(UnsatisfiedLinkError e) {
				Logger.getInstance().logRobotThread(Level.WARNING, e);
				isUnitTest = true;
			} catch(NoClassDefFoundError e) {
				Logger.getInstance().logRobotThread(Level.WARNING, e);
				isUnitTest = true;
			}
		}
	}

	/**
	 * Start or stop sending cantable data
	 * 
	 * @param start
	 *            true if you want to start sending data
	 */
	public void toggleCANTable(boolean start) {
		if(start) {
			if(canTable != null && !isUnitTest) {
				Logger.getInstance().logRobotThread(Level.FINER, "Started CANTables");
				this.canTable.getEntry("start").setString("true");
				this.canTable.getEntry("end").setString("false");
			} else {
				Logger.getInstance().logRobotThread(Level.WARNING, "Error in CANTables");
			}
		} else {
			if(canTable != null && !isUnitTest) {
				this.canTable.getEntry("start").setString("false");
				this.canTable.getEntry("end").setString("true");
			}
		}
	}
}
