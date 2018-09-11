package org.ah.gcc.virtualrover;

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

    private String errorMessage = "No model found: try load()";
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

    public Model getBaloon() throws Exception {
        if (baloon == null) {
            throw (new Exception(errorMessage));
        }
        return baloon;
    }

    public Model getMotorHolder() throws Exception {
        if (motorholder == null) {
            throw (new Exception(errorMessage));
        }
        return motorholder;
    }

    public Model getFullWheel() throws Exception {
        if (fullwheel == null) {
            throw (new Exception(errorMessage));
        }
        return fullwheel;
    }

    public Model getBody() throws Exception {
        if (body == null) {
            throw (new Exception(errorMessage));
        }
        return body;
    }

    public Model getPiNoon() throws Exception {
        if (pinoon == null) {
            throw (new Exception(errorMessage));
        }
        return pinoon;
    }

    public Model getTop() throws Exception {
        if (top == null) {
            throw (new Exception(errorMessage));
        }
        return top;
    }

    public Model getTyre() throws Exception {
        if (tyre == null) {
            throw (new Exception(errorMessage));
        }
        return tyre;
    }

    public Model getWheel() throws Exception {
        if (wheel == null) {

            throw (new Exception(errorMessage));
        }
        return wheel;
    }

    public Model getMarker() throws Exception {
        if (marker == null) {

            throw (new Exception(errorMessage));
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
