package com.palyrobotics.frc2020.vision;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class PiCommunicator {

	private static PiCommunicator sInstance = new PiCommunicator();

	public static PiCommunicator getInstance() {
		return sInstance;
	}

	public PiCommunicator() {
		setupListeners();
	}

	private static final NetworkTableInstance sNetworkTableInstance = NetworkTableInstance.getDefault();
	private static final NetworkTable sPiTable = sNetworkTableInstance.getTable("pi-table");
	private static final String kPingKey = "ping-pi";
	public NetworkTableEntry commEntry = sPiTable.getEntry("comm-key");

	public void ping() {
		sPiTable.getEntry(kPingKey).setBoolean(true);
	}

	void setupListeners() {
		sPiTable.addEntryListener("comm-key", (table, key, entry, value, flags) -> {
			System.out.println("comm-key changed value: " + value.getValue());
		}, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

	}
}
