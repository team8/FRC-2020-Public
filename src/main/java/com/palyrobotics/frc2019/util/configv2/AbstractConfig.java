package com.palyrobotics.frc2019.util.configv2;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public abstract class AbstractConfig {
    @Override
    public String toString() {
        try {
            return new ObjectMapper().defaultPrettyPrintingWriter().writeValueAsString(this);
        } catch (IOException exception) {
            exception.printStackTrace();
            return "Invalid";
        }
    }
}
