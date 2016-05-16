package es.ehubio.io;

import java.util.logging.Handler;
import java.util.logging.Logger;

public class LogUtils {
	public static void disable() {
		//Logger globalLogger = Logger.getLogger("global");
		Logger globalLogger = Logger.getLogger("");
		Handler[] handlers = globalLogger.getHandlers();
		for(Handler handler : handlers) {
		    globalLogger.removeHandler(handler);
		}
	}
}
