package Config;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;

import DTO.*;

import Log.*;

public class ServerConfig {

    private static Properties properties = null;

    private ServerConfig() {
    }

    public static void load(String configFilePath) throws IOException {
        InputStream configFile = ServerConfig.class.getClassLoader()
                .getResourceAsStream(configFilePath);
        properties = new Properties();
        properties.load(configFile);
    }

    public static int getPort()
            throws IOException {
        if (properties == null) {
            Log.logError("Forgot to ServerConfig.load()!");
            return 0;
        }

        return Integer.parseInt(properties.getProperty("port"));
    }

    public static boolean getLogEvents()
            throws IOException {
        if (properties == null) {
            Log.logError("Forgot to ServerConfig.load()!");
            return false;
        }

        return Boolean.parseBoolean(properties.getProperty("bLogEvents"));
    }

    public static Integer getTimeout()
            throws IOException {
        if (properties == null) {
            Log.logError("Forgot to ServerConfig.load()!");
            return 0;
        }

        return Integer.parseInt(properties.getProperty("timeout"));
    }

    public static DTOType getDTOType() {
        if (properties == null) {
            Log.logError("Forgot to ServerConfig.load()!");
            return DTOType.DTO_TYPE_JAVA_OBJECT;
        }

        var dtoTypeString = properties.getProperty("DTO.format");
        if (dtoTypeString.equals("json_file")) return DTOType.DTO_TYPE_JSON_FILE;
        else if (dtoTypeString.equals("java_object")) {
            return DTOType.DTO_TYPE_JAVA_OBJECT;
        }

        Log.logWarn("Unknown DTO type! Using default: JAVA_OBJECT.");
        return DTOType.DTO_TYPE_JAVA_OBJECT;
    }
}
