package com.palyrobotics.frc2018.vision.util.synchronization;

import java.util.HashMap;
import java.util.Map;

public class ReadWriteLock {

	private static final int IDLE = 0;
	public static final int WRITING = 1;
	public static final int READING = 2;

	private Map<Thread, Integer> threadReadCount = new HashMap<>();

	private int writeAccesses = 0;
	private int writeRequests = 0;
	private Thread writingThread = null;

	private String name;

	public ReadWriteLock(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public synchronized void lockRead() throws InterruptedException {
		Thread callingThread = Thread.currentThread();
		while(!canGrantReadAccess(callingThread)) {
			wait();
		}
		threadReadCount.put(callingThread, (getReadAccessCount(callingThread) + 1));
	}

	public synchronized void unlockRead() {
		Thread callingThread = Thread.currentThread();
		if(!isReader(callingThread)) {
			throw new IllegalMonitorStateException("Calling Thread does not" + " hold a read lock on this ReadWriteLock");
		}
		int accessCount = getReadAccessCount(callingThread);
		if(accessCount == 1) {
			threadReadCount.remove(callingThread);
		} else {
			threadReadCount.put(callingThread, (accessCount - 1));
		}
		notifyAll();
	}

	private boolean canGrantReadAccess(Thread callingThread) {
		if(isWriter(callingThread))
			return true;
		if(hasWriter())
			return false;
		if(isReader(callingThread))
			return true;
		if(hasWriteRequests())
			return false;
		return true;
	}

	private boolean isReader(Thread callingThread) {
		return threadReadCount.get(callingThread) != null;
	}

	private boolean hasReaders() {
		return threadReadCount.size() > 0;
	}

	private boolean isOnlyReader(Thread callingThread) {
		return threadReadCount.size() == 1 && threadReadCount.get(callingThread) != null;
	}

	private int getReadAccessCount(Thread callingThread) {
		Integer accessCount = threadReadCount.get(callingThread);
		if(accessCount == null)
			return 0;
		return accessCount.intValue();
	}

	public synchronized void lockWrite() throws InterruptedException {
		writeRequests++;
		Thread callingThread = Thread.currentThread();
		while(!canGrantWriteAccess(callingThread)) {
			wait();
		}
		writeRequests--;
		writeAccesses++;
		writingThread = callingThread;
	}

	public synchronized void unlockWrite() throws InterruptedException {
		if(!isWriter(Thread.currentThread())) {
			throw new IllegalMonitorStateException("Calling Thread does not" + " hold the write lock on this ReadWriteLock");
		}
		writeAccesses--;
		if(writeAccesses == 0) {
			writingThread = null;
		}
		notifyAll();
	}

	private boolean canGrantWriteAccess(Thread callingThread) {
		if(isOnlyReader(callingThread))
			return true;
		if(hasReaders())
			return false;
		if(writingThread == null)
			return true;
		if(!isWriter(callingThread))
			return false;
		return true;
	}

	private boolean hasWriter() {
		return writingThread != null;
	}

	private boolean isWriter(Thread callingThread) {
		return writingThread == callingThread;
	}

	private boolean hasWriteRequests() {
		return this.writeRequests > 0;
	}
}
