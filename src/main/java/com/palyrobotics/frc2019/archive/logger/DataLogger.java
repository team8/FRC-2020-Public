//package com.palyrobotics.frc2019.archive.logger;
//
//import com.google.common.base.Charsets;
//import com.google.common.io.Files;
//import edu.wpi.first.wpilibj.DriverStation;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.io.StringWriter;
//import java.time.ZonedDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.ConcurrentModificationException;
//import java.util.Objects;
//import java.util.Optional;
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReadWriteLock;
//import java.util.concurrent.locks.ReentrantReadWriteLock;
//import java.util.logging.Level;
//
///**
// * Log is at /home/lvuser/logs/ directory Unit test safe, creates diff file directory for Mac/Windows/Linux fileName defaults to ex: "Mar13 13-29" using
// * 24-hr-time Can set desired filename manually If log file exists on first start, automatically creates new file
// *
// * If run on roboRIO, will attempt to copy the driverstation console log to this directory to save it No longer uses bufferedwriter, uses Guava Files to append
// * to file
// *
// * FYI, buffered writer closes the underlying filewriter and flushes the buffer
// */
//public class DataLogger {
//	private static DataLogger instance = new DataLogger();
//
//	public static DataLogger getInstance() {
//		return instance;
//	}
//
//	//Default filename
//	private String fileName = "DEFAULT";
//
//	private boolean isEnabled = false;
//
//	private ArrayList<TimestampedString> mData = new ArrayList<>();
//	private ArrayList<LeveledString> mCycleLine = new ArrayList<>();
//
//	private Thread mWritingThread = null;
//	//Stores the runnable for the thread to be restarted
//	private Runnable mRunnable;
//
//	private final ReadWriteLock lock =  new ReentrantReadWriteLock();
//
//	private StringWriter sw = new StringWriter();
//	private PrintWriter pw = new PrintWriter(sw);
//
//	private int duplicatePrevent = 0;
//	private File mainLog;
//	private boolean fmsConnected;
//	private int writeLimit;
//
//	//Finds the driver station console output
//
//	public boolean setFileName(String fileName) {
//		if(mainLog != null) {
//			System.err.println("Already created log file");
//			return false;
//		}
//		this.fileName = fileName;
//		return true;
//	}
//
//	/**
//	 * Creates the file at desired filepath, avoids file collision, will not recreate if logger already there Also sanitizes inputs to prevent unwanted
//	 * directory creation
//	 */
//	public void start() {
//		if(fileName == "DEFAULT") {
//			System.err.println("WARNING: Using default filename!");
//		}
//		//Verifying file names
//		//this.fileName = fileName.replaceAll(File.separator, ":");
//		this.fileName = fileName.replaceAll(" ", "_");
//		this.fileName = fileName.replaceAll("/n", "_");
//		//If initialized before, then recreate the buffered writer and re-enable
//		if(mWritingThread != null) {
//			isEnabled = true;
//			mWritingThread = new Thread(mRunnable);
//			mWritingThread.start();
//			return;
//		}
//		String cDate = ZonedDateTime.now(LoggerConstants.tZone).format(DateTimeFormatter.ofPattern("MM-dd-yy"));
//		String cTime = ZonedDateTime.now(LoggerConstants.tZone).format(DateTimeFormatter.ofPattern("HH-mm"));
//		String os = System.getProperty("os.name");
//		String filePath = fileName + File.separatorChar + cDate + File.separatorChar + fileName + "-" + cTime;
//		//Changes directory based on competition status
//		try {
//			fmsConnected = DriverStation.getInstance().isFMSAttached();
//		} catch(UnsatisfiedLinkError|NoClassDefFoundError e) {
//		}
//		if(LoggerConstants.compStatus || fmsConnected) {
//			filePath = "COMPETITIONS" + File.separatorChar + filePath;
//		} else {
//			filePath = "PRACTICE" + File.separatorChar + filePath;
//		}
//		if(os.startsWith("Mac")) {
//			filePath = "logs" + File.separatorChar + filePath;
//		} else if(os.startsWith("Windows")) {
//			filePath = "." + File.separatorChar + "logs" + File.separatorChar + filePath;
//		} else if(os.startsWith("Linux")) {
//			//Pray that this is a roborio
//			filePath = "/home/lvuser/logs/" + filePath;
//		} else {
//			System.err.println("Error in determining OS name, reverting to RIO base");
//			filePath = "/home/lvuser/logs/" + filePath;
//		}
//		mainLog = new File(filePath + ".datalog");
//		while(mainLog.exists()) {
//			duplicatePrevent++;
//			mainLog = new File(filePath + duplicatePrevent + ".datalog");
//		}
//		try {
//			//File header
//			Files.createParentDirs(mainLog);
//			Files.append("Robot data log:" + "\n", mainLog, Charsets.UTF_8);
//			Files.append(filePath + "\n", mainLog, Charsets.UTF_8);
//		} catch(IOException e) {
//			System.err.println("Failed to create log at " + filePath);
//			e.printStackTrace();
//		}
//
//		//Create thread to write out logger
//		mWritingThread = new Thread(mRunnable);
//		isEnabled = true;
//		mWritingThread.start();
//	}
//
//
//	/**
//	 * Called on robot thread
//	 *
//	 * @param l
//	 *            Sets level of log message; determines writing to console and file
//	 * @param key
//	 *            String added to input object
//	 * @param value
//	 *            Object used for input; stores .toString() value
//	 */
//	public synchronized void logData(Level l, String key, Object value) {
//		Optional<Object> v = Optional.ofNullable(value);
//		Optional<String> k = Optional.ofNullable(key);
//		try {
//			mCycleLine.add(new LeveledString(l, k.orElse("NULL KEY"), checkStackTrace(l, v.orElse("NULL VALUE"))));
//		} catch(ConcurrentModificationException e) {
//			System.err.println("Attempted concurrent modification on robot logger");
//		}
//	}
//
//	/**
//	 * Checks if object passed needs to be converted into a stack trace
//	 * @param l Level
//	 * @param value Value to be checked
//	 * @return String
//	 */
//	private String checkStackTrace(Level l, Object value) {
//		if(LoggerConstants.writeStackTrace && value instanceof Throwable && l.intValue() <= LoggerConstants.traceLevel.intValue()) {
//			((Throwable) value).printStackTrace(pw);
//			String s = pw.toString();
//			pw.flush();
//			return s;
//		}
//		else {
//			return value.toString();
//		}
//	}
//
//	public synchronized void cleanup() {
//		if (isEnabled) {
//			mWritingThread.interrupt();
//		}
//	}
//
//	private DataLogger() {
//		mData = new ArrayList<>();
//		mRunnable = () -> {
//			while(true) {
//				writeLogs();
//				try {
//					Thread.sleep(500);
//				} catch(InterruptedException e) {
//					shutdown();
//					return;
//				}
//				//If thread is interrupted, cleanup
//				if(Thread.currentThread().isInterrupted()) {
//					shutdown();
//					return;
//				}
//			}
//		};
//	}
//
//	/**
//	 * Writes current log messages to file and console according to level Still supports deprecated log messages, will log all of them
//	 */
//	private void writeLogs() {
//		if(isEnabled) {
//			ArrayList<TimestampedString> mDataCopy;
//			final Lock w = lock.writeLock();
//		    w.lock();
//		    try {
//		    	mDataCopy = new ArrayList<>(mData);
//		    	mData.clear();
//		    } finally {
//		        w.unlock();
//		    }
//
//			writeLimit = 0;
//			try {
//				mDataCopy.removeIf(Objects::isNull);
//			}
//			catch(UnsupportedOperationException e) {
//				e.printStackTrace();
//			}
//			mDataCopy.sort(TimestampedString::compareTo);
//			mDataCopy.forEach(c -> {
//				try {
//						Files.append(c.getTimestampedString(), mainLog, Charsets.UTF_8);
//				} catch(IOException e) {
//					e.printStackTrace();
//				}
//			});
//
//		}
//
//	}
//
//	public synchronized void cycle() {
//
//
//		try {
//			mCycleLine.removeIf(Objects::isNull);
//		}
//		catch(UnsupportedOperationException e) {
//			e.printStackTrace();
//		}
//		mCycleLine.sort(LeveledString::compareTo);
//		ArrayList<LeveledString> mTemp = new ArrayList<>();
//		for (int i = mCycleLine.size() - 1; i >= 0; i--) {
//			boolean add = true;
//			for (int j = 0; j < mTemp.size(); j++) {
//				if (mCycleLine.get(i).getKey() == mTemp.get(j).getKey()) add = false;;
//			}
//			if (add) mTemp.add(mCycleLine.get(i));
//		}
//		mCycleLine = mTemp;
//
//		StringBuilder sb = new StringBuilder();
//		for (int i = 0; i < mCycleLine.size(); i++) {
//			LeveledString c = mCycleLine.get(i);
//			if(c.getLevel().intValue() >= LoggerConstants.dataWriteLevel.intValue()) {
//				sb.append(c.getKey() + ": " + c.getString());
//				if (i != mCycleLine.size() - 1) {
//					sb.append(", ");
//				}
//			}
//		}
//		mCycleLine.clear();
//		final Lock w = lock.writeLock();
//	    w.lock();
//	    try {
//	    	mData.add(new TimestampedString(sb.toString()));
//	    } finally {
//	        w.unlock();
//	    }
//
//	}
//
//	public String getLogPath() {
//		if(mainLog != null) {
//			return mainLog.getAbsolutePath();
//		} else {
//			return "NoLogYet";
//		}
//	}
//
//	//Used to cleanup internally, write out last words, etc
//	private void shutdown() {
//		writeLogs();
//		mCycleLine.clear();
//		final Lock w = lock.writeLock();
//		w.lock();
//	    try {
//			mData.clear();
//	    } finally {
//	        w.unlock();
//	    }
//
//		// try {
//		// 	Files.append("Logger stopped \n", mainLog, Charsets.UTF_8);
//		// } catch(IOException e) {
//		// 	e.printStackTrace();
//		// }
//		isEnabled = false;
//	}
//}