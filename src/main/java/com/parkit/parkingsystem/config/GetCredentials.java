package com.parkit.parkingsystem.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

public class GetCredentials {
    InputStream inputStream;
    String[] values = new String[2];

    public String[] getPropValues() throws IOException {

        try {
            Properties prop = new Properties();
            String propFileName = "db.properties";

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            // get the property value and print it out
            values[0] = prop.getProperty("username");
            values[1] = prop.getProperty("password");
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            if (inputStream != null){
                inputStream.close();
            }
        }
        return Arrays.copyOf(values,values.length);
    }
}
