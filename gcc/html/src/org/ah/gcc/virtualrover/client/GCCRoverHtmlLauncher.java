package org.ah.gcc.virtualrover.client;

import org.ah.gcc.virtualrover.GCCRoverDisplay;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.badlogic.gdx.backends.gwt.preloader.Preloader.PreloaderCallback;
import com.badlogic.gdx.backends.gwt.preloader.Preloader.PreloaderState;
import com.badlogic.gdx.utils.TimeUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class GCCRoverHtmlLauncher extends GwtApplication {

    private GCCRoverDisplay gccRoverDisplay;
    private GwtApplicationConfiguration config;

    @Override
    public GwtApplicationConfiguration getConfig() {
        int height = com.google.gwt.user.client.Window.getClientHeight();
        int width = com.google.gwt.user.client.Window.getClientWidth();

        gccRoverDisplay.resize(width, height);

        com.google.gwt.user.client.Window.enableScrolling(false);
        com.google.gwt.user.client.Window.setMargin("0");

        config = new GwtApplicationConfiguration(width, height);
        return config;
    }

    @Override
    public ApplicationListener getApplicationListener() {
        // littlePlanets = new LittlePlanets();
        return gccRoverDisplay;
    }

    @Override
    public void onModuleLoad() {
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

                com.google.gwt.user.client.Window.scrollTo((config.width - width) / 2, (config.height - height) / 2);

                // Gdx.graphics.setDisplayMode(ev.getWidth(), ev.getHeight(), false);
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

    long loadStart = TimeUtils.nanoTime();

    @Override
    public PreloaderCallback getPreloaderCallback() {
        final Panel preloaderPanel = new VerticalPanel();
        preloaderPanel.setStyleName("gdx-preloader");
//        final Image libGDXLogo = new Image(GWT.getModuleBaseURL() + "LibGDX-logo.png");
//        libGDXLogo.setStyleName("logo");
//        preloaderPanel.add(libGDXLogo);
        final Image gccLogo = new Image(GWT.getModuleBaseURL() + "GCC_full.png");
        gccLogo.setStyleName("logo");
        preloaderPanel.add(gccLogo);
        final Panel meterPanel = new SimplePanel();
        meterPanel.setStyleName("gdx-meter");
        meterPanel.addStyleName("red");
        final InlineHTML meter = new InlineHTML();
        final Style meterStyle = meter.getElement().getStyle();
        meterStyle.setWidth(0, Unit.PCT);
        meterPanel.add(meter);
        preloaderPanel.add(meterPanel);
        getRootPanel().add(preloaderPanel);
        return new PreloaderCallback() {

                @Override
                public void error (String file) {
                        System.out.println("error: " + file);
                }

                @Override
                public void update (PreloaderState state) {
                        meterStyle.setWidth(100f * state.getProgress(), Unit.PCT);
                }

        };
   }
}