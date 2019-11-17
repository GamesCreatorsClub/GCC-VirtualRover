package org.ah.gcc.virtualrover.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;

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
import org.ah.themvsus.engine.client.ServerCommunication.ServerConnectionCallback;
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

    public PiNoonScreen(MainGame game,
            PlatformSpecific platformSpecific,
            AssetManager assetManager,
            SoundManager soundManager,
            ModelFactory modelFactory,
            ServerCommunicationAdapter serverCommunicationAdapter,
            Console console) {
        super(game, platformSpecific, assetManager, soundManager, modelFactory, serverCommunicationAdapter, console);
        this.game = game;
        this.assetManager = assetManager;
        this.soundManager = soundManager;
        this.modelFactory = modelFactory;
        this.console = console;

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

//        stateMachine = new StateMachine<PiNoonScreen, GameState>();

        if (platformSpecific.isSimulation()) {
//            stateMachine.toState(GameState.SIMULATION, this);
            serverCommunicationAdapter.connectToServer(platformSpecific.getPreferredServerAddress(),
                    platformSpecific.getPreferredServerPort(),
                    new ServerConnectionCallback() {

                        @Override public void successful() {
                            // TODO Do we need anything here?
                        }

                        @Override public void failed(String msg) {
                            // TODO log something to console
                        }
            });
            //
//        } else {
//            stateMachine.toState(GameState.SELECTION, this);
        }
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
//        if (!platformSpecific.isSimulation()) {
            if (!engine.isPaused()) {
                if (engine.isFixedClientFrameNo()) {
                    engine.resetFixedClientFrameNo();
                    nextRun = now;
                } else if (nextRun == 0) {
                    nextRun = now;
                }
                engine.processCommands();
                while (nextRun <= now) {
                    if (!platformSpecific.isSimulation()) {
                        engine.processPlayerInputs();
                    }
                    processedGameState = engine.process();

                    nextRun = nextRun + ENGINE_LOOP_TIME_us;
                }
            }
//        } else {
//            engine.processCommands();
//            processedGameState = engine.process();
//        }

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
//        stateMachine.update(this);

//        Gdx.graphics.getGL20().glEnable(GL20.GL_BLEND);
//        Gdx.graphics.getGL20().glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        if (renderBackground) {
            background.render(camera, batch, environment);
        }

        batch.begin(camera);


        challenge.render(batch, environment, serverCommunicationAdapter.getVisibleObjects());
        if (serverCommunicationAdapter.hasPlayerOne() && serverCommunicationAdapter.hasPlayerTwo()) {
            moveRovers();
        } else {
            setMiddleMessage("Press space to begin", true);
        }

//        if (stateMachine.getCurrentState().shouldMoveRovers()) {
//            moveRovers();
// TODO this is now done in Game. What now?
//
//            if (stateMachine.isState(GameState.GAME)) {
//                for (PlayerModel player : players) {
//                    for (PlayerModel other : players) {
//                        if (player != other) {
//                            PiNoonAttachment playerPiNoonAttachment = player.getPiNoonAttachment();
//                            PiNoonAttachment otherPiNoonAttachment = other.getPiNoonAttachment();
//
//                            if (playerPiNoonAttachment.checkIfBalloonsPopped(otherPiNoonAttachment) == 0) {
//                                other.playerScore++;
//                                winner = other.name;
//                            }
//                        }
//                    }
//                }
//            }
//        }

        batch.end();

        spriteBatch.begin();
//        if (stateMachine.getCurrentState().shouldDisplayScore()) {
            drawScore();
//        }
//        if (stateMachine.getCurrentState().shouldDisplayPlayerSelection()) {
//            drawPlayerSelection();
//        }
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

