package com.palyrobotics.frc2018.vision.util;

import com.palyrobotics.frc2018.util.logger.Logger;

import java.util.logging.Level;

/**
 * Base class for vision threads.
 *
 * @author Quintin Dwight
 */
public abstract class AbstractVisionThread implements Runnable {

	public enum ThreadState {
		PRE_INIT, RUNNING, PAUSED, STOPPED
	}

	protected double m_SecondsAlive;
	protected long m_UpdateRate;
	protected boolean m_IsRunning;
	protected ThreadState m_ThreadState = ThreadState.PRE_INIT;
	protected final String fm_Name;

	public double getTimeAlive() { return m_SecondsAlive; }
	public boolean isRunning() { return m_IsRunning; }

	protected AbstractVisionThread(final String threadName) {
		fm_Name = threadName;
	}

	protected void setThreadState(final ThreadState state) {
		m_ThreadState = state;
	}

	/**
	 * Starts the thread with a specific update rate.
	 */
	public void start(final long updateRate) {
		m_ThreadState = ThreadState.PRE_INIT;
		m_UpdateRate = updateRate;
		if (m_IsRunning) {
			log(Level.FINEST, "Thread is already running! Aborting...");
			return;
		}
		init();
		log(Level.FINEST, "Starting thread...");
		m_IsRunning = true;
		m_ThreadState = ThreadState.RUNNING;
		new Thread(this).start();
	}

	/**
	 * Called by {@link #start} after it has been verified that the thread can run.
	 */
	protected abstract void init();

	@Override
	public void run() {
		while (m_IsRunning) {
			if (m_ThreadState == ThreadState.PRE_INIT) {
				log(Level.WARNING, "Thread has not been initialized in running state! Aborting...");
				return;
			}
			update();
			if (m_UpdateRate >= 0) {
				try {
					Thread.sleep(m_UpdateRate);
					m_SecondsAlive += m_UpdateRate / 1000.0;
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Called by {@link #run} every time the thread updates.
	 */
	protected abstract void update();

	/**
	 * Pauses the thread
	 */
	public void pause() {
		log(Level.FINEST, "Pausing thread...");
		if (!m_IsRunning)
			log(Level.FINEST, "Cannot pause a thread that isn't running!");
		setThreadState(ThreadState.PAUSED);
		onPause();
	}

	protected abstract void onPause();

	/**
	 * Resume the thread.
	 */
	public void resume() {
		if (m_ThreadState == ThreadState.STOPPED || !m_IsRunning)
			log(Level.WARNING, "Thread is not running, cannot be resumed from a stopped state!");
		setThreadState(ThreadState.RUNNING);
		onResume();
	}

	protected void log(final Level level, final String message) {
		final String finalMessage = String.format("[%s] %s", fm_Name, message);
		Logger.getInstance().logRobotThread(level, finalMessage);
	}

	protected abstract void onResume();

	/**
	 * Stops the thread completely.
	 */
	public void stop() {
		log(Level.FINEST, "Stopping thread...");
		m_IsRunning = false;
		setThreadState(ThreadState.STOPPED);
		onStop();
	}

	protected abstract void onStop();
}
