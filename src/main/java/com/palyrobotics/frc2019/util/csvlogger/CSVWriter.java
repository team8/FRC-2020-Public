package com.palyrobotics.frc2019.util.csvlogger;

import com.palyrobotics.frc2019.config.RobotState;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jason Liu
 */
public class CSVWriter {
    private ArrayList<HashMap> mData = new ArrayList<>();

    private static final String FILENAME = "/home/lvuser/canlog.csv";
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";

    private static CSVWriter instance = new CSVWriter();

    public static CSVWriter getInstance() {
        return instance;
    }

    public void cleanFile() {
        File file = new File(FILENAME);
        if(file.exists()) {
            file.delete();
            // System.out.println("Removed old log file");
        }
    }

    public void addData(String key, double value) {
        HashMap<String, double[]> mCycleData = new HashMap<>();
        double[] values = new double[] {(System.currentTimeMillis() - RobotState.getInstance().matchStartTime)/1000, value};
        mCycleData.put(key, values);
        mData.add(mCycleData);
    }

    public int getSize() {
        return mData.size();
    }

    public void write(){
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(FILENAME, true);
            for(HashMap<String, double[]> map: mData) {
                for(Map.Entry<String, double[]> entry: map.entrySet()) {
                    fileWriter.append(entry.getKey());
                    fileWriter.append(COMMA_DELIMITER);
                    fileWriter.append(Double.toString(entry.getValue()[0]));
                    fileWriter.append(COMMA_DELIMITER);
                    fileWriter.append(Double.toString(entry.getValue()[1]));
                    fileWriter.append(NEW_LINE_SEPARATOR);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                mData = new ArrayList<>();
                fileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}