//    private void drawPlayerSelection() {
//        // TODO sort out selection
//        if (serverCommunicationAdapter.hasPlayerOne() && serverCommunicationAdapter.hasPlayerTwo()) {
//            font.draw(spriteBatch, serverCommunicationAdapter.getPlayerOne().getAlias(), 64, Gdx.graphics.getHeight() / 2 + 64);
//            font.draw(spriteBatch, serverCommunicationAdapter.getPlayerTwo().getAlias(), 64 + Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 + 64);
//        }
//    }

    private void moveRovers() {
        PlayerModel player1 = serverCommunicationAdapter.getPlayerOneVisualObject();
        if (player1 != null) {
            player1.roverInput.moveY(Gdx.input.isKeyPressed(Input.Keys.W) ? 1f : Gdx.input.isKeyPressed(Input.Keys.S) ? -1f : 0f);
            player1.roverInput.moveX(Gdx.input.isKeyPressed(Input.Keys.A) ? 1f : Gdx.input.isKeyPressed(Input.Keys.D) ? -1f : 0f);
            player1.roverInput.rotateX(Gdx.input.isKeyPressed(Input.Keys.Q) ? 1f : Gdx.input.isKeyPressed(Input.Keys.E) ? -1f : 0f);
        }

        PlayerModel player2 = serverCommunicationAdapter.getPlayerTwoVisualObject();
        if (player2 != null) {
            player2.roverInput.moveY(Gdx.input.isKeyPressed(Input.Keys.I) ? 1f : Gdx.input.isKeyPressed(Input.Keys.K) ? -1f : 0f);
            player2.roverInput.moveX(Gdx.input.isKeyPressed(Input.Keys.J) ? 1f : Gdx.input.isKeyPressed(Input.Keys.L) ? -1f : 0f);
            player2.roverInput.rotateX(Gdx.input.isKeyPressed(Input.Keys.U) ? 1f : Gdx.input.isKeyPressed(Input.Keys.O) ? -1f : 0f);
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        // TODO add handling of current main camera
    }

    @Override
    public boolean keyDown(int keycode) {
        if (serverCommunicationAdapter.isLocal() && !serverCommunicationAdapter.hasPlayerOne() && !serverCommunicationAdapter.hasPlayerTwo()) {
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

//    private enum GameState implements State<PiNoonScreen> {
//
//        SIMULATION() {
//            @Override public boolean shouldMoveRovers() { return true; }
//        },
//
//        SELECTION() {
//            @Override public boolean shouldDisplayPlayerSelection() { return true; }
//
//            @Override public void enter(PiNoonScreen s) {
//                Game game = s.serverCommunicationAdapter.getEngine().getGame();
//
//                if (game.containsObject(2)) {
//                    game.removeGameObject(2);
//                }
//                if (game.containsObject(1)) {
//                    game.removeGameObject(1);
//                }
//
//                s.setBottomMessage("Select rovers and press space to begin", true);
//            }
//
//            @Override public void exit(PiNoonScreen s) {
//                Game game = s.serverCommunicationAdapter.getEngine().getGame();
//                GCCPlayer player1 = (GCCPlayer)game.spawnPlayer(1, "Blue");
//                player1.setRoverType(RoverType.GCC);
//                GCCPlayer player2 = (GCCPlayer)game.spawnPlayer(2, "Green");
//                player2.setRoverType(RoverType.CBIS);
//                s.serverCommunicationAdapter.setLocalPlayerIds(1, 2);
//                s.serverCommunicationAdapter.getEngine().process();
//
//                s.setBottomMessage("", false);
//            }
//        },
//
//        GAME() {
//            @Override public boolean shouldMoveRovers() { return true; }
//            @Override public boolean shouldDisplayScore() { return true; }
//
//            @Override public void enter(PiNoonScreen s) {
//            }
//
//            @Override public void update(PiNoonScreen s) {
//            }
//        },
//
//        END() {
//            @Override public boolean shouldMoveRovers() { return true; }
//            @Override public boolean shouldDisplayScore() { return true; }
//
//            @Override public void enter(PiNoonScreen s) {
//            }
//
//            @Override public void exit(PiNoonScreen s) {
//            }
//        };
//
//        int timer;
//
//        @Override public void enter(PiNoonScreen s) {}
//        @Override public void update(PiNoonScreen s) {
//            timer--;
//        }
//        @Override public void exit(PiNoonScreen s) {}
//
//        public boolean shouldMoveRovers() { return false; }
//
//        public boolean shouldDisplayScore() { return false; }
//
//        public boolean shouldDisplayPlayerSelection() { return false; }
//    }
}
