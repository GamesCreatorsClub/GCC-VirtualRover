package org.ah.gcc.display.client;

import org.ah.gcc.display.GCCRoverDisplay;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;

public class GCCRoverHtmlLauncher extends GwtApplication {

    private GCCRoverDisplay gccRoverDisplay;
    private GwtApplicationConfiguration cfg;

    @Override
    public GwtApplicationConfiguration getConfig() {
        int height = com.google.gwt.user.client.Window.getClientHeight();
        int width = com.google.gwt.user.client.Window.getClientWidth();

        gccRoverDisplay.resize(width, height);

        com.google.gwt.user.client.Window.enableScrolling(false);
        com.google.gwt.user.client.Window.setMargin("0");

        cfg = new GwtApplicationConfiguration(width, height);
        return cfg;
    }

    @Override
    public ApplicationListener getApplicationListener() {
        // littlePlanets = new LittlePlanets();
        return gccRoverDisplay;
    }

    @Override
    public void onModuleLoad () {
        super.onModuleLoad();
        com.google.gwt.user.client.Window.addResizeHandler(new ResizeHandler() {
              @Override
            public void onResize(ResizeEvent event) {
                  int width = event.getWidth();
                  int height = event.getHeight();
                  // Gdx.graphics.setDisplayMode(width, height, false);
                  // Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                  Gdx.gl.glViewport(0, 0, width, height);
                  gccRoverDisplay.resize(width, height);

                  com.google.gwt.user.client.Window.scrollTo((cfg.width - width)/2,(cfg.height - height)/2);

                  //Gdx.graphics.setDisplayMode(ev.getWidth(), ev.getHeight(), false);
              }
            });
    }
//    @Override
//    public GwtApplicationConfiguration getConfig() {
//        return new GwtApplicationConfiguration(480, 320);
//    }

    @Override
    public ApplicationListener createApplicationListener() {
        gccRoverDisplay = new GCCRoverDisplay();
        return gccRoverDisplay;
    }
}