package com.palyrobotics.frc2020.util.service;

import java.io.PrintWriter;
import java.io.StringWriter;

@SuppressWarnings ("java:S1104")
public class LogEntry {

	public long timeStampMilliseconds;
	public int level;
	public String category, message;
	public Throwable cause;

	public LogEntry(long timeStampMilliseconds, int level, String category, String message, Throwable cause) {
		this.timeStampMilliseconds = timeStampMilliseconds;
		this.level = level;
		this.category = category;
		this.message = message;
		this.cause = cause;
	}

	@Override
	public String toString() {
		var log = new StringBuilder(256);
		long minutes = timeStampMilliseconds / (1000 * 60), seconds = timeStampMilliseconds / (1000) % 60;
		if (minutes <= 9) log.append('0');
		log.append(minutes);
		log.append(':');
		if (seconds <= 9) log.append('0');
		log.append(seconds);
		switch (level) {
			case 5:
				log.append(" ERROR: ");
				break;
			case 4:
				log.append("  WARN: ");
				break;
			case 3:
				log.append("  INFO: ");
				break;
			case 2:
				log.append(" DEBUG: ");
				break;
			case 1:
				log.append(" TRACE: ");
				break;
			default:
				log.append(" OTHER ");
		}
		if (category != null) {
			log.append('[').append(category).append("] ");
		} else {
			log.append("[General] ");
		}
		log.append(message);
		if (cause != null) {
			var writer = new StringWriter(256);
			cause.printStackTrace(new PrintWriter(writer));
			log.append('\n').append(writer.toString().trim());
		}
		return log.toString();
	}
}
