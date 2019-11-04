package org.ah.gcc.virtualrover.message;

import org.ah.gcc.virtualrover.input.GCCPlayerInput;
import org.ah.gcc.virtualrover.input.GCCPlayerInputs;
import org.ah.themvsus.engine.common.input.PlayerInputs;
import org.ah.themvsus.engine.common.message.MessageFactory;
import org.ah.themvsus.engine.common.message.PlayerInputMessage;

public class GCCPlayerInputMessage extends PlayerInputMessage {

    GCCPlayerInputMessage(MessageFactory messageFactory) {
        super(messageFactory);
    }

    @Override
    protected PlayerInputs createPlayerInputs() {
        return new GCCPlayerInputs();
    }

    @Override
    public PlayerInputs getInputs() {
        return playerInputs;
    }

    public void addInput(int sessionId, int frameNo, GCCPlayerInput playerInput) {
        this.sessionId = sessionId;
        this.frameNo = frameNo;

        ((GCCPlayerInputs)playerInputs).addGCCInput(frameNo, playerInput);
    }
}
