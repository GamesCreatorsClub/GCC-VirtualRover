package org.ah.gcc.virtualrover.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.utils.IntSet;

import org.ah.gcc.virtualrover.MainGame;
import org.ah.gcc.virtualrover.ModelFactory;
import org.ah.gcc.virtualrover.PlatformSpecific;
import org.ah.gcc.virtualrover.ServerCommunicationAdapter;
import org.ah.gcc.virtualrover.backgrounds.PerlinNoiseBackground;
import org.ah.gcc.virtualrover.camera.CameraControllersManager;
import org.ah.gcc.virtualrover.camera.CinematicCameraController;
import org.ah.gcc.virtualrover.challenges.PiNoonArena;
import org.ah.gcc.virtualrover.game.GCCGame;
import org.ah.gcc.virtualrover.game.GCCPlayer;
import org.ah.gcc.virtualrover.game.rovers.RoverType;
import org.ah.gcc.virtualrover.utils.SoundManager;
import org.ah.gcc.virtualrover.view.Console;
import org.ah.gcc.virtualrover.world.PlayerModel;
import org.ah.themvsus.engine.client.ClientEngine;
import org.ah.themvsus.engine.common.game.Game;

import static org.ah.gcc.virtualrover.MainGame.SCALE;
import static org.ah.gcc.virtualrover.game.GCCGame.ENGINE_LOOP_TIME_us;

public class PiNoonScreen extends AbstractStandardScreen implements InputProcessor {

    private PerspectiveCamera camera;
    private CameraControllersManager cameraControllersManager;
    private InputMultiplexer cameraInputMultiplexer;

    private RoverType player1RoverType = RoverType.GCC;
    private RoverType player2RoverType = RoverType.CBIS;

    private boolean renderBackground = false;
    private boolean drawFPS = false;
    private long nextRun;

    private org.ah.themvsus.engine.common.game.GameState processedGameState;
    private IntSet unknownObjectIds = new IntSet();

    public PiNoonScreen(MainGame game,
            PlatformSpecific platformSpecific,
            AssetManager assetManager,
            SoundManager soundManager,
            ModelFactory modelFactory,
            ServerCommunicationAdapter serverCommunicationAdapter,
            Console console) {
        super(game, platformSpecific, assetManager, soundManager, modelFactory, serverCommunicationAdapter, console);

        setBackground(new PerlinNoiseBackground());

        PiNoonArena challenge = new PiNoonArena(modelFactory);
        challenge.init();

        setChallenge(challenge);

        camera = new PerspectiveCamera(45, 800, 480);
        camera.position.set(300f * SCALE, 480f * SCALE, 300f * SCALE);
        camera.lookAt(0f, 0f, 0f);
        camera.near = 0.02f;
        camera.far = 1000f;

        cameraInputMultiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(cameraInputMultiplexer);
        Gdx.input.setCursorCatched(false);

        cameraControllersManager = new CameraControllersManager();
        cameraInputMultiplexer.addProcessor(this);
        cameraInputMultiplexer.addProcessor(cameraControllersManager);

        cameraControllersManager.addCameraController("Cinematic", new CinematicCameraController(camera, serverCommunicationAdapter));
        cameraControllersManager.addCameraController("Default", new CameraInputController(camera));
        // cameraControllersManager.addCameraController("Other", new CinematicCameraController2(camera, players));
    }

    @Override
    public void dispose() {
        // Do our dispose

        super.dispose();
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void hide() {
        super.hide();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.6f, 0.75f, 1f, 1f);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl20.glDepthFunc(GL20.GL_LEQUAL);
        Gdx.gl.glEnable(GL20.GL_POLYGON_OFFSET_FILL);
        Gdx.gl20.glPolygonOffset(1.0f, 1.0f);

        ClientEngine<GCCGame> engine = serverCommunicationAdapter.getEngine();
        long now = System.currentTimeMillis() * 1000;
        if (engine != null &&!engine.isPaused()) {
            if (engine.isFixedClientFrameNo()) {
                engine.resetFixedClientFrameNo();
                nextRun = now;
            } else if (nextRun == 0) {
                nextRun = now;
            }
            unknownObjectIds.clear();
            engine.processCommands(unknownObjectIds);
            if (unknownObjectIds.size > 0) {
                serverCommunicationAdapter.requestFullUpdate(unknownObjectIds);
            }
            while (nextRun <= now) {
                if (!platformSpecific.isSimulation()) {
                    engine.processPlayerInputs();
                } else {
                    engine.getPlayerInputs().pop(engine.getGame().getCurrentGameState().getFrameNo());
                }
                processedGameState = engine.process();

                nextRun = nextRun + ENGINE_LOOP_TIME_us;
            }
        }

        PlayerModel playerOne = serverCommunicationAdapter.getPlayerOneVisualObject();
        if (playerOne != null) {
            serverCommunicationAdapter.setPlayerOneInput(processedGameState.getFrameNo() + 1, playerOne.roverInput);
        }
        PlayerModel playerTwo = serverCommunicationAdapter.getPlayerTwoVisualObject();
        if (playerTwo != null) {
            serverCommunicationAdapter.setPlayerTwoInput(processedGameState.getFrameNo() + 1, playerTwo.roverInput);
        }

        cameraControllersManager.update();
        camera.update();

        if (renderBackground) {
            background.render(camera, batch, environment);
        }

        batch.begin(camera);


        challenge.render(batch, environment, serverCommunicationAdapter.getVisibleObjects());
        if (serverCommunicationAdapter.isLocal()) {
            if (serverCommunicationAdapter.hasPlayerOne() && serverCommunicationAdapter.hasPlayerTwo()) {
                moveRovers();
            } else if (serverCommunicationAdapter.isLocal()) {
                setMiddleMessage("Press space to begin", true);
            }
        } else {
            if (serverCommunicationAdapter.hasPlayerOne()) {
                moveRovers();
            }
        }

        batch.end();

        spriteBatch.begin();
        drawScore();

        if (drawFPS) {
            drawFPS();
        }
        spriteBatch.end();

        drawStandardMessages();

        if (console != null) {
            console.render();
        }
    }

