package Config;

import DTO.*;
import Log.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ClientConfig {

    private static Properties properties;

    private ClientConfig() {
    }

    public static void load(String configFilePath) throws IOException {
        InputStream configFile = ClientConfig.class.getClassLoader()
                .getResourceAsStream(configFilePath);
        properties = new Properties();
        properties.load(configFile);
    }

    public static DTOType getDTOType() {
        if (properties == null) {
            Log.GetLogger().error("Forgot to ClientConfig.load()!");
            return DTOType.DTO_TYPE_JAVA_OBJECT;
        }

        var dtoTypeString = properties.getProperty("DTO.format");
        if (dtoTypeString.equals("json_file")) return DTOType.DTO_TYPE_JSON_FILE;
        else if (dtoTypeString.equals("java_object")) {
            return DTOType.DTO_TYPE_JAVA_OBJECT;
        }

        Log.GetLogger().warn("Unknown DTO type! Using default: JAVA_OBJECT.");
        return DTOType.DTO_TYPE_JAVA_OBJECT;
    }

}
