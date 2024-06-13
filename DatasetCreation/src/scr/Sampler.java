package scr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Sampler
{
    public long index = 0;

    private BufferedWriter bw;

    public Sampler(String filename) throws Exception
    {
        this.bw = new BufferedWriter(new FileWriter(filename, true));
    }

    public void writeIntoDataset(String filename, String sample, String sampleIndex) throws Exception
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
        
        if(sampleIndex == null)
        {
            sampleIndex = "sample " + index + ",";
            index++;
        }
        bw.write(sampleIndex + sample + "\n");
        
    }

    public void close() throws Exception
    {
        this.bw.close();
    }

}
