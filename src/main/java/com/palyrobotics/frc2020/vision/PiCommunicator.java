package com.palyrobotics.frc2020.vision;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class PiCommunicator {

	private static PiCommunicator sInstance = new PiCommunicator();

	public static PiCommunicator getInstance() {
		return sInstance;
	}

	private NetworkTableInstance sNetworkTableInstance = NetworkTableInstance.getDefault();
	private NetworkTable sPiTable = sNetworkTableInstance.getTable("pi-table");
	private static final String kPingKey = "ping-pi";
	private int kPingInt = 0;
	//public NetworkTableEntry commEntry = sPiTable.getEntry("comm-key");

	public PiCommunicator() {
		setupListeners();
	}

	public void ping() {
		sPiTable.getEntry(kPingKey).setNumber(kPingInt);
		kPingInt += 1;
		System.out.println("pinged at " + kPingKey);
	}

	void setupListeners() {
		sNetworkTableInstance = NetworkTableInstance.getDefault();
		sPiTable = sNetworkTableInstance.getTable("pi-table");
		sNetworkTableInstance.startClientTeam(8);
		System.out.println(sNetworkTableInstance + " pi table");
		sPiTable.addEntryListener((table, key, entry, value, flags) -> {
			System.out.println(key + " value change to: " + value.getValue());
		}, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

		try {
			Thread.sleep(10000);
		} catch (InterruptedException ex) {
			System.out.println("Interrupted");
			Thread.currentThread().interrupt();
			return;
		}

	}
}
