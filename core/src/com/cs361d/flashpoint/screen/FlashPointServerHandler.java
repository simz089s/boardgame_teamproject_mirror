package com.cs361d.flashpoint.screen;

import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static com.cs361d.flashpoint.screen.ServerScreen.logMessage;

public class FlashPointServerHandler extends Handler {

  public FlashPointServerHandler() {
    super();
  }

  @Override
  public void publish(LogRecord record) {
    try {
      if (this.isLoggable(record)) {
        String[] msgs = null;
        try {
          msgs = getFormatter().format(record).split("\n");
        } catch (Exception e) {
          getErrorManager().error("Exception occurred when formatting the log record",
                  e, ErrorManager.FORMAT_FAILURE);
        }
        for (String msg : msgs) {
          logMessage(msg);
        }
      }
    } catch (Exception e) {
      getErrorManager().error("Exception occurred when logging the record", e,
              ErrorManager.GENERIC_FAILURE);
      e.printStackTrace();
    }
  }

  @Override
  public void flush() {

  }

  @Override
  public void close() throws SecurityException {

  }
}
