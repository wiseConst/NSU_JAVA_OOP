package Connection;


import Config.ServerConfig;
import DTO.DTOType;
import Log.Log;


public class ConnectionFactory {

    public static Connection create() {
        if (ServerConfig.getDTOType() == DTOType.DTO_TYPE_JAVA_OBJECT) return new JavaObjectConnection();
        else if (ServerConfig.getDTOType() == DTOType.DTO_TYPE_JSON_FILE) return new JsonConnection();

        Log.logWarn("Unknown connection type returning null!");
        return null;
    }

}
