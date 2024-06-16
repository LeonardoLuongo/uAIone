

package scr;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Logger
 */
public class Logger
{

    private BufferedWriter bw;
    private String myId;

    public Logger(String filename, String myId) throws Exception
    {
        if(filename == null)
        {
            filename = "./logs/" + myId + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_hh_mm")).toString().replaceAll(" ","").replaceAll(":", "_") +".log";
        }

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

        this.bw = new BufferedWriter(new FileWriter(filename, true)); 

        this.myId = myId;

        this.log("Logger File Created.\n");
    }

    public void log(String message) throws Exception
    {
        this.bw.write(LocalDateTime.now().toString() + " --> " + this.myId + ": " + message + "\n");
        
        // Debug
        // System.out.println(LocalDateTime.now().toString() + ": " + message);
        
        this.bw.flush();
    }
    
    public static void emptyFile(String filename) throws Exception {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        writer.write("");
        writer.close();
    }
}