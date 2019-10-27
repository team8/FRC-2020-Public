//package com.palyrobotics.frc2019.archive.logger;
//
//import java.time.ZoneId;
//import java.util.logging.Level;
//
///**
// * Created on 10/27/17
// *
// * @author Joseph Rumelhart Replaces constant file for logger constants Avoids interference with other code, can be changed easily to account for time zone
// *         changes
// */
//public class LoggerConstants {
//	/**
//	 * Explanation of logger levels: <p>
//	 * SEVERE: Errors which will cause a major part of the program to stop working (1000) <p>
//	 * WARNING: Errors which will cause a minorfailure or indicate a risk of serious failure (900) <p>
//	 * INFO: Information on the normal functioning of the robot; Expect this level and above to be printed to console (800) <p>
//	 * CONFIG: Static configuration information (700) <p>
//	 * FINE: Basic tracing information (500) <p>
//	 * FINER: More detailed tracing information (400) <p>
//	 * FINEST: Most detailed tracing information, highest volume (300) <p>
//	 * ALL: Not actually a level, used to display information from all levels (Integer.MIN_VALUE)
//	 */
//	public static Level displayLevel = Level.INFO;
//	/**
//	 * Explanation of logger levels: <p>
//	 * SEVERE: Errors which will cause a major part of the program to stop working (1000) <p>
//	 * WARNING: Errors which will cause a minorfailure or indicate a risk of serious failure (900) <p>
//	 * INFO: Information on the normal functioning of the robot; Expect this level and above to be printed to console (800) <p>
//	 * CONFIG: Static configuration information (700) <p>
//	 * FINE: Basic tracing information (500) <p>
//	 * FINER: More detailed tracing information (400) <p>
//	 * FINEST: Most detailed tracing information, highest volume (300) <p>
//	 * ALL: Not actually a level, used to display information from all levels (Integer.MIN_VALUE)
//	 */
//	public static Level writeLevel = Level.FINE;
//	public static Level dataWriteLevel = Level.FINEST;
//
//	//Prevents the logger from printing too many messages to the console
//	public static int writeLimit = 10;
//
//	//Used to control display times
//	//Need to change for out of state competitions
//	public static ZoneId tZone = ZoneId.of("UTC-8");
//
//	//Controls if stack traces will be written
//	public static boolean writeStackTrace = true;
//	public static Level traceLevel = Level.ALL;
//
//	//Only set to TRUE when in a match
//	public static final boolean compStatus = false;
//}