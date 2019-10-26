package org.ah.gcc.virtualrover.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Quaternion;

import org.ah.gcc.virtualrover.MainGame;
import org.ah.gcc.virtualrover.ModelFactory;
import org.ah.gcc.virtualrover.ServerCommunicationAdapter;
import org.ah.gcc.virtualrover.VisibleObject;
import org.ah.gcc.virtualrover.backgrounds.PerlinNoiseBackground;
import org.ah.gcc.virtualrover.camera.CameraControllersManager;
import org.ah.gcc.virtualrover.camera.CinematicCameraController;
import org.ah.gcc.virtualrover.camera.CinematicCameraController2;
import org.ah.gcc.virtualrover.challenges.PiNoonArena;
import org.ah.gcc.virtualrover.game.GCCGame;
import org.ah.gcc.virtualrover.rovers.attachments.PiNoonAttachment;
import org.ah.gcc.virtualrover.statemachine.State;
import org.ah.gcc.virtualrover.statemachine.StateMachine;
import org.ah.gcc.virtualrover.utils.SoundManager;
import org.ah.gcc.virtualrover.view.Console;
import org.ah.gcc.virtualrover.world.PlayerModel;
import org.ah.themvsus.engine.client.ClientEngine;
import org.ah.themvsus.engine.common.Engine;

import java.util.ArrayList;
import java.util.List;

import static org.ah.gcc.virtualrover.MainGame.SCALE;

public class PiNoonScreen extends AbstractStandardScreen implements InputProcessor {

    private ServerCommunicationAdapter serverCommunicationAdapter;

    private PerspectiveCamera camera;
    private CameraControllersManager cameraControllersManager;
    private InputMultiplexer cameraInputMultiplexer;

    private StateMachine<PiNoonScreen, GameState> stateMachine;
    private String winner = null;
    private int countdown;

    private List<PlayerModel> players = new ArrayList<PlayerModel>();

    private boolean renderBackground = false;
    private long nextRun;

    private org.ah.themvsus.engine.common.game.GameState processedGameState;

    private Quaternion orientation = new Quaternion();

