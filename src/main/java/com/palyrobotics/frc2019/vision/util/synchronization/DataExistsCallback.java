package com.palyrobotics.frc2019.vision.util.synchronization;

public class DataExistsCallback<T> {

	public boolean exists(T data) {
		return data != null;
	}
}
