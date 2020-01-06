package com.palyrobotics.frc2020.auto;

import com.palyrobotics.frc2020.auto.AutoModeBase.Alliance;
import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.config.AutoDistances;

import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class AutoGrapher {

    @Test
    public void showAuto() throws IOException {
    	AutoDistances.updateAutoDistances();
        for (int i = 0; i < 26; i++) {
        	AutoModeBase.mAlliance = (i%2==0 ? Alliance.BLUE: Alliance.RED);
            loadAutos(AutoModeSelector.getInstance().getAutoModeByIndex(i), (i%2==0 ? "Blue" : "Red"));
        }

        String os = System.getProperty("os.name");
        if(os.startsWith("Mac")) {
            Runtime.getRuntime().exec( new String[] { "open" , "-a", "Safari", System.getProperty("user.dir") + "/auto-graph/index.html"});
        } else if(os.startsWith("Windows")) {
            Runtime.getRuntime().exec(new String[]{"cmd", "/c","start chrome " + System.getProperty("user.dir") + "/auto-graph/index.html"});
        }
    }

    public void loadAutos(AutoModeBase auto, String color) throws IOException {
        AutoModeBase base = auto;
        base.prestart();
        String pathType = base.getClass().getName().split("\\.")[base.getClass().getName().split("\\.").length-1] + color;
        System.out.println("Loading " + pathType);
        FileOutputStream out = new FileOutputStream("auto-graph/"+pathType + ".txt");
        ArrayList<Routine> pathExport = base.getRoutine().getEnclosingSequentialRoutine();
        for (int i = 0; i < pathExport.size(); i++) {
            System.out.println(pathExport.get(i).getName());
            if (pathExport.get(i) instanceof DrivePathRoutine) {
                out.write(pathExport.get(i).toString().getBytes());
            }

            if (pathExport.get(i).getEnclosingSequentialRoutine() != null) {
                for (int j = 0; j < pathExport.get(i).getEnclosingSequentialRoutine().size(); j++) {
                    if (pathExport.get(i).getEnclosingSequentialRoutine().get(j) instanceof DrivePathRoutine) {
                        out.write(pathExport.get(i).getEnclosingSequentialRoutine().get(j).toString().getBytes());
                    }

                }
            }
            
            if (pathExport.get(i).getEnclosingParallelRoutine() != null) {
                for (int j = 0; j < pathExport.get(i).getEnclosingParallelRoutine().size(); j++) {
                    if (pathExport.get(i).getEnclosingParallelRoutine().get(j) instanceof DrivePathRoutine) {
                        out.write(pathExport.get(i).getEnclosingParallelRoutine().get(j).toString().getBytes());
                    }

                }
            }
        }
        out.close();
    }
}
