package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.util.input.XboxController;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;

public class Miscellaneous {

    private static Miscellaneous sInstance;

    public final Compressor compressor = new Compressor();
    public final PowerDistributionPanel pdp = new PowerDistributionPanel();
//		final UsbCamera fisheyeCam = CameraServer.getInstance().startAutomaticCapture();

    public final Joystick driveStick = new Joystick(0), turnStick = new Joystick(1);
    public final XboxController operatorXboxController = new XboxController(2);

    public static Miscellaneous getInstance() {
        if (sInstance == null) sInstance = new Miscellaneous();
        return sInstance;
    }

    public void configureMiscellaneousHardware() {
        pdp.clearStickyFaults();
        compressor.clearAllPCMStickyFaults();
    }
}
