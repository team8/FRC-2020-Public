package com.palyrobotics.frc2020.subsystems;

import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2020.config.PortConstants;
import com.palyrobotics.frc2020.config.subsystem.IndexerConfig;
import com.palyrobotics.frc2020.robot.*;
import com.palyrobotics.frc2020.util.CircularBuffer;
import com.palyrobotics.frc2020.util.Util;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.*;
import com.palyrobotics.frc2020.util.dashboard.LiveGraph;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.PowerDistributionPanel;

import java.util.List;

public class Indexer extends SubsystemBase {

	public static final int kTimeoutMs = 150;
	private static final String kLoggerTag = Util.classToJsonName(Indexer.class);
	public static final double kVoltageCompensation = 12.0;
	public static final SupplyCurrentLimitConfiguration k30AmpCurrentLimitConfiguration = new SupplyCurrentLimitConfiguration(
			true, 30.0, 35.0, 1.0);
	private final double kStuckPercent = 0.2, kForwardThreshold = -0.1;

	private static Indexer sInstance = new Indexer();
	private IndexerConfig mConfig = Configs.get(IndexerConfig.class);
	private ControllerOutput mMasterSparkOutput = new ControllerOutput(),
			mSlaveSparkOutput = new ControllerOutput(),
			mLeftVTalonOutput = new ControllerOutput(),
			mRightVTalonOutput = new ControllerOutput();
	private CircularBuffer<Double> mMasterVelocityFilter = new CircularBuffer<>(30);
	private boolean mHopperOutput, mBlockOutput;
	private static final PortConstants sPortConstants = Configs.get(PortConstants.class);

	private Indexer() {
	}

	public static Indexer getInstance() {
		return sInstance;
	}

	private final Spark masterSpark = new Spark(sPortConstants.nariIndexerMasterId, "Indexer Master"),
			slaveSpark = new Spark(sPortConstants.nariIndexerSlaveId, "Indexer Slave");
	private final List<Spark> sparks = List.of(masterSpark, slaveSpark);
	private final CANEncoder masterEncoder = masterSpark.getEncoder(), slaveEncoder = slaveSpark.getEncoder();
	private final Talon leftVTalon = new Talon(sPortConstants.nariIndexerLeftVTalonId, "Indexer Left V"),
			rightVTalon = new Talon(sPortConstants.nariIndexerRightVTalonId, "Indexer Right V");
	private final List<Talon> vTalons = List.of(leftVTalon, rightVTalon);
	private final TimedSolenoid hopperSolenoid = new TimedSolenoid(sPortConstants.nariIndexerHopperSolenoidId, 0.8, true),
			blockingSolenoid = new TimedSolenoid(sPortConstants.nariIndexerBlockingSolenoidId, 0.2, true);
	private final DigitalInput backInfrared = new DigitalInput(sPortConstants.nariIndexerBackInfraredDio),
			frontInfrared = new DigitalInput(sPortConstants.nariIndexerFrontInfraredDio),
			topInfrared = new DigitalInput(sPortConstants.nariIndexerTopInfraredDio);

	public void configureIndexerHardware() {
		// Sparks
		for (Spark spark : sparks) {
			spark.restoreFactoryDefaults();
			spark.enableVoltageCompensation(kVoltageCompensation);
			spark.setOpenLoopRampRate(0.0825);
			spark.setClosedLoopRampRate(0.0825);
			spark.setInverted(true);
			double maxOutput = 0.9;
			spark.setOutputRange(-maxOutput, maxOutput);
			spark.setSmartCurrentLimit((int) Math.round(40.0 / maxOutput));
			spark.setSecondaryCurrentLimit(70.0 / maxOutput, 10);
		}
		/* V-Belt Talons */
		for (Talon vTalon : vTalons) {
			vTalon.configFactoryDefault(kTimeoutMs);
			vTalon.enableVoltageCompensation(true);
			vTalon.configVoltageCompSaturation(kVoltageCompensation, kTimeoutMs);
			vTalon.configOpenloopRamp(0.1, kTimeoutMs);
			vTalon.configSupplyCurrentLimit(k30AmpCurrentLimitConfiguration, kTimeoutMs);
			vTalon.configFrameTimings(40, 40);
		}
		leftVTalon.setInverted(true);
		rightVTalon.setInverted(true);
	}

