package scr;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

public class Utility
{
    public static final String PROPERTY_FILENAME = "./config.txt";

    public static HashMap<String, String> readProperties(String[] propertiesNames)
    {
        return readProperties(PROPERTY_FILENAME, propertiesNames);
    }

    public static HashMap<String, String> readProperties(String filename, String[] propertiesNames)
    {
        HashMap<String, String> retProp = new HashMap<String, String>();
        Properties prop = new Properties();
        InputStream input = null;
        try {

            String currentDirectory = System.getProperty("user.dir");
            System.out.println("Current working directory: " + currentDirectory);

            // Specify the path to your properties file
            input = new FileInputStream(filename);

            // Load the properties file
            prop.load(input);

            // Get properties
            for (String propName : propertiesNames) {
                retProp.put(propName, prop.getProperty(propName));
            }
            return retProp;

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    
}
