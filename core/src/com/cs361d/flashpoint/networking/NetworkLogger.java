package com.cs361d.flashpoint.networking;

import com.cs361d.flashpoint.screen.FlashPointServerHandler;
import com.cs361d.flashpoint.screen.ServerScreen;

import java.io.IOException;
import java.io.OutputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

import static com.cs361d.flashpoint.screen.ServerScreen.logMessage;

/*
 * Put these calls where you want to log. They will be called depending on corresponding logging level
 * LOGGER.severe("...");
 * LOGGER.warning("...");
 * LOGGER.info("...");
 * LOGGER.finest("...");
 */
public class NetworkLogger {
    //    private static final Logger LOGGER =
    // Logger.getLogger(NetworkLogger.class.getName());
    //    private static final Logger rootLogger = Logger.getLogger("");
    //    private static final Logger globalLogger =
    //            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME); // Child of rootLogger
    private static final Logger LOGGER =
            Logger.getLogger(NetworkLogger.class.getPackage().getName()); // Package logger

    //    private static ConsoleHandler cmdHandler;
    //    private static FileHandler fileHandler;
    //    private static SimpleFormatter formatterTxt;

    public NetworkLogger() {
        try {
            setup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void setup() throws IOException {

        // Suppress parent logger output to console
        //        Handler[] handlers = rootLogger.getHandlers();
        //        if (handlers[0] instanceof ConsoleHandler) {
        //            rootLogger.removeHandler(handlers[0]);
        //        }
        //        Logger globalLogger = Logger.getLogger("global");
        //        Handler[] handlers = globalLogger.getHandlers();
        //        for (Handler handler : handlers) {
        //            globalLogger.removeHandler(handler);
        //        }
        LogManager.getLogManager().reset();
        LOGGER.setUseParentHandlers(false);

        LOGGER.setLevel(Level.INFO);
        System.out.println("Logger level set to : INFO");

        // Also log to a dated log file
        Format formatter = new SimpleDateFormat("YYYY-MM-dd_hh-mm-ss");

        FileHandler fileHandler =
                new FileHandler("logs/LOG_" + formatter.format(new Date()) + ".txt");

        // ConsoleHandler outputs to stderr by default and cannot be changed...
        ConsoleHandler cmdHandler =
                new ConsoleHandler() {
                    @Override
                    protected synchronized void setOutputStream(OutputStream out)
                            throws SecurityException {
                        super.setOutputStream(System.out);
                    }
                };

        Handler fpsHandler = new FlashPointServerHandler();

        // Formats outputs
        SimpleFormatter formatterTxt = new SimpleFormatter();

        fileHandler.setFormatter(formatterTxt);
        LOGGER.addHandler(fileHandler);

        cmdHandler.setFormatter(formatterTxt);
        LOGGER.addHandler(cmdHandler);

        fpsHandler.setFormatter(formatterTxt);
        LOGGER.addHandler(fpsHandler);
    }

}
