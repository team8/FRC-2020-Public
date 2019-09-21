package com.palyrobotics.frc2019.util.configv2;

import java.io.IOException;

public abstract class AbstractConfig {
    @Override
    public String toString() {
        try {
            return Configs.getMapper().defaultPrettyPrintingWriter().writeValueAsString(this);
        } catch (IOException exception) {
            exception.printStackTrace();
            return "Invalid";
        }
    }
}
