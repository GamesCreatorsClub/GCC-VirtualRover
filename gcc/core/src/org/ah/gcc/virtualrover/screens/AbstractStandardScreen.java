package org.ah.gcc.virtualrover.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;

import org.ah.gcc.virtualrover.MainGame;
import org.ah.gcc.virtualrover.ModelFactory;
import org.ah.gcc.virtualrover.PlatformSpecific;
import org.ah.gcc.virtualrover.ServerCommunicationAdapter;
import org.ah.gcc.virtualrover.backgrounds.Background;
import org.ah.gcc.virtualrover.challenges.Challenge;
import org.ah.gcc.virtualrover.game.GameMessageObject;
import org.ah.gcc.virtualrover.utils.SoundManager;
import org.ah.gcc.virtualrover.view.ChatColor;
import org.ah.gcc.virtualrover.view.ChatListener;
import org.ah.gcc.virtualrover.view.Console;
import org.ah.themvsus.engine.common.game.Player;

public abstract class AbstractStandardScreen extends ScreenAdapter implements ChatListener {

    protected MainGame game;
    protected PlatformSpecific platformSpecific;
    protected ServerCommunicationAdapter serverCommunicationAdapter;

    protected AssetManager assetManager;
    protected SoundManager soundManager;
    protected ModelFactory modelFactory;

    protected ModelBatch batch;
    protected Environment environment;

    protected OrthographicCamera hudCamera;
    protected SpriteBatch spriteBatch;
    protected BitmapFont font;
    protected Texture gccLogo;

    protected Console console;

    protected Challenge challenge;
    protected Background background;

    private String bottomMessage;
    private boolean bottomMessageBlink;

    private String middleMessage;
    private boolean middleMessageBlink;

    private GlyphLayout glyphLayout = new GlyphLayout();
    private int a = 0;

    protected AbstractStandardScreen(MainGame game,
            PlatformSpecific platformSpecific,
            AssetManager assetManager,
            SoundManager soundManager,
            ModelFactory modelFactory,
            ServerCommunicationAdapter serverCommunicationAdapter,
            Console console) {

        this.game = game;
        this.platformSpecific = platformSpecific;
        this.assetManager = assetManager;
        this.soundManager = soundManager;
        this.modelFactory = modelFactory;
        this.serverCommunicationAdapter = serverCommunicationAdapter;
        this.console = console;

        batch = new ModelBatch();
        spriteBatch = new SpriteBatch();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f));
        DirectionalLight light = new DirectionalLight();
        light.set(1f, 1f, 1f, new Vector3(-0.5f, -1f, 0.5f));
        environment.add(light);

        hudCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        hudCamera.setToOrtho(true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        spriteBatch.dispose();
        font.dispose();
        if (gccLogo == null) { gccLogo.dispose(); }
        if (challenge != null) { challenge.dispose(); }
        if (background != null) { background.dispose(); }
    }

    protected Background getBackground() {
        return background;
    }

    protected void setBackground(Background background) {
        this.background = background;
    }

    protected Challenge getChallenge() {
        return challenge;
    }

    protected void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    protected void setBottomMessage(String message, boolean blink) {
        this.bottomMessage = message;
        this.bottomMessageBlink = blink;
    }

    protected void setMiddleMessage(String message, boolean blink) {
        this.middleMessage = message;
        this.middleMessageBlink = blink;
    }

    @Override
    public void show() {
        if (font == null) {
            font = assetManager.get("font/basic.fnt");
        }
        if (gccLogo == null) {
            gccLogo = assetManager.get("GCC_full.png");
        }

        if (console != null) {
            console.setCamera(hudCamera);
            console.addListener(this);
        }
    }

    @Override
    public void hide() {
        if (console != null) {
            console.removeListener(this);
        }
    }

    @Override
    public void resize(int width, int height) {
        if (console != null) {
            console.setConsoleWidth(width);
        }
        if (hudCamera != null) {
            hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            hudCamera.update();
        }
    }

    protected void drawStandardMessages() {
        a++;
        spriteBatch.begin();
        if (!platformSpecific.isSimulation()) {
            spriteBatch.draw(gccLogo, 0, Gdx.graphics.getHeight() - gccLogo.getHeight());
        }
        if (bottomMessage != null && (!bottomMessageBlink || Math.floor(a / 20.0) % 2 == 0)) {
            font.draw(spriteBatch, bottomMessage, 64, 128);
            // font.draw(spriteBatch, "Press space to start", margin, margin * 2);
        }

        GameMessageObject gameMessageObject = serverCommunicationAdapter.getGameMessageObject();

        if (gameMessageObject != null) {
            String message = gameMessageObject.getMessage();
            font.draw(spriteBatch, message, (Gdx.graphics.getWidth() - textWidth(font, message)) / 2, (Gdx.graphics.getHeight() - font.getLineHeight()) / 2);
        } else if (middleMessage != null && (!middleMessageBlink || Math.floor(a / 20.0) % 2 == 0)) {
            String message = middleMessage;
            font.draw(spriteBatch, message, (Gdx.graphics.getWidth() - textWidth(font, message)) / 2, (Gdx.graphics.getHeight() - font.getLineHeight()) / 2);
        }

        spriteBatch.end();
    }

    @Override
    public void onCommand(Player from, String cmdName, String[] args) {
        if ("hello".equals(cmdName)) {
            console.chat("Bot", "Hello", ChatColor.PURPLE);
        } else if ("help".equals(cmdName)) {
            console.raw(ChatColor.PURPLE + "/hello" + ChatColor.YELLOW + " | " + ChatColor.GREEN + "says hello");
            console.raw(ChatColor.PURPLE + "/time" + ChatColor.GREEN + "gives current time in millis");
            console.raw(ChatColor.PURPLE + "/help" + ChatColor.YELLOW + " | " + ChatColor.GREEN + "Shows this");
            console.raw(ChatColor.PURPLE + "/cpu" + ChatColor.YELLOW + " | " + ChatColor.GREEN + "Shows this");
        } else if ("colors".equals(cmdName)) {
            console.raw(ChatColor.RED + "o" + ChatColor.ORANGE + "o" + ChatColor.YELLOW + "o" + ChatColor.GREEN + "o" + ChatColor.BLUE + "o" + ChatColor.INDIGO
                    + "o" + ChatColor.PURPLE + "o" + ChatColor.GRAY + "o" + ChatColor.BLACK + "o");
        } else if ("time".equals(cmdName)) {
            console.info(ChatColor.INDIGO + "millis: " + ChatColor.GREEN + System.currentTimeMillis());
        } else {
            console.error("Unknow command, type /help for list");
        }
    }

    @Override
    public void onChat(String playerName, String text) {

    }

    @Override
    public void onText(String text) {

    }

    protected float textWidth(BitmapFont font, String text) {
        glyphLayout.setText(font, text);
        return glyphLayout.width;
    }
}
