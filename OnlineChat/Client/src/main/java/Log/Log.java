package Log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log {

    private static final Logger m_Logger = LogManager.getLogger(Log.class);

    private Log() {
    }

    public static Logger GetLogger() {
        return m_Logger;
    }

}
