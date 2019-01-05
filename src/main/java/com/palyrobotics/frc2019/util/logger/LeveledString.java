package com.palyrobotics.frc2018.util.logger;

import java.util.logging.Level;

/**
 * Extenstion of TimestamptedString to include logger levels
 * 
 * @author Joseph Rumelhart
 *
 */
public class LeveledString extends TimestampedString {
	Level mLevel;

	public LeveledString(Level l, String string) {
		super(string);
		mLevel = l;
	}

	public Level getLevel() {
		return mLevel;
	}

	public String getLeveledString() {
		return mLevel.toString() + ": " + this.getTimestampedString();
	}
}