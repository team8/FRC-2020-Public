package com.palyrobotics.frc2019.util.config;

import java.io.IOException;

public abstract class AbstractConfig {
    @Override
    public String toString() {
        try {
            return Configs.getMapper().defaultPrettyPrintingWriter().writeValueAsString(this);
        } catch (IOException exception) {
            exception.printStackTrace();
            return super.toString();
        }
    }

    protected void onPostUpdate() {}
}
