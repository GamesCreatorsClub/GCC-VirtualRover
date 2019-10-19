package org.ah.gcc.virtualrover.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import org.ah.gcc.virtualrover.Inputs;
import org.ah.gcc.virtualrover.MainGame;
import org.ah.gcc.virtualrover.ModelFactory;
import org.ah.gcc.virtualrover.backgrounds.PerlinNoiseBackground;
import org.ah.gcc.virtualrover.challenges.PiNoonArena;
import org.ah.gcc.virtualrover.rovers.*;
import org.ah.gcc.virtualrover.rovers.attachments.PiNoonAttachment;
import org.ah.gcc.virtualrover.utils.SoundManager;
import org.ah.gcc.virtualrover.view.Console;

import java.util.List;

import static org.ah.gcc.virtualrover.MainGame.SCALE;
import static org.ah.gcc.virtualrover.utils.MeshUtils.polygonsOverlap;

public class PiNoonScreen extends AbstractStandardScreen implements InputProcessor {

    private static final long BREAK = 300;

    private PerspectiveCamera camera;
    private CameraInputController camController;
    private InputMultiplexer cameraInputMultiplexer;
    private int cameratype = 3;
    private Vector3 pos1 = new Vector3();
    private Vector3 pos2 = new Vector3();
    private Vector3 midpoint = new Vector3();
    private float distance;

    private int mouseX = 0;
    private int mouseY = 0;
    private float rotSpeed = 0.1f;

    private boolean mouse = false;

    private long breakTime = 0;

    private GameState currentState = GameState.MENU;
    private boolean readySoundPlayed = false;
    private boolean fightSoundPlayed = false;

    private String winner = "NONE";

    private RoverType playerSelection1 = RoverType.GCC;
    private RoverType playerSelection2 = RoverType.CBIS;
    private Rover rover1;
    private Rover rover2;
    private Inputs rover1Inputs;
    private Inputs rover2Inputs;
    private int player1score = 0;
    private int player2score = 0;

    private boolean renderBackground = false;