    public PiNoonScreen(MainGame game,
            AssetManager assetManager,
            SoundManager soundManager,
            ModelFactory modelFactory,
            ServerCommunicationAdapter serverCommunicationAdapter,
            Console console) {
        super(game, assetManager, soundManager, modelFactory, console);
        this.game = game;
        this.assetManager = assetManager;
        this.soundManager = soundManager;
        this.modelFactory = modelFactory;
        this.serverCommunicationAdapter = serverCommunicationAdapter;
        this.console = console;

        setBackground(new PerlinNoiseBackground());
        setChallenge(new PiNoonArena(modelFactory));

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

        cameraControllersManager.addCameraController("Cinematic", new CinematicCameraController(camera, players));
        cameraControllersManager.addCameraController("Other", new CinematicCameraController2(camera, players));
        cameraControllersManager.addCameraController("Default", new CameraInputController(camera));

        stateMachine = new StateMachine<PiNoonScreen, GameState>();
        stateMachine.toState(GameState.SELECTION, this);


        serverCommunicationAdapter.getEngine().getGame().spawnPlayer(1, "Blue");
        serverCommunicationAdapter.getEngine().getGame().spawnPlayer(2, "Green");
        serverCommunicationAdapter.getEngine().process();
        // players.add(new PlayerModel(RoverType.GCC, 1, "Blue", Color.BLUE));
        // players.add(new PlayerModel(RoverType.CBIS, 2, "Green", Color.GREEN));
        // TODO do something about it... Remove? Do something smarter? Stop using players list?
        for (VisibleObject visibleObject : serverCommunicationAdapter.getSprites().values()) {
            if (visibleObject instanceof PlayerModel) {
                players.add((PlayerModel)visibleObject);
            }
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
        long now = System.currentTimeMillis();
        if (!engine.isPaused()) {
            if (engine.isFixedClientFrameNo()) {
                engine.resetFixedClientFrameNo();
                nextRun = now;
            } else if (nextRun == 0) {
                nextRun = now;
            }
            engine.processCommands();
            while (nextRun <= now) {
                engine.processPlayerInputs();
                processedGameState = engine.process();

                nextRun = nextRun + Engine.ENGINE_LOOP_TIME;
            }

            if (players.size() > 0) {
                PlayerModel playerOne = players.get(0);
                serverCommunicationAdapter.setPlayerOneInput(processedGameState.getFrameNo() + 1,
                        playerOne.roverInputs.moveX(), playerOne.roverInputs.moveY(),
                        playerOne.roverInputs.rotateX(), playerOne.roverInputs.rotateY());
            }
            if (players.size() > 1) {
                PlayerModel playerTwo = players.get(1);
                serverCommunicationAdapter.setPlayerTwoInput(processedGameState.getFrameNo() + 1,
                        playerTwo.roverInputs.moveX(), playerTwo.roverInputs.moveY(),
                        playerTwo.roverInputs.rotateX(), playerTwo.roverInputs.rotateY());
            }
        }

        cameraControllersManager.update();
        camera.update();
        stateMachine.update(this);

        Gdx.graphics.getGL20().glEnable(GL20.GL_BLEND);
        Gdx.graphics.getGL20().glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        if (renderBackground) {
            background.render(camera, batch, environment);
        }

        batch.begin(camera);

        challenge.render(batch, environment);

        if (stateMachine.getCurrentState().shouldMoveRovers()) {
            if (players.size() > 1) {
                moveRovers();
            }
            if (stateMachine.isState(GameState.GAME)) {
                for (PlayerModel player : players) {
                    for (PlayerModel other : players) {
                        if (player != other) {
                            PiNoonAttachment playerPiNoonAttachment = player.getPiNoonAttachment();
                            PiNoonAttachment otherPiNoonAttachment = other.getPiNoonAttachment();

                            if (playerPiNoonAttachment.checkIfBalloonsPopped(otherPiNoonAttachment) == 0) {
                                other.playerScore++;
                                winner = other.name;
                            }
                        }
                    }
                }
            }
        }

        for (VisibleObject visibleObject : serverCommunicationAdapter.getSprites().values()) {
            visibleObject.render(batch, environment);
        }

        batch.end();

        spriteBatch.begin();
        if (stateMachine.getCurrentState().shouldDisplayScore()) {
            drawScore();
        }
        if (stateMachine.getCurrentState().shouldDisplayPlayerSelection()) {
            drawPlayerSelection();
        }
        spriteBatch.end();

        drawStandardMessages();

        if (console != null) {
            console.render();
        }
    }

    private void setupRovers() {
        for (PlayerModel player : players) {
            player.makeRobot(modelFactory);
        }

        // TODO reset sets balloons and we have to remove them immediately after. That's not good...
        resetRovers();
        for (PlayerModel player : players) {
            player.getPiNoonAttachment().removeBalloons();
        }
    }

    private void drawScore() {
        // TODO sort out score
        if (players.size() > 0) {
            font.draw(spriteBatch, players.get(0).playerScore + " - " + players.get(1).playerScore, Gdx.graphics.getWidth() - 120, Gdx.graphics.getHeight() - 40);
        }
    }

    private void drawPlayerSelection() {
        // TODO sort out selection
        if (players.size() > 1) {
            font.draw(spriteBatch, players.get(0).playerSelection.getName(), 64, Gdx.graphics.getHeight() / 2 + 64);
            font.draw(spriteBatch, players.get(1).playerSelection.getName(), 64 + Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 + 64);
        }
    }

    private void moveRovers() {
        // TODO move all rovers
        PlayerModel player1 = players.get(0);
        PlayerModel player2 = players.get(1);

        if (player1.rover != null && player2.rover != null) {
            player1.roverInputs.moveY(Gdx.input.isKeyPressed(Input.Keys.W) ? 1f : Gdx.input.isKeyPressed(Input.Keys.S) ? -1f : 0f);
            player1.roverInputs.moveX(Gdx.input.isKeyPressed(Input.Keys.A) ? -1f : Gdx.input.isKeyPressed(Input.Keys.D) ? 1f : 0f);
            player1.roverInputs.rotateX(Gdx.input.isKeyPressed(Input.Keys.Q) ? -1f : Gdx.input.isKeyPressed(Input.Keys.E) ? 1f : 0f);

            player2.roverInputs.moveY(Gdx.input.isKeyPressed(Input.Keys.I) ? 1f : Gdx.input.isKeyPressed(Input.Keys.K) ? -1f : 0f);
            player2.roverInputs.moveX(Gdx.input.isKeyPressed(Input.Keys.J) ? -1f : Gdx.input.isKeyPressed(Input.Keys.L) ? 1f : 0f);
            player2.roverInputs.rotateX(Gdx.input.isKeyPressed(Input.Keys.U) ? -1f : Gdx.input.isKeyPressed(Input.Keys.O) ? 1f : 0f);

//                List<Polygon> rover1Poligons = player1.rover.getPolygons();
//                List<Polygon> rover2Poligons = player2.rover.getPolygons();
//
//                boolean roversCollide = polygonsOverlap(rover1Poligons, rover2Poligons);
//
//                if (roversCollide) {
//                    if (rover1Moves) {
//                        player1.rover.getTransform().set(rover1Position);
//                        player1.rover.update();
//                    }
//                    if (rover2Moves) {
//                        player2.rover.getTransform().set(rover2Position);
//                        player2.rover.update();
//                    }
//                } else {
//                    if (rover1Moves && challenge.collides(rover1Poligons)) {
//                        player1.rover.getTransform().set(rover1Position);
//                        player1.rover.update();
//                    }
//                    if (rover2Moves && challenge.collides(rover2Poligons)) {
//                        player2.rover.getTransform().set(rover2Position);
//                        player2.rover.update();
//                    }
//                }
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        // TODO add handling of current main camera
    }

    @Override
    public boolean keyDown(int keycode) {
        if (stateMachine.isState(GameState.SELECTION)) {

            if (keycode == Input.Keys.D) {
                players.get(0).playerSelection = players.get(0).playerSelection.getNext();
            } else if (keycode == Input.Keys.A) {
                players.get(0).playerSelection = players.get(0).playerSelection.getPrevious();
            }

            if (keycode == Input.Keys.L) {
                players.get(1).playerSelection = players.get(1).playerSelection.getNext();
            } else if (keycode == Input.Keys.J) {
                players.get(1).playerSelection = players.get(1).playerSelection.getPrevious();
            }

            if (keycode == Input.Keys.SPACE) {
                stateMachine.toState(GameState.BREAK, this);
            }
        }

        if (keycode == Input.Keys.SPACE) {
            if (stateMachine.isState(GameState.END)) {
                stateMachine.toState(GameState.SELECTION, this);
            } else if (stateMachine.isState(GameState.SELECTION)) {
                stateMachine.toState(GameState.SELECTION, this);
            }
        }

        if (keycode == Input.Keys.TAB) {
            camera.fieldOfView = 4f;
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

    public void resetRovers() {
        if (players.size() > 0) {
            PlayerModel player = players.get(0);
            orientation.setEulerAnglesRad(0f, 0f, (float)(Math.PI + Math.PI / 4f));
            player.setGamePlayerPositionAndOrientation(700, 700, orientation);
        }
        if (players.size() > 0) {
            PlayerModel player = players.get(1);
            orientation.setEulerAnglesRad(0f, 0f, (float)(+ Math.PI / 4f));
            player.setGamePlayerPositionAndOrientation(-700, -700, orientation);
        }

        for (PlayerModel player : players) {
            player.getPiNoonAttachment().resetBalloons();
        }
    }

    private enum GameState implements State<PiNoonScreen> {

        SELECTION() {
            @Override public boolean shouldDisplayPlayerSelection() { return true; }

            @Override public void enter(PiNoonScreen s) {
                for (PlayerModel player : s.players) {
                    player.playerScore = 0;
                }
                s.winner = null;
                s.setBottomMessage("Select rovers and press space to begin", true);
            }

            @Override public void exit(PiNoonScreen s) {
                s.setupRovers();
            }
        },

        BREAK() {
            @Override public boolean shouldMoveRovers() { return true; }
            @Override public boolean shouldDisplayScore() { return true; }

            @Override public void enter(PiNoonScreen s) {
                s.setBottomMessage(null, false);
                if (s.winner != null) {
                    timer = 180;
                    s.setMiddleMessage(s.winner + " won that round!", false);
                } else {
                    timer = 60;
                    s.setMiddleMessage(null, false);
                }
            }

            @Override public void update(PiNoonScreen s) {
                super.update(s);

                if (timer == 0) {
                    s.stateMachine.toState(GameState.ROUND, s);
                }
            }
        },

        ROUND() {
            @Override public boolean shouldMoveRovers() { return true; }
            @Override public boolean shouldDisplayScore() { return true; }

            @Override public void enter(PiNoonScreen s) {
                timer = 60;
                s.setMiddleMessage("round " + (s.players.get(0).playerScore + s.players.get(1).playerScore + 1), false);
            }

            @Override public void update(PiNoonScreen s) {
                super.update(s);

                if (timer == 0) {
                    s.countdown = 3;
                    s.stateMachine.toState(GameState.ROUND_COUNTDOWN, s);
                }
            }
        },

        ROUND_COUNTDOWN() {
            @Override public boolean shouldMoveRovers() { return true; }
            @Override public boolean shouldDisplayScore() { return true; }

            @Override public void enter(PiNoonScreen s) {
                timer = 60;
                s.setMiddleMessage(Integer.toString(s.countdown), false);
                if (s.countdown == 2) {
                    s.soundManager.playReady();
                }
            }

            @Override public void update(PiNoonScreen s) {
                super.update(s);

                if (timer == 0) {
                    s.countdown--;
                    if (s.countdown == 0) {
                        s.stateMachine.toState(GameState.GAME, s);
                    } else {
                        enter(s);
                    }
                }
            }
        },

        GAME() {
            @Override public boolean shouldMoveRovers() { return true; }
            @Override public boolean shouldDisplayScore() { return true; }

            @Override public void enter(PiNoonScreen s) {
                s.resetRovers();
                timer = 60;
                s.setMiddleMessage("GO!", false);
                s.setBottomMessage(null, false);
                s.soundManager.playFight();
            }

            @Override public void update(PiNoonScreen s) {
                super.update(s);

                if (timer == 0 ) {
                    s.setMiddleMessage(null, false);
                }

                PiNoonAttachment rover1PiNoonAttachment = s.players.get(0).getPiNoonAttachment();
                PiNoonAttachment rover2PiNoonAttachment = s.players.get(1).getPiNoonAttachment();

                if (rover1PiNoonAttachment.getUnpoppedBalloonsCount() == 0 || rover2PiNoonAttachment.getUnpoppedBalloonsCount() == 0) {
                    int player1score = s.players.get(0).playerScore;
                    int player2score = s.players.get(1).playerScore;
                    if (player1score + player2score >= 3) {
                        if (player1score > player2score) {
                            s.winner = s.players.get(0).name;
                        } else if (player1score < player2score) {
                            s.winner = s.players.get(1).name;
                        }
                        s.stateMachine.toState(GameState.END, s);
                    } else {
                        s.stateMachine.toState(GameState.BREAK, s);
                    }
                }
            }
        },

        END() {
            @Override public boolean shouldMoveRovers() { return true; }
            @Override public boolean shouldDisplayScore() { return true; }

            @Override public void enter(PiNoonScreen s) {
                s.setMiddleMessage(s.winner + " wins! " + s.players.get(0).playerScore + " - " + s.players.get(1).playerScore, false);
                s.setBottomMessage("Press space to return to menu!", true);
            }

            @Override public void exit(PiNoonScreen s) {
                s.setMiddleMessage(null, false);
            }
        };

        int timer;

        @Override public void enter(PiNoonScreen s) {}
        @Override public void update(PiNoonScreen s) {
            timer--;
        }
        @Override public void exit(PiNoonScreen s) {}

        public boolean shouldMoveRovers() { return false; }

        public boolean shouldDisplayScore() { return false; }

        public boolean shouldDisplayPlayerSelection() { return false; }
    }
}
