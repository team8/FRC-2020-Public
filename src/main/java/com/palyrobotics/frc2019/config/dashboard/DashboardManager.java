package com.palyrobotics.frc2019.config.dashboard;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class DashboardManager {

    //Usage of cantable or not
    private boolean mEnableCANTable = true;

    //Allow motion profile gains to be modified over NT
    public final boolean mPidTuning = false;

    private static DashboardManager sInstance = new DashboardManager();

    private static final String TABLE_NAME = "RobotTable", CAN_TABLE_NAME = "data_table";

    private NetworkTableInstance mNetInstance;

    private NetworkTable mRobotTable, mCANTable;

    public static DashboardManager getInstance() {
        return sInstance;
    }

    private boolean isUnitTest;

    private DashboardManager() {
    }

    public void robotInit() {
        try {
            initializeRobotTable();
            initializeCANTable();
//            Logger.getInstance().logRobotThread(Level.FINE, "Successfully initialized cantables");
        } catch (Exception e) {
            isUnitTest = true;
        }
    }

    private void initializeRobotTable() {
        this.mNetInstance = NetworkTableInstance.getDefault();
        this.mRobotTable = mNetInstance.getTable(TABLE_NAME);
    }

    private void initializeCANTable() {
        //Gains.initNetworkTableGains();
        if (mEnableCANTable) {
            this.mCANTable = mNetInstance.getTable(CAN_TABLE_NAME);
            mNetInstance.setUpdateRate(.005);
        }
    }

    /**
     * Publishes a KV pair to the Network Table.
     *
     * @param d The dashboard value.
     */
    public void publishKVPair(DashboardValue d) {
        if (mRobotTable == null) {
            try {
                initializeRobotTable();
            } catch (UnsatisfiedLinkError | NoClassDefFoundError e) {
                isUnitTest = true;
            }
        }
        //If we are now connected
        if (mRobotTable != null && !isUnitTest) {
            this.mRobotTable.getEntry(d.getKey()).setString(d.getValue());
        }
    }

    public void updateCANTable(String key, String value) {
        if (!mEnableCANTable || isUnitTest) {
            return;
        }
        if (mCANTable != null) {
            this.mCANTable.getEntry(key).setString(value + "\n");
        } else {
            //try to reach it again
            try {
                initializeCANTable();
            } catch (UnsatisfiedLinkError | NoClassDefFoundError e) {
//                Logger.getInstance().logRobotThread(Level.WARNING, e);
                isUnitTest = true;
            }
        }
    }

    /**
     * Start or stop sending cantable data
     *
     * @param start true if you want to start sending data
     */
    public void toggleCANTable(boolean start) {
        if (start) {
            if (mCANTable != null && !isUnitTest) {
//                Logger.getInstance().logRobotThread(Level.FINER, "Started CANTables");
                this.mCANTable.getEntry("start").setString("true");
                this.mCANTable.getEntry("end").setString("false");
            } else {
//                Logger.getInstance().logRobotThread(Level.WARNING, "Error in CANTables");
            }
        } else {
            if (mCANTable != null && !isUnitTest) {
                this.mCANTable.getEntry("start").setString("false");
                this.mCANTable.getEntry("end").setString("true");
            }
        }
    }
}
