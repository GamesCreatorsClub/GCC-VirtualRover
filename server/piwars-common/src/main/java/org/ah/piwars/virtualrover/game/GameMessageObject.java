package org.ah.piwars.virtualrover.game;

import org.ah.themvsus.engine.common.game.Game;
import org.ah.themvsus.engine.common.game.GameObject;
import org.ah.themvsus.engine.common.game.GameObjectFactory;
import org.ah.themvsus.engine.common.game.GameObjectType;
import org.ah.themvsus.engine.common.game.GameObjectWithPosition;
import org.ah.themvsus.engine.common.transfer.Serializer;

public class GameMessageObject extends GameObject {

    private boolean flashing = false;
    private boolean inGame = false;
    private boolean waiting = false;
    private boolean hasTimer = false;
    private boolean timerStopped = false;
    private String message = "";
    private int timer = 0;

    public GameMessageObject(GameObjectFactory factory, int id) {
        super(factory, id);
        nonInteractive = true;
    }

    @Override
    public GameObjectType getType() { return PiWarsGameTypeObject.GameMessageObject; }

    public boolean isInGame() {
        return inGame;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    public boolean isWaiting() {
        return waiting;
    }

    public void setWaiting(boolean waiting) {
        this.waiting = waiting;
    }

    public boolean hasTimer() {
        return hasTimer;
    }

    public void setHasTimer(boolean hasTimer) {
        this.hasTimer = hasTimer;
    }

    public boolean isTimerStopped() {
        return timerStopped;
    }

    public void setTimerStopped(boolean timerStopped) {
        this.timerStopped = timerStopped;
    }

    public int getTimer() {
        return timer;
    }

    public int getTimerTens(Game game) {
        long tens = game.getGameTickMicros() * timer / 100000L;
        return (int)tens;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public void setTimerTens(int tens, Game game) {
        this.timer = (int)(tens * 100000L / game.getGameTickMicros());
    }

    public void setMessage(String message, boolean flashing) {
        if (message == null) {
            message = "";
        }
        if (!message.equals(this.message) || flashing != this.flashing) {
            changed = true;
        }

        this.message = message;
        this.flashing = flashing;
    }

    public boolean isFlashing() {
        return flashing;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void process(Game game, Iterable<GameObjectWithPosition> objects) {
        if (hasTimer && !timerStopped && timer > 0) {
            timer--;
        }
    }

    @Override
    public void serialize(boolean full, Serializer serializer) {
        super.serialize(full, serializer);

        serializer.serializeUnsignedByte((flashing ? 1 : 0)
                + (inGame ? 2 : 0)
                + (waiting ? 4 : 0)
                + (hasTimer ? 8 : 0)
                + (timerStopped ? 16 : 0));
        serializer.serializeShort(timer);
        serializer.serializeString(message);
    }

    @Override
    public void deserialize(boolean full, Serializer serializer) {
        super.deserialize(full, serializer);

        int status = serializer.deserializeUnsignedByte();
        boolean flashing = (status & 0x1) != 0;
        boolean inGame = (status & 0x2) != 0;
        boolean waiting = (status & 0x4) != 0;
        boolean hasTimer = (status & 0x8) != 0;
        boolean timerStopped = (status & 0x10) != 0;

        int timer = serializer.deserializeShort();
        String message = serializer.deserializeString();

        changed = changed || flashing != this.flashing;
        changed = changed || inGame != this.inGame;
        changed = changed || waiting != this.waiting;
        changed = changed || hasTimer != this.hasTimer;
        changed = changed || timerStopped != this.timerStopped;
        changed = changed || (timer != this.timer);
        changed = changed || !this.message.equals(message);

        this.flashing = flashing;
        this.inGame = inGame;
        this.waiting = waiting;
        this.hasTimer = hasTimer;
        this.timerStopped = timerStopped;
        this.message = message;
        this.timer = timer;
    }

    @Override
    public int size(boolean full) {
        return super.size(full) + 1 + 2 + 2 + (message != null ? message.length() : 0);
    }

    @Override
    protected GameObject copyInt(GameObject newObject) {
        super.copyInt(newObject);
        GameMessageObject gameMessageObject = (GameMessageObject)newObject;
        gameMessageObject.flashing = flashing;
        gameMessageObject.inGame = inGame;
        gameMessageObject.waiting = waiting;
        gameMessageObject.hasTimer = hasTimer;
        gameMessageObject.timerStopped = timerStopped;
        gameMessageObject.timer = timer;
        gameMessageObject.message = message;
        return gameMessageObject;
    }

    @Override
    public String toString() {
        return "GameMessage" + (changed ? "*[" : "[") + id + ", ingame=" + inGame + ", waiting=" + waiting + ", hasTimer=" + hasTimer + ", timer=" + timer + ", msg=\"" + message + "\"]";
    }
}
