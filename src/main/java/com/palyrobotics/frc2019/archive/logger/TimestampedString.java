//package com.palyrobotics.frc2019.archive.logger;
//
//import java.time.ZoneId;
//import java.time.ZonedDateTime;
//import java.time.format.DateTimeFormatter;
//
///**
// * Created by Nihar on 3/13/17. Stores a String with the timestamp on construction Allows data structures to sort the strings by timestamp And then retrieve the
// * String Also automatically adds a newline to the end
// *
// * Updated by Joseph 10/24/2017 Replaced system time with zonedTime using external time zone constant Still stored as UTC
// */
//public class TimestampedString implements Comparable<TimestampedString> {
//	private String mString;
//	private String mKey;
//	private ZonedDateTime mTime;
//
//	public TimestampedString(String string) {
//		mString = string;
//		mTime = ZonedDateTime.now(ZoneId.of("Z"));
//	}
//
//	public TimestampedString(String key, String string) {
//		this(string);
//		mKey = key;
//	}
//
//	public ZonedDateTime getTimestamp() {
//		return mTime;
//	}
//
//	public String getString() {
//		return mString;
//	}
//
//	public String getKey() {
//		return mKey;
//	}
//
//	/**
//	 * Outputs time as local time format
//	 *
//	 * @return Time in requisite format
//	 */
//	public String getTimestampedString() {
//		return (mTime.withZoneSameInstant(LoggerConstants.tZone).format(DateTimeFormatter.ISO_LOCAL_TIME)) + (mKey==null ? "" : ": " + mKey )+ ": " + mString + "\n";
//	}
//
//	@Override
//	public String toString() {
//		return getTimestampedString();
//	}
//
//	@Override
//	public int compareTo(TimestampedString o) {
//		return mTime.compareTo(o.getTimestamp());
//	}
//}
