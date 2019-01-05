package com.palyrobotics.frc2018.vision;

import com.palyrobotics.frc2018.config.Constants;
import com.palyrobotics.frc2018.vision.networking.VisionDataReceiver;
import com.palyrobotics.frc2018.vision.networking.VisionVideoServer;
import com.palyrobotics.frc2018.vision.util.AbstractVisionThread;
import com.palyrobotics.frc2018.vision.util.commandline.CommandExecutor;
import com.palyrobotics.frc2018.vision.util.commandline.DeviceStatus;

import java.util.logging.Level;

/**
 * Handles starting the ADB server, finding the android device, starting the vision application, and starting networking sub-processes.
 *
 * @author Alvin
 */
public class VisionManager extends AbstractVisionThread {

	private static VisionManager s_Instance;

	/**
	 * @return The singleton
	 */
	public static VisionManager getInstance() {
		if (s_Instance == null)
			s_Instance = new VisionManager();
		return s_Instance;
	}

	protected VisionManager(final String threadName) {
		super(threadName);
	}

	public enum State {
        PRE_INITIALIZE, INITIALIZE_CMD_ENV, STARTING_SUB_PROCESSES, FINDING_DEVICE, STARTING_VISION_APP, STREAMING, GIVEN_UP
	}

	private State m_State = State.PRE_INITIALIZE;
	private VisionDataReceiver m_Receiver = new VisionDataReceiver();
	private int m_InitAdbRetryCount = 0;
	private boolean m_IsADBServerStarted = false;

	private VisionManager() {
		super("Vision Manager");
	}

	private void setState(State state) {
		m_State = state;
	}

	public boolean isADBServerStarted() {
	    return m_IsADBServerStarted;
    }

	@Override
	@Deprecated
	public void start(final long updateRate) {
		throw new RuntimeException();
	}

	public void start(final long updateRate, final boolean isTesting) {
		super.start(updateRate);
	}

	@Override
	public void init() {
		if (m_State != State.PRE_INITIALIZE)
			log(Level.WARNING, "Thread has been already initialized in initialization mode!");
		setState(State.INITIALIZE_CMD_ENV);
	}

    /**
     * Start the sub-processes, the receiver of video from the device and the sender to the driver station.
     *
     * @return The state after execution
     */
	private State startSubProcesses() {
		m_Receiver.start(Constants.kVisionVideoReceiverUpdateRate, Constants.kVisionVideoReceiverSocketPort, true);
		new VisionVideoServer().start(Constants.kVisionVideoServerUpdateRate, Constants.kVisionVideoSocketPort, true);
		return State.FINDING_DEVICE;
	}

	/**
	 * Try to find the android device via the adb server.
     * If the device can not be found, try restarting  the ADB server with an increasing timeout.
	 * 
	 * @return The state after execution
	 */
	private State findDevice() {
		if (CommandExecutor.getNexusStatus() != DeviceStatus.DEVICE) {
            if (Constants.kVisionUseTimeout) {
            	final long wait = Math.min(m_InitAdbRetryCount * 200, Constants.kVisionMaxTimeoutWait);
            	log(Level.INFO, String.format("Device could not be secured. Status: %s. Retrying in %d ms", CommandExecutor.getNexusStatus().toString(), wait));
                try {
                    Thread.sleep(wait);
                } catch (final InterruptedException e) {
                    log(Level.FINEST, e.toString());
                    return State.GIVEN_UP;
                }
				m_InitAdbRetryCount++;
			} else {
                return State.GIVEN_UP;
            }
            return State.FINDING_DEVICE;
        } else {
            return State.STARTING_VISION_APP;
        }
	}

    /**
     * Initialize the command line interface for the android device.
     * This initializes RIODroid.
     *
     * @return The state after execution
     */
	private State initializeCmdEnv() {
        try {
        	Thread.sleep(3000);
            CommandExecutor.setUpADB();
			log(Level.INFO, "Setting up ADB server...");
			m_IsADBServerStarted = true;
        } catch (final Exception e) {
            log(Level.FINEST, e.toString());
            log(Level.WARNING, "Exception when starting ADB server!");
            // TODO timeout here maybe
            return State.GIVEN_UP;
        }
        log(Level.INFO, "ADB server setup!");
        return State.STARTING_SUB_PROCESSES;
    }

	/**
	 * Sends adb command to boot up the vision app.
	 * 
	 * @return The state after execution
	 */
	private State startVisionApp() {
		try {
			CommandExecutor.startVisionApp();
			return State.STREAMING;
		} catch (final Exception e) {
			log(Level.FINEST, e.toString());
			return State.GIVEN_UP;
		}
	}

	public void verifyVisionAppIsRunning() {
		CommandExecutor.startVisionApp();
	}

	@Override protected void onPause() { }

	@Override protected void onResume() { }

	@Override protected void onStop() { }

	@Override
	public void update() {
		switch (m_State) {
			case PRE_INITIALIZE:
				break;
			case INITIALIZE_CMD_ENV:
				setState(initializeCmdEnv());
				break;
			case STARTING_SUB_PROCESSES:
				setState(startSubProcesses());
				break;
			case FINDING_DEVICE:
				setState(findDevice());
				break;
            case STARTING_VISION_APP:
                setState(startVisionApp());
                break;
			case STREAMING:
				break;
		}
	}
}
