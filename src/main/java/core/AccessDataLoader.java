package core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AccessDataLoader {
    private static final String filePath = "src/main/resources/api_access_data.properties";

    public static Properties getApiData(){
        FileInputStream fileInputStream;
        Properties properties = new Properties();
        try {
            fileInputStream = new FileInputStream(filePath);
            properties.load(fileInputStream);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return properties;
    }

}