    public PiNoonScreen(MainGame game, AssetManager assetManager, SoundManager soundManager, ModelFactory modelFactory, Console console) {
        super(game, assetManager, soundManager, modelFactory, console);
        this.game = game;
        this.assetManager = assetManager;
        this.soundManager = soundManager;
        this.modelFactory = modelFactory;
        this.console = console;

        setBackground(new PerlinNoiseBackground());
        setChallenge(new PiNoonArena(modelFactory));

        rover1Inputs = Inputs.create();
        rover2Inputs = Inputs.create();


        camera = new PerspectiveCamera(45, 800, 480);
        camera.position.set(300f * SCALE, 480f * SCALE, 300f * SCALE);
        camera.lookAt(0f, 0f, 0f);

        camera.near = 0.02f;
        camera.far = 1000f;

        camController = new CameraInputController(camera);

        cameraInputMultiplexer = new InputMultiplexer();
        cameraInputMultiplexer.addProcessor(this);
        cameraInputMultiplexer.addProcessor(camController);
        Gdx.input.setInputProcessor(cameraInputMultiplexer);
        Gdx.input.setCursorCatched(mouse);
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

        if (cameratype == 0) {
            camController.target.set(pos1);

            camera.lookAt(pos1);
            camera.up.set(new Vector3(0, 1, 0));
            camera.fieldOfView = 45;

        } else if (cameratype == 1) {
            pos1 = rover1.getTransform().getTranslation(pos1);
            pos1.y = 10f;

            camera.position.set(pos1);

            Quaternion q = new Quaternion();
            q = rover1.getTransform().getRotation(q);
            camera.direction.set(new Vector3(0.1f, 0, 0));
            camera.direction.rotate(new Vector3(0, 1, 0), 180 + q.getAngleAround(new Vector3(0, 1, 0)));

            camera.up.set(new Vector3(0, 1, 0));

            camera.fieldOfView = 120f;
        } else if (cameratype == 2) {
            pos1 = rover1.getTransform().getTranslation(pos1);
            camera.lookAt(pos1);
            camera.position.set(1500f * SCALE, 2400f * SCALE, 1500f * SCALE);
            camera.up.set(new Vector3(0, 1, 0));
            camera.fieldOfView = 120f;
        } else if (cameratype == 3) {
            float distanceBetweeRovers = 0f;
            distance = 450f;
            if (rover1 != null && rover2 != null) {
                pos1 = rover1.getTransform().getTranslation(pos1);
                pos2 = rover2.getTransform().getTranslation(pos2);
                distanceBetweeRovers = pos1.dst(pos2);
                midpoint.set(pos1).add(pos2).scl(0.5f);
                // distance = (-midpoint.dst(300f * SCALE, 0f, -300f * SCALE)) / SCALE;
                // distance = (distanceBetweeRovers) / SCALE;
                distance = 350f + (distanceBetweeRovers * 0.5f - midpoint.dst(1500f * SCALE, 0f, -1500f * SCALE) * 0.6f) / SCALE;
            } else {
                pos1.set(0f, 0f, 0f);
            }
            camera.lookAt(midpoint);
            camera.position.set(distance * SCALE, (1000f + distanceBetweeRovers * 200f) * SCALE, -distance * SCALE);
            camera.up.set(new Vector3(0, 1, 0));
            camera.fieldOfView = 45;
        }

        camera.update();

        Gdx.graphics.getGL20().glEnable(GL20.GL_BLEND);
        Gdx.graphics.getGL20().glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        if (renderBackground) {
            background.render(camera, batch, environment);
        }

        batch.begin(camera);

        challenge.render(batch, environment);



        int margin = 64;
        spriteBatch.begin();

        spriteBatch.draw(gccLogo, 0, Gdx.graphics.getHeight() - gccLogo.getHeight());

        if (currentState == GameState.BREAK || currentState == GameState.GAME || currentState == GameState.END) {
            font.draw(spriteBatch, player1score + " - " + player2score, Gdx.graphics.getWidth() - 120, Gdx.graphics.getHeight() - 40);
        }

        breakTime--;
        if (currentState == GameState.SELECTION) {
            font.draw(spriteBatch, playerSelection1.getName(), margin, Gdx.graphics.getHeight() / 2 + margin);
            font.draw(spriteBatch, playerSelection2.getName(), margin + Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 + margin);
            setBottomMessage("Press space to begin", true);
        } else if (currentState == GameState.MENU) {
            setBottomMessage("Press space to begin", true);
        } else if (currentState == GameState.END) {
            moveRovers();
            setMiddleMessage(winner + " wins! " + player1score + " - " + player2score, false);
            setBottomMessage("Press space to return to menu!", true);
        } else if (currentState == GameState.BREAK) {
            moveRovers();
            setBottomMessage(null, false);

            if (breakTime < 60) {
                setMiddleMessage("1", false);
            } else if (breakTime < 120) {
                setMiddleMessage("2", false);
            } else if (breakTime < 180) {
                setMiddleMessage("3", false);
                if (!readySoundPlayed) {
                    soundManager.playReady();
                    readySoundPlayed = true;
                }
            } else if (breakTime < 240) {
                setMiddleMessage("round " + (player1score + player2score + 1), false);
            } else {
                if (!winner.equals("NONE")) {
                    setMiddleMessage(winner + " won that round!", false);
                } else {
                    setMiddleMessage(null, false);
                }
            }
            if (breakTime < 0) {
                currentState = GameState.GAME;
                fightSoundPlayed = false;
                resetRobots();
            }
        } else if (currentState == GameState.GAME) {
            moveRovers();
            setBottomMessage(null, false);

            if (breakTime > -60) {
                setMiddleMessage("GO!", false);
                if (!fightSoundPlayed) {
                    soundManager.playFight();
                    fightSoundPlayed = true;
                }
            } else {
                setMiddleMessage(null, false);
            }
            boolean end = false;
            PiNoonAttachment rover1PiNoonAttachment = (PiNoonAttachment)rover1.getAttachemnt();
            PiNoonAttachment rover2PiNoonAttachment = (PiNoonAttachment)rover2.getAttachemnt();

            if (rover1PiNoonAttachment.checkIfBalloonsPopped(rover2PiNoonAttachment.getSharpPoint()) == 0) {
                end = true;
                player1score++;
                winner = "Green";
            } else if (rover2PiNoonAttachment.checkIfBalloonsPopped(rover1PiNoonAttachment.getSharpPoint()) == 0) {
                end = true;
                player2score++;
                winner = "Blue";
            }

            if (end) {
                if (player1score + player2score >= 3) {
                    currentState = GameState.END;
                    if (player1score > player2score) {
                        winner = "Green";
                    } else if (player1score < player2score) {
                        winner = "Blue";
                    }
                } else {
                    currentState = GameState.BREAK;
                    readySoundPlayed = false;
                    breakTime = BREAK;
                }
            }
        }

        spriteBatch.end();
        batch.end();

        drawStandardMessages();

        if (console != null) {
            console.render();
        }
    }

