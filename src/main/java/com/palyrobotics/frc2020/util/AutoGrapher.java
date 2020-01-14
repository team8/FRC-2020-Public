package com.palyrobotics.frc2020.util;

import com.palyrobotics.frc2020.auto.AutoModeBase;
import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AutoGrapher {

    public static void main(String... args) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, InterruptedException, NoSuchMethodException, InvocationTargetException {
        String autoClassName = System.getenv("AUTO_NAME");
        if (autoClassName != null && !autoClassName.isEmpty()) {
            String autoPath = "com.palyrobotics.frc2020.auto.modes." + autoClassName;

            Class<?> autoClass = Class.forName(autoPath);

            AutoModeBase auto = (AutoModeBase) autoClass.getDeclaredConstructor().newInstance();

            ArrayList<String> routineExport = new ArrayList<>();
            autoCompiler(auto.getRoutine(), routineExport);

            // Make export string that is readable in javascript
            String routineString = routineExport.toString();
            routineString = routineString.replace("Pose2d(Translation2d", "");
            routineString = routineString.replace("), Rotation2d(", ", ");
            routineString = routineString.replace("))", ")");
            routineString = routineString.replace("(", "{");
            routineString = routineString.replace(")", "}");

            String[] replace = {"X", "Y", "Rads", "Deg"};
            routineString = addQuotes(replace, routineString);

            String os = System.getProperty("os.name");

            // Export string as a text file
            File file = new File("auto-graph/automode.js");
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write("var importedAuto = " + routineString);
            }


            if (os.startsWith("Mac")) {
                Runtime.getRuntime().exec(new String[]{"open", "-a", "google chrome", System.getProperty("user.dir") + "/auto-graph/index.html"});
            } else if (os.startsWith("Windows")) {
                Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start chrome " + "file://" + System.getProperty("user.dir") + "/auto-graph/index.html"});
            }

            // Clear javascript auto mode file
            TimeUnit.SECONDS.sleep(5);
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write("");
            }
        }
    }

    public static void autoCompiler(Routine routines, List<String> export) {
        if (routines instanceof SequentialRoutine) {
            ArrayList<Routine> decomposedRoutine = routines.getEnclosingSequentialRoutine();
            for (Routine current : decomposedRoutine) {
                autoCompiler(current, export);
            }
        } else if (routines instanceof DrivePathRoutine) {
            export.add(routines.toString());
        } else {
            export.add("\"" + routines.getName() + "\"");
        }
    }

    private static String addQuotes(String[] query, String str) {
        for (String i : query) {
            str = str.replace(i, "\"" + i + "\"");
        }
        return str;
    }
}
