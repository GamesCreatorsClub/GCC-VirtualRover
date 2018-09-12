package org.ah.gcc.virtualrover;

import java.util.NoSuchElementException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.utils.UBJsonReader;

public class ModelFactory {
    private G3dModelLoader g3Modelloader;
    private ObjLoader objModelloader;
    private Model baloon;
    private Model body;
    private Model pinoon;
    private Model top;
    private Model tyre;
    private Model wheel;

    private Model motorholder;
    private Model fullwheel;
    private Model cBody;
    private Model bigWheel;
    private Model bigTyre;
    private Model marker;

    public ModelFactory() {
        UBJsonReader jsonreader = new UBJsonReader();
        g3Modelloader = new G3dModelLoader(jsonreader);
        objModelloader = new ObjLoader();

    }

    public Model loadModel(String filename) {
        Model m = null;
        FileHandle f = Gdx.files.internal(filename);
        if (!f.exists()) {
            f = Gdx.files.internal("3d/" + filename);
        }

        if (filename.endsWith(".g3db")) {
            m = g3Modelloader.loadModel(f);
        } else {
            m = objModelloader.loadModel(f, true);
        }
        return m;
    }

    public void load() {
        baloon = loadModel("balloon1.obj");
        body = loadModel("Body.obj");
        pinoon = loadModel("PiNoon.obj");
        top = loadModel("Top.obj");
        tyre = loadModel("Tyre.obj");
        fullwheel = loadModel("FullWheel.obj");

        cBody = loadModel("cBody.obj");
        setBigWheel(loadModel("cwheel.obj"));
        bigTyre = loadModel("cTyre.obj");

        wheel = loadModel("Wheel.obj");
        motorholder = loadModel("MotorHolder.obj");

        marker = loadModel("teapot.g3db");
    }

    private Exception resourceDoesNotExists(String name) throws NoSuchElementException {
        throw new NoSuchElementException("No model found for " + name + ": try load()");
    }

    public Model getBaloon() throws NoSuchElementException {
        if (baloon == null) {
            resourceDoesNotExists("balloon");
        }
        return baloon;
    }

    public Model getMotorHolder() throws NoSuchElementException {
        if (motorholder == null) {
            resourceDoesNotExists("motor holder");
        }
        return motorholder;
    }

    public Model getFullWheel() throws NoSuchElementException {
        if (fullwheel == null) {
            resourceDoesNotExists("full wheel");
        }
        return fullwheel;
    }

    public Model getBody() throws NoSuchElementException {
        if (body == null) {
            resourceDoesNotExists("body");
        }
        return body;
    }

    public Model getPiNoon() throws NoSuchElementException {
        if (pinoon == null) {
            resourceDoesNotExists("pinoon");
        }
        return pinoon;
    }

    public Model getTop() throws NoSuchElementException {
        if (top == null) {
            resourceDoesNotExists("top");
        }
        return top;
    }

    public Model getTyre() throws NoSuchElementException {
        if (tyre == null) {
            resourceDoesNotExists("tyre");
        }
        return tyre;
    }

    public Model getWheel() throws NoSuchElementException {
        if (wheel == null) {

            resourceDoesNotExists("wheel");
        }
        return wheel;
    }

    public Model getMarker() throws NoSuchElementException {
        if (marker == null) {

            resourceDoesNotExists("worker");
        }
        return marker;
    }

    public Model getcBody() {
        return cBody;
    }

    public void setcBody(Model cBody) {
        this.cBody = cBody;
    }

    public Model getBigTyre() {
        return bigTyre;
    }

    public void setBigTyre(Model bigTyre) {
        this.bigTyre = bigTyre;
    }

    public Model getBigWheel() {
        return bigWheel;
    }

    public void setBigWheel(Model bigWheel) {
        this.bigWheel = bigWheel;
    }
}
