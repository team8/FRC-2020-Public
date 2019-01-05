package com.palyrobotics.frc2018.vision.util.commandline;

import com.palyrobotics.frc2018.util.logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class RuntimeExecutor {

    /**
     * Executes a command in the command line during runtime
     *
     * @param command Command to execute
     * @return Console output of executing the command
     */
    public static String exec(final String command) {
        // Builds the output of the console
        final StringBuilder out = new StringBuilder();
        try {
            String line;
            // Execute the command as a process
            final Process p = Runtime.getRuntime().exec(command);
            // Read in console output from the process object
            final BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                out.append(line);
                out.append("\n");
            }
            input.close();
        } catch (final IOException e) {
            Logger.getInstance().logRobotThread(Level.FINEST, e);
        }
        return out.toString();
    }
}