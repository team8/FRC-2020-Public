package com.palyrobotics.frc2019.config.configv2;

import com.palyrobotics.frc2019.util.configv2.AbstractConfig;

import java.util.ArrayList;
import java.util.List;

public class ServiceConfig extends AbstractConfig {

    public boolean competitionMode;
    public List<String> enabledServices = new ArrayList<>();
}