	@Override
	public void update(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		double multiplier = 1.0;
		if (state.indexerHasBackBall) {
			multiplier = 0.0;
		}
//		if ((Math.round(Timer.getFPGATimestamp() * mConfig.pulsePeriod) % 2L) == 0L) {
//			leftMultiplier = 0.0;
//			rightMultiplier = 0.6;
//		} else {
//			leftMultiplier = 0.4;
//			rightMultiplier = 0.0;
//		}

		switch (commands.indexerWantedBeltState) {
			case IDLE:
				mMasterSparkOutput.setIdle();
				mSlaveSparkOutput.setIdle();
				mLeftVTalonOutput.setIdle();
				mRightVTalonOutput.setIdle();
				mBlockOutput = true;
				break;
			case INDEX:
				if (state.gamePeriod == RobotState.GamePeriod.AUTO) {
					mMasterVelocityFilter.add(state.indexerMasterVelocity);
					if (mMasterVelocityFilter.numberOfOccurrences(d -> (d < mConfig.sparkIndexingOutput * kStuckPercent) && d > kForwardThreshold) > 20) {
						setVelocity(-mConfig.reversingOutput);
					} else {
						setProfiledVelocity(mConfig.sparkIndexingOutput);
					}
				} else {
					setProfiledVelocity(mConfig.sparkIndexingOutput);
				}
				setVTalonOutput(mConfig.leftTalonIndexingOutput, mConfig.rightTalonIndexingOutput, multiplier);
				mBlockOutput = true;
				break;
			case WAITING_TO_FEED:
				mMasterSparkOutput.setIdle();
				mSlaveSparkOutput.setIdle();
				mLeftVTalonOutput.setIdle();
				mRightVTalonOutput.setIdle();
				mBlockOutput = false;
				break;
			case FEED_SINGLE:
			case FEED_ALL:
				if (state.gamePeriod == RobotState.GamePeriod.AUTO) {
					mMasterVelocityFilter.add(state.indexerMasterVelocity);
					if (mMasterVelocityFilter.numberOfOccurrences(d -> (d < mConfig.feedingOutput * kStuckPercent) && d > kForwardThreshold) > 20) {
						setVelocity(-mConfig.reversingOutput);
					} else {
						setProfiledVelocity(mConfig.feedingOutput);
					}
				} else {
					setProfiledVelocity(mConfig.feedingOutput);
				}
				setVTalonOutput(mConfig.leftTalonIndexingOutput, mConfig.rightTalonIndexingOutput, multiplier);
				mBlockOutput = false;
				break;
			case REVERSING:
				setVelocity(-mConfig.reversingOutput);
				setVTalonOutput(-mConfig.leftTalonIndexingOutput, -mConfig.rightTalonIndexingOutput, multiplier);
				mBlockOutput = false;
				break;
			case MANUAL:
				setProfiledVelocity(commands.indexerManualVelocity);
				setVTalonOutput(
						Math.signum(commands.indexerManualVelocity) * mConfig.leftTalonIndexingOutput,
						Math.signum(commands.indexerManualVelocity) * mConfig.rightTalonIndexingOutput, multiplier);
				break;
		}
		if (commands.indexerWantedBeltState != BeltState.INDEX && commands.indexerWantedBeltState != BeltState.FEED_ALL) mMasterVelocityFilter.clear();
		mHopperOutput = commands.indexerWantedHopperState == HopperState.OPEN;
	}

