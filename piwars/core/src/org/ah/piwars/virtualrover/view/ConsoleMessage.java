package org.ah.piwars.virtualrover.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class ConsoleMessage {
    private String message;
    private long createTime;

    private GlyphLayout layout;
    private int consoleWidth = 0;
    private BitmapFont font;

    public ConsoleMessage(String message, BitmapFont font, int consoleWidth) {
        this.message = message;
        this.font = font;
        setTime(System.currentTimeMillis());
        this.consoleWidth = consoleWidth;
        layout = new GlyphLayout(font, message, Color.WHITE, consoleWidth, -1, true);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return createTime;
    }

    public void setTime(long time) {
        this.createTime = time;
    }

    public GlyphLayout getLayout() {
        return layout;
    }

    public int getConsoleWidth() {
        return consoleWidth;
    }

    public void setConsoleWidth(int consoleWidth) {
        layout = new GlyphLayout(font, message, Color.WHITE, consoleWidth, -1, true);
        this.consoleWidth = consoleWidth;
    }

    public int getHeight() {
        return (int) layout.height;
    }




}
