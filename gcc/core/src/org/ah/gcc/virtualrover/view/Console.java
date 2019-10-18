package org.ah.gcc.virtualrover.view;

import java.util.ArrayList;
import java.util.List;

import org.ah.themvsus.engine.common.game.Player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class Console {
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;
    private List<ConsoleMessage> messages;

    private OrthographicCamera camera;

    private SpriteBatch batch;
    private boolean typing = false;
    private boolean password = false;

    private String typingtext = "";
    private String prompt = "";

    private long t = 0;
    private String playerName = "Player";
    private ChatColor teamColor = ChatColor.BLUE;

    private float lineHeight = 24f;
    private float padding = 3f;

    private List<ChatListener> listeners;
    private Player player;
    private int consoleWidth;

    public Console() {
        font = new BitmapFont(Gdx.files.internal("font/copper18.fnt"), Gdx.files.internal("font/copper18.png"), false);
        font.getData().markupEnabled = true;
        shapeRenderer = new ShapeRenderer();
        messages = new ArrayList<ConsoleMessage>();
        batch = new SpriteBatch();

        listeners = new ArrayList<ChatListener>();
        lineHeight = font.getLineHeight();
        consoleWidth = Gdx.graphics.getWidth();
    }

    public void dispose() {
        font.dispose();
        shapeRenderer.dispose();
        batch.dispose();
    }

    public void setCamera(OrthographicCamera camera) {
        this.camera = camera;
    }

    public void startTyping(String prompt, boolean password) {
        this.typing = true;
        this.prompt = prompt;
        this.password = password;
        if (typing) {
            typingtext = "";
        }
    }

    public void stopTyping() {
        typing = false;
    }

    public void render() {
        t++;

        Gdx.gl.glEnable(GL20.GL_BLEND);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin();
        shapeRenderer.set(ShapeType.Filled);

        if (typing) {
            shapeRenderer.setColor(0.6f, 0.6f, 0.6f, 0.3f);
            shapeRenderer.rect(0, 0, consoleWidth, 24);
        }

        shapeRenderer.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        float height = lineHeight;
        int y = (int) lineHeight * 2;
        for (int i = messages.size() - 1; i >= 0; i--) {

            ConsoleMessage message = messages.get(i);
            float age = (System.currentTimeMillis() - message.getTime()) / 1000;
            if (typing || age < 10) {
                float alpha = (10 - age) / 10 - 0.1f;
                font.setColor(1, 1, 1, alpha);

                height = drawText(batch, message, 0 + padding, y - padding);
            }

            y += height + padding;
        }
        if (typing) {
            font.setColor(1, 1,1, 1f);
            char end_char = ' ';
            if ((t / 20) % 2 == 0) {
                end_char = '|';
            }
            if (password) {
                int l = typingtext.length();
                String p = "***************************************************************************";
                if (l > p.length()) {
                    l = p.length();
                }
                drawText(batch, prompt + p.substring(0, l) + end_char, 0 + padding, lineHeight);
            } else {
                drawText(batch, prompt + typingtext + end_char, 0 + padding, lineHeight);
            }
        }

        batch.end();
    }

    public void setConsoleWidth(int consoleWidth) {
        this.consoleWidth = consoleWidth;
        for (ConsoleMessage c : messages) {
            c.setConsoleWidth(consoleWidth);
        }
    }

    private float drawText(Batch batch, String s, float x, float y) {
        GlyphLayout layout = new GlyphLayout(font, s, Color.WHITE, consoleWidth, -1, true);
        font.draw(batch, layout, x, y);

        return layout.height;
    }

    private float drawText(Batch batch, ConsoleMessage m, float x, float y) {
        GlyphLayout layout = m.getLayout();
        font.draw(batch, layout, x, y + m.getHeight());

        return layout.height;
    }

    private void addMessage(String message) {
        messages.add(new ConsoleMessage(message, font, consoleWidth));
    }

    public void chat(String name, String message, ChatColor team) {
        addMessage(team + name + ChatColor.GRAY + ": " + ChatColor.WHITE + message);
        for (ChatListener listener : listeners) {
            listener.onChat(name, message);
        }
    }
    public void info(String message) {
        addMessage(ChatColor.GRAY +"[" + "INFO" + "] "+ ChatColor.WHITE + message);
        for (ChatListener listener : listeners) {
            listener.onText(message);
        }
    }

    public void debug(String message) {
        addMessage(ChatColor.BLUE +"[" + "DEBUG" + "] "+ ChatColor.WHITE + message);
        for (ChatListener listener : listeners) {
            listener.onText(message);
        }
    }

    public void error(String message) {
        addMessage(ChatColor.RED +"[" + "ERROR" + "] " + ChatColor.WHITE + message);
        for (ChatListener listener : listeners) {
            listener.onText(message);
        }
    }
    public void raw(String message) {
        addMessage(message);
        for (ChatListener listener : listeners) {
            listener.onText(message);
        }
    }

    public void addListener(ChatListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ChatListener listener) {
        listeners.remove(listener);
    }

    public void keyTyped(char character) {
        if (typing) {
            typingtext = typingtext + character;
        }
    }

    public void setPlayer(Player p) {
        this.player = p;
//        if (p.getTeam() == Team.BLUE) {
//            this.teamColor = ChatColor.BLUE;
//        } else if (p.getTeam() == Team.RED) {
//            this.teamColor = ChatColor.RED;
//        }
        this.playerName = p.getAlias();;
    }

    public void send() {
        if (typingtext.startsWith("/")) {
            for (ChatListener listener : listeners) {

                String[] split = typingtext.substring(1).split(" ");
                String cmdName = split[0];
                String[] args = new String[split.length - 1];
                for (int i = 1; i < split.length; i++) {
                    args[i - 1] = split[i];
                }
                listener.onCommand(player, cmdName, args);
            }
        } else {
            chat(playerName, typingtext, teamColor);
        }
        typingtext = "";
        typing = false;
    }

    public String getTypingtext() {
        return typingtext;
    }

    public void setTypingtext(String typingtext) {
        this.typingtext = typingtext;
    }

    public boolean isTyping() {
        return typing;
    }

    public void setTyping(boolean typing) {
        this.typing = typing;
    }
}
