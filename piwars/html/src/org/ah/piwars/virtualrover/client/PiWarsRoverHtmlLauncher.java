package org.ah.piwars.virtualrover.client;

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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import org.ah.piwars.virtualrover.MainGame;

public class PiWarsRoverHtmlLauncher extends GwtApplication {

    private static final int PADDING = 0;
    private GwtApplicationConfiguration cfg;

    private MainGame piwarsRoverDisplay;

    @Override
    public GwtApplicationConfiguration getConfig() {
        int w = Window.getClientWidth() - PADDING;
        int h = Window.getClientHeight() - PADDING;
        cfg = new GwtApplicationConfiguration(w, h);
        Window.enableScrolling(false);
        Window.setMargin("0");
        Window.addResizeHandler(new ResizeListener());
        cfg.preferFlash = false;
        return cfg;
    }

    class ResizeListener implements ResizeHandler {
        @Override
        public void onResize(ResizeEvent event) {
            int width = event.getWidth() - PADDING;
            int height = event.getHeight() - PADDING;
            getRootPanel().setWidth("" + width + "px");
            getRootPanel().setHeight("" + height + "px");
            getApplicationListener().resize(width, height);
            Gdx.graphics.setWindowedMode(width, height);
        }
    }

    @Override
    public ApplicationListener getApplicationListener() {
        return piwarsRoverDisplay;
    }

//    @Override
//    public void onModuleLoad() {
//        super.onModuleLoad();
//        com.google.gwt.user.client.Window.addResizeHandler(new ResizeHandler() {
//            @Override
//            public void onResize(ResizeEvent event) {
//                int width = event.getWidth();
//                int height = event.getHeight();
//                // Gdx.graphics.setDisplayMode(width, height, false);
//                // Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
//                Gdx.gl.glViewport(0, 0, width, height);
//                piwarsRoverDisplay.resize(width, height);
//
//                com.google.gwt.user.client.Window.scrollTo((cfg.width - width) / 2, (cfg.height - height) / 2);
//
//                // Gdx.graphics.setDisplayMode(ev.getWidth(), ev.getHeight(), false);
//            }
//        });
//    }

    @Override
    public ApplicationListener createApplicationListener() {
        piwarsRoverDisplay = new MainGame(new PiWarsHtmlPlatformSpecific());
        return piwarsRoverDisplay;
    }

    long loadStart = TimeUtils.nanoTime();

    @Override
    public PreloaderCallback getPreloaderCallback() {
        final Panel preloaderPanel = new VerticalPanel();
        preloaderPanel.setStyleName("gdx-preloader");
//        final Image libGDXLogo = new Image(GWT.getModuleBaseURL() + "LibGDX-logo.png");
//        libGDXLogo.setStyleName("logo");
//        preloaderPanel.add(libGDXLogo);
        final Image gccLogo = new Image(GWT.getModuleBaseURL() + "PiWarsLogo-small.png");
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