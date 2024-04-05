package Log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log {

    private static final Logger logger = LogManager.getLogger(Log.class);
    private static boolean bIsLogging = true;

    private Log() {
    }

    public static void setIsLogging(boolean bIsLogging) {
        Log.bIsLogging = bIsLogging;
    }

    public static void logInfo(String message) {
        if (!bIsLogging) return;
        logger.info(message);
    }

    public static void logWarn(String message) {
        if (!bIsLogging) return;
        logger.warn(message);
    }

    public static void logError(String message) {
        if (!bIsLogging) return;
        logger.error(message);
    }
}