    private void moveRovers() {
        if (rover1 != null && rover2 != null) {
            rover2Inputs.moveUp(Gdx.input.isKeyPressed(Input.Keys.I));
            rover2Inputs.moveDown(Gdx.input.isKeyPressed(Input.Keys.K));
            rover2Inputs.moveLeft(Gdx.input.isKeyPressed(Input.Keys.J));
            rover2Inputs.moveRight(Gdx.input.isKeyPressed(Input.Keys.L));
            rover2Inputs.rotateLeft(Gdx.input.isKeyPressed(Input.Keys.U));
            rover2Inputs.rotateRight(Gdx.input.isKeyPressed(Input.Keys.O));

            rover1Inputs.moveUp(Gdx.input.isKeyPressed(Input.Keys.W));
            rover1Inputs.moveDown(Gdx.input.isKeyPressed(Input.Keys.S));
            rover1Inputs.moveLeft(Gdx.input.isKeyPressed(Input.Keys.A));
            rover1Inputs.moveRight(Gdx.input.isKeyPressed(Input.Keys.D));
            rover1Inputs.rotateLeft(Gdx.input.isKeyPressed(Input.Keys.Q));
            rover1Inputs.rotateRight(Gdx.input.isKeyPressed(Input.Keys.E));

            Matrix4 newRover1Position = rover1.processInput(rover1Inputs);
            Matrix4 newRover2Position = rover2.processInput(rover2Inputs);

            boolean rover1Moves = newRover1Position != null;
            boolean rover2Moves = newRover2Position != null;

            if (rover1Moves || rover2Moves) {
                Matrix4 rover1Position = rover1.getPreviousTransform();
                Matrix4 rover2Position = rover2.getPreviousTransform();
                if (newRover1Position == null) {
                    newRover1Position = rover1Position;
                }
                if (newRover2Position == null) {
                    newRover2Position = rover2Position;
                }

                if (rover1Moves) {
                    rover1.getTransform().set(newRover1Position);
                    rover1.update();
                }

                if (rover2Moves) {
                    rover2.getTransform().set(newRover2Position);
                    rover2.update();
                }

                List<Polygon> rover1Poligons = rover1.getPolygons();
                List<Polygon> rover2Poligons = rover2.getPolygons();

                boolean roversCollide = polygonsOverlap(rover1Poligons, rover2Poligons);

                if (roversCollide) {
                    if (rover1Moves) {
                        rover1.getTransform().set(rover1Position);
                        rover1.update();
                    }
                    if (rover2Moves) {
                        rover2.getTransform().set(rover2Position);
                        rover2.update();
                    }
                } else {
                    if (rover1Moves && challenge.collides(rover1Poligons)) {
                        rover1.getTransform().set(rover1Position);
                        rover1.update();
                    }
                    if (rover2Moves && challenge.collides(rover2Poligons)) {
                        rover2.getTransform().set(rover2Position);
                        rover2.update();
                    }
                }
            }

            rover1.render(batch, environment, true); // currentState == GameState.GAME);
            rover2.render(batch, environment, true); // currentState == GameState.GAME);
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        // TODO add handling of current main camera
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.F1) {
            cameratype++;
            if (cameratype > 3) {
                cameratype = 0;
            }
        } else if (keycode == Input.Keys.F2) {
            cameratype--;
            if (cameratype < 0) {
                cameratype = 3;
            }
        }
        if (keycode == Input.Keys.ESCAPE) {
            mouse = !mouse;
            Gdx.input.setCursorCatched(mouse);
            if (mouse) {
                Gdx.input.setInputProcessor(this);
            } else {
                Gdx.input.setInputProcessor(cameraInputMultiplexer);
            }
        }
        if (currentState == GameState.SELECTION) {

            if (keycode == Input.Keys.D) {
                playerSelection1 = playerSelection1.getNext();
            } else if (keycode == Input.Keys.A) {
                playerSelection1 = playerSelection1.getPrevious();
            }

            if (keycode == Input.Keys.L) {
                playerSelection2 = playerSelection2.getNext();
            } else if (keycode == Input.Keys.J) {
                playerSelection2 = playerSelection2.getPrevious();
            }

            if (keycode == Input.Keys.SPACE) {
                rover1 = makeRobot(playerSelection1, "1", Color.BLUE);
                rover2 = makeRobot(playerSelection2, "2", Color.GREEN);
                rover2.setId(2);
                rover1.setAttachment(new PiNoonAttachment(modelFactory, rover1.getColour()));
                rover2.setAttachment(new PiNoonAttachment(modelFactory, rover2.getColour()));

                resetRobots();

                ((PiNoonAttachment)rover1.getAttachemnt()).removeBalloons();

                ((PiNoonAttachment)rover2.getAttachemnt()).removeBalloons();

                player1score = 0;
                player2score = 0;
                currentState = GameState.BREAK;
                readySoundPlayed = false;
                breakTime = BREAK + 100;
                winner = "NONE";
            }
        }

        if (keycode == Input.Keys.SPACE) {
            if (currentState == GameState.END) {
                currentState = GameState.MENU;
            } else if (currentState == GameState.MENU) {
                currentState = GameState.SELECTION;
            }
        }

        if (keycode == Input.Keys.TAB) {
            camera.fieldOfView = 4f;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.TAB) {
            camera.fieldOfView = 45f;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (mouse) {
            int magX = Math.abs(mouseX - screenX);
            int magY = Math.abs(mouseY - screenY);

            if (mouseX > screenX) {
                camera.rotate(Vector3.Y, 1 * magX * rotSpeed);
                camera.update();
            }

            if (mouseX < screenX) {
                camera.rotate(Vector3.Y, -1 * magX * rotSpeed);
                camera.update();
            }

            if (mouseY < screenY) {
                if (camera.direction.y > -1)
                    camera.rotate(camera.direction.cpy().crs(Vector3.Y), -1 * magY * rotSpeed);
                camera.update();
            }

            if (mouseY > screenY) {

                if (camera.direction.y < 1)
                    camera.rotate(camera.direction.cpy().crs(Vector3.Y), 1 * magY * rotSpeed);
                camera.update();
            }

            mouseX = screenX;
            mouseY = screenY;
        }
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    public AbstractRover makeRobot(RoverType t, String name, Color color) {
        AbstractRover r;
        if (t == RoverType.CBIS) {
            r = new CBiSRover(name, modelFactory, color);
        } else {
            r = new GCCRover(name, modelFactory, color);

        }
        return r;
    }

    public void resetRobots() {
        rover1.getTransform().idt();
        rover1.getTransform().setToTranslationAndScaling(700 * SCALE, 0, 700 * SCALE, SCALE, SCALE, SCALE);
        rover1.getTransform().rotate(new Vector3(0, 1, 0), -45);
        rover1.update();

        rover2.getTransform().idt();
        rover2.getTransform().setToTranslationAndScaling(-700 * SCALE, 0, -700 * SCALE, SCALE, SCALE, SCALE);
        rover2.getTransform().rotate(new Vector3(0, 1, 0), 180 - 45);
        rover2.update();

        ((PiNoonAttachment)rover1.getAttachemnt()).resetBalloons();
        ((PiNoonAttachment)rover2.getAttachemnt()).resetBalloons();
    }

    private enum GameState {
        MENU, SELECTION, GAME, BREAK, END
    }
}
