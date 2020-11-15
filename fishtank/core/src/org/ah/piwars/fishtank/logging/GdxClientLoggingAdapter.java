package org.ah.piwars.fishtank.logging;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

import org.ah.themvsus.engine.client.logging.AbstractClientLogging;

public class GdxClientLoggingAdapter extends AbstractClientLogging {

    private static GdxClientLoggingAdapter INSTANCE = new GdxClientLoggingAdapter();

    public static GdxClientLoggingAdapter getInstance() { return INSTANCE; }


    @Override
    public boolean isDebug() {
        return Gdx.app.getLogLevel() >= Application.LOG_DEBUG;
    }

    @Override
    public void debug(String message) {
        Gdx.app.debug("", time() + message);
    }

    @Override
    public void debug(String tag, String message) {
        Gdx.app.debug(tag, time() + message);
    }

    @Override
    public void info(String message) {
        Gdx.app.log("", time() + message);
    }

    @Override
    public void info(String tag, String message) {
        Gdx.app.log(tag, time() + message);
    }

    @Override
    public void warning(String message) {
        Gdx.app.error("", time() + message);
    }

    @Override
    public void warning(String tag, String message) {
        Gdx.app.error(tag, time() + message);
    }

    @Override
    public void error(String message, Throwable t) {
        if (t == null) {
            Gdx.app.error("", time() + message);
        } else {
            Gdx.app.error("", time() + message, t);
        }
    }

    @Override
    public void error(String tag, String message, Throwable t) {
        if (t == null) {
            Gdx.app.error(tag, time() + message);
        } else {
            Gdx.app.error(tag, time() + message, t);
        }
    }

    protected String time() {
        String timestamp = Long.toString(System.currentTimeMillis());
        timestamp = timestamp.substring(8);
        return timestamp + " ";
    }
}
