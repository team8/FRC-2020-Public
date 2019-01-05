package com.palyrobotics.frc2018.vision.util.synchronization;

public class AutoCloseableLock implements AutoCloseable {

	private int closableState = 0;
	private ReadWriteLock mLock;

	public AutoCloseableLock(ReadWriteLock lock, int lockState) throws Exception {
		this.closableState = lockState;
		this.mLock = lock;
		switch(this.closableState) {
			case ReadWriteLock.WRITING:
				mLock.lockWrite();
				break;
			case ReadWriteLock.READING:
				mLock.lockRead();
				break;
			case 0:
				throw new IllegalMonitorStateException("Lock " + mLock.getName() + " is trying to be closed in IDLE state");
		}
	}

	@Override
	public void close() throws Exception {
		switch(closableState) {
			case ReadWriteLock.WRITING:
				mLock.unlockWrite();
				break;
			case ReadWriteLock.READING:
				mLock.unlockRead();
				break;
			case 0:
				throw new IllegalMonitorStateException("Lock " + mLock.getName() + " is trying to be closed in IDLE state");
		}
	}
}