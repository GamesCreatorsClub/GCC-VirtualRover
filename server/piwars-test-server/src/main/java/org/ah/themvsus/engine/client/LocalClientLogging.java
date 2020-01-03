package org.ah.themvsus.engine.client;

import org.ah.themvsus.engine.client.logging.AbstractClientLogging;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LocalClientLogging extends AbstractClientLogging {
    private static Logger logger = Logger.getLogger("headless");

    @Override
    public boolean isDebug() {
        return logger.isLoggable(Level.FINER);
    }

    @Override
    public void debug(String message) {
        logger.finer(message);
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void warning(String message) {
        logger.warning(message);
    }

    @Override
    public void error(String message, Throwable t) {
        if (t == null) {
            logger.severe(message);
        } else {
            logger.log(Level.SEVERE, message, t);
        }
    }
}