    private void drawFPS() {
        String fps = String.format("%3s", Gdx.graphics.getFramesPerSecond());
        font.draw(spriteBatch, fps, Gdx.graphics.getWidth() - 40, Gdx.graphics.getHeight() - 40);
    }

    private void drawScore() {
        // TODO sort out score
        if (serverCommunicationAdapter.hasPlayerOne() && serverCommunicationAdapter.hasPlayerTwo()) {
            font.draw(spriteBatch, serverCommunicationAdapter.getPlayerOne().getScore() + " - " + serverCommunicationAdapter.getPlayerTwo().getScore(), Gdx.graphics.getWidth() - 120, Gdx.graphics.getHeight() - 40);
        }
    }

    private void moveRovers() {
        PlayerModel player1 = serverCommunicationAdapter.getPlayerOneVisualObject();
        if (player1 != null) {
            player1.roverInput.moveY(Gdx.input.isKeyPressed(Input.Keys.W) ? 1f : Gdx.input.isKeyPressed(Input.Keys.S) ? -1f : 0f);
            player1.roverInput.moveX(Gdx.input.isKeyPressed(Input.Keys.A) ? 1f : Gdx.input.isKeyPressed(Input.Keys.D) ? -1f : 0f);
            player1.roverInput.rotateX(Gdx.input.isKeyPressed(Input.Keys.Q) ? 1f : Gdx.input.isKeyPressed(Input.Keys.E) ? -1f : 0f);
            player1.roverInput.rightTrigger(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ? 1f : 0f);
            player1.roverInput.circle(Gdx.input.isKeyPressed(Input.Keys.Z));
            player1.roverInput.cross(Gdx.input.isKeyPressed(Input.Keys.X));
            player1.roverInput.square(Gdx.input.isKeyPressed(Input.Keys.C));
            player1.roverInput.triangle(Gdx.input.isKeyPressed(Input.Keys.V));
        }

        PlayerModel player2 = serverCommunicationAdapter.getPlayerTwoVisualObject();
        if (player2 != null) {
            player2.roverInput.moveY(Gdx.input.isKeyPressed(Input.Keys.I) ? 1f : Gdx.input.isKeyPressed(Input.Keys.K) ? -1f : 0f);
            player2.roverInput.moveX(Gdx.input.isKeyPressed(Input.Keys.J) ? 1f : Gdx.input.isKeyPressed(Input.Keys.L) ? -1f : 0f);
            player2.roverInput.rotateX(Gdx.input.isKeyPressed(Input.Keys.U) ? 1f : Gdx.input.isKeyPressed(Input.Keys.O) ? -1f : 0f);
            player2.roverInput.rightTrigger(Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT) ? 1f : 0f);
            player2.roverInput.circle(Gdx.input.isKeyPressed(Input.Keys.N));
            player2.roverInput.cross(Gdx.input.isKeyPressed(Input.Keys.M));
            player2.roverInput.square(Gdx.input.isKeyPressed(Input.Keys.COMMA));
            player2.roverInput.triangle(Gdx.input.isKeyPressed(Input.Keys.PERIOD));
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        // TODO add handling of current main camera
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.SPACE && serverCommunicationAdapter.isLocal() && !serverCommunicationAdapter.hasPlayerOne() && !serverCommunicationAdapter.hasPlayerTwo()) {
            Game game = serverCommunicationAdapter.getEngine().getGame();
            GCCPlayer player1 = (GCCPlayer)game.spawnPlayer(1, "Blue");
            player1.setRoverType(player1RoverType);
            GCCPlayer player2 = (GCCPlayer)game.spawnPlayer(2, "Green");
            player2.setRoverType(player2RoverType);
            serverCommunicationAdapter.setLocalPlayerIds(1, 2);
        }

        if (keycode == Input.Keys.TAB) {
            camera.fieldOfView = 4f;
        }

        if (keycode == Input.Keys.H && challenge instanceof PiNoonArena) {
            PiNoonArena piNoonArena = (PiNoonArena)challenge;
            if (piNoonArena.showRovers && !piNoonArena.showPlan) {
                piNoonArena.showRovers = true;
                piNoonArena.showPlan = true;
            } else if (piNoonArena.showRovers && piNoonArena.showPlan) {
                piNoonArena.showRovers = false;
                piNoonArena.showPlan = true;
            } else {
                piNoonArena.showRovers = true;
                piNoonArena.showPlan = false;
            }
        }
        if (keycode == Input.Keys.G && challenge instanceof PiNoonArena) {
            PiNoonArena piNoonArena = (PiNoonArena)challenge;
            piNoonArena.showShadows = !piNoonArena.showShadows;
        }
        if (keycode == Input.Keys.T && challenge instanceof PiNoonArena) {
            PiNoonArena piNoonArena = (PiNoonArena)challenge;
            piNoonArena.showRovers = !piNoonArena.showRovers;
        }
        if (keycode == Input.Keys.F) {
            drawFPS = !drawFPS;
        }
        return false;
    }

    @Override public boolean keyUp(int keycode) { return false; }

    @Override public boolean keyTyped(char character) { return false; }

    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }

    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }

    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }

    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }

    @Override public boolean scrolled(int amount) { return false; }
}
