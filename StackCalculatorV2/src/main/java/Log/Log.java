package Log;

import org.apache.log4j.Logger;

public class Log {

    private static final Logger m_Logger = Logger.getLogger(Log.class);


    private Log() {
    }


    public static Logger GetLogger() {
        return m_Logger;
    }

}
