package Connection;


import Config.ClientConfig;
import DTO.DTOType;
import Log.Log;


public class ConnectionFactory {

    public static Connection create() {
        if (ClientConfig.getDTOType() == DTOType.DTO_TYPE_JAVA_OBJECT) return new JavaObjectConnection();
        else if (ClientConfig.getDTOType() == DTOType.DTO_TYPE_JSON_FILE) return new JsonConnection();

        Log.GetLogger().warn("Unknown connection type returning null!");
        return null;
    }

}
