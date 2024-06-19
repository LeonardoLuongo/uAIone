package src.TorcsClient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Sampler 
{
    /*
     * Questa classe gestisce il campionamento e la scrittura dei dati in un file CSV.
     */
    public long index = 0;

    private static final int NUM_TRACK_EDGE_SENSORS = 19;
    private static final int NUM_FOCUS_SENSORS = 5;
    private static final int NUM_OPPONENT_SENSORS = 36;
    private static final int NUM_WHEEL_SPIN_VELOCITY = 4;

    private BufferedWriter bw;

    public Sampler(String filename) throws Exception 
    {
        // Normalize the filename to handle relative paths properly
        String filePath = new File(filename).getAbsolutePath();
        
        // Extract the directory path from the filePath
        String directoryPath = filePath.substring(0, filePath.lastIndexOf(File.separator));

        // Create the directory if it doesn't exist
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                System.out.println("Failed to create directory: " + directory.getAbsolutePath());
                return;
            }
        }
        
        emptyFile(filename);

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < NUM_TRACK_EDGE_SENSORS; i++) {
            sb.append("trackEdgeSensor_" + i + ",");
        }
        String trackEdgeSensors = sb.toString();
        
        sb = new StringBuilder();
        for (int i = 0; i < NUM_FOCUS_SENSORS; i++) {
            sb.append("focusSensor_" + i + ",");
        }
        String focusSensors = sb.toString();
        
        sb = new StringBuilder();
        for (int i = 0; i < NUM_OPPONENT_SENSORS; i++) 
        {
            sb.append("opponentSensor_" + i + ",");
        }
        String opponentSensors = sb.toString();

        sb = new StringBuilder();
        for (int i = 0; i < NUM_WHEEL_SPIN_VELOCITY; i++) {
            sb.append("wheelSpinVelocity_" + i + ",");
        }
        String wheelSpinVelocity = sb.toString();

        this.bw = new BufferedWriter(new FileWriter(filename, true), 8200); // (~8 KB)
        String header = filename + ",speed,angleToTrackAxis," + trackEdgeSensors + focusSensors + "gear," + opponentSensors +"racePosition," +
                "lateralSpeed,currentLapTime,damage,distanceFromStartLine,distanceRaced,fuelLevel,lastLapTime," +
                "rpm,trackPosition," + wheelSpinVelocity + "z,zSpeed,accel,brake,clutch,gear,steer,meta,focus";
        
        bw.write(header + "\n");
    }

    public void writeIntoDataset(String sample, String sampleIndex) throws Exception {
        if (sampleIndex == null) {
            sampleIndex = "sample " + index + ",";
            index++;
        }
        bw.write(sampleIndex + sample + "\n");
    }

    public void close() throws Exception {
        this.bw.close();
    }

    public static void emptyFile(String filename) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        writer.write("");
        writer.close();
    }
}