	private void setVTalonOutput(double leftOutput, double rightOutput, double multiplier) {
		mLeftVTalonOutput.setPercentOutput(leftOutput * multiplier);
		mRightVTalonOutput.setPercentOutput(rightOutput * multiplier);
	}

	private void setProfiledVelocity(double velocity) {
		mMasterSparkOutput.setTargetVelocityProfiled(velocity, mConfig.masterVelocityGains);
		mSlaveSparkOutput.setTargetVelocityProfiled(velocity, mConfig.slaveVelocityGains);
	}

	private void setVelocity(double velocity) {
		var masterGains = new Gains(mConfig.masterVelocityGains.p, mConfig.masterVelocityGains.i, mConfig.masterVelocityGains.d, mConfig.masterVelocityGains.f, mConfig.masterVelocityGains.iZone, mConfig.masterVelocityGains.iMax);
		var slaveGains = new Gains(mConfig.slaveVelocityGains.p, mConfig.slaveVelocityGains.i, mConfig.slaveVelocityGains.d, mConfig.slaveVelocityGains.f, mConfig.slaveVelocityGains.iZone, mConfig.slaveVelocityGains.iMax);
		mMasterSparkOutput.setTargetVelocity(velocity, masterGains);
		mSlaveSparkOutput.setTargetVelocity(velocity, slaveGains);
	}

	public ControllerOutput getMasterSparkOutput() {
		return mMasterSparkOutput;
	}

	public ControllerOutput getSlaveSparkOutput() {
		return mSlaveSparkOutput;
	}

	public ControllerOutput getLeftVTalonOutput() {
		return mLeftVTalonOutput;
	}

	public ControllerOutput getRightVTalonOutput() {
		return mRightVTalonOutput;
	}

	public boolean getHopperOutput() {
		return mHopperOutput;
	}

	public boolean getBlockOutput() {
		return mBlockOutput;
	}

	public enum BeltState {
		IDLE, INDEX, WAITING_TO_FEED, FEED_SINGLE, FEED_ALL, REVERSING, MANUAL
	}

	public enum HopperState {
		OPEN, CLOSED
	}

	public void updateIndexer() {
		vTalons.forEach(Talon::handleReset);
		masterSpark.setOutput(getMasterSparkOutput());
		slaveSpark.setOutput(getSlaveSparkOutput());
		hopperSolenoid.setExtended(getHopperOutput());
		blockingSolenoid.setExtended(getBlockOutput());
		leftVTalon.setOutput(getLeftVTalonOutput());
		rightVTalon.setOutput(getRightVTalonOutput());
		handleReset(slaveSpark);
		handleReset(masterSpark);
		LiveGraph.add("indexerMasterAppliedOutput", masterSpark.getAppliedOutput());
		LiveGraph.add("indexerMasterVelocity", masterEncoder.getVelocity());
		LiveGraph.add("indexerSlaveAppliedOutput", slaveSpark.getAppliedOutput());
		LiveGraph.add("indexerSlaveVelocity", slaveEncoder.getVelocity());
		LiveGraph.add("indexerTargetVelocity", getMasterSparkOutput().getReference());
		PowerDistributionPanel pdp = HardwareAdapter.MiscellaneousHardware.getInstance().pdp;
		LiveGraph.add("indexerCurrent10", pdp.getCurrent(10));
		LiveGraph.add("indexerCurrent11", pdp.getCurrent(11));
//		LiveGraph.add("intakeCurrent8", pdp.getCurrent(8));
//		LiveGraph.add("totalCurrent", pdp.getTotalCurrent());
//		LiveGraph.add("batteryVoltage", RobotController.getBatteryVoltage());
	}

	private void handleReset(Spark spark) {
		if (spark.getStickyFault(CANSparkMax.FaultID.kHasReset)) {
			spark.clearFaults();
			Log.error(kLoggerTag, String.format("%s spark reset", spark.getName()));
		}
	}
}
