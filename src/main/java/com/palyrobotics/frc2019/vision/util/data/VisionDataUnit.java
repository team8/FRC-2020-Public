package com.palyrobotics.frc2018.vision.util.data;

import com.palyrobotics.frc2018.vision.util.synchronization.DataExistsCallback;

public class VisionDataUnit<T> {

	protected T value, defaultValue;

	protected DataExistsCallback<T> existsCallback;

	public VisionDataUnit(T value, T defaultValue, DataExistsCallback<T> existsCallback) {

		this.value = value;
		this.defaultValue = defaultValue;
		this.existsCallback = existsCallback;
	}

	public void set(T value) {

		this.value = value;
	}

	public void set(VisionDataUnit<T> value) {

		this.value = value.get();
	}

	public void setDefaultValue(T value) {

		defaultValue = value;
	}

	public T getDefaultValue() {
		return defaultValue;
	}

	public void setToDefault() {

		value = defaultValue;
	}

	public T get() {

		return exists() ? value : defaultValue;
	}

	public boolean isNull() {

		return value == null;
	}

	public boolean exists() {

		return existsCallback.exists(value);
	}
}
