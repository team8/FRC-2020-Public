package com.palyrobotics.frc2020.util.config;

public abstract class ConfigBase {

	@Override
	public String toString() {
		return Configs.toJson(this);
	}

	void onPostUpdate() {
	}
}
