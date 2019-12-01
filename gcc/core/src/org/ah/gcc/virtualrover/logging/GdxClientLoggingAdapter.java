package org.ah.gcc.virtualrover.logging;

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
        Gdx.app.debug("", message);
    }

    @Override
    public void debug(String tag, String message) {
        Gdx.app.debug(tag, message);
    }

    @Override
    public void info(String message) {
        Gdx.app.log("", message);
    }

    @Override
    public void info(String tag, String message) {
        Gdx.app.log(tag, message);
    }

    @Override
    public void warning(String message) {
        Gdx.app.error("", message);
    }

    @Override
    public void warning(String tag, String message) {
        Gdx.app.error(tag, message);
    }

    @Override
    public void error(String message, Throwable t) {
        if (t == null) {
            Gdx.app.error("", message);
        } else {
            Gdx.app.error("", message, t);
        }
    }

    @Override
    public void error(String tag, String message, Throwable t) {
        if (t == null) {
            Gdx.app.error(tag, message);
        } else {
            Gdx.app.error(tag, message, t);
        }
    }
}
