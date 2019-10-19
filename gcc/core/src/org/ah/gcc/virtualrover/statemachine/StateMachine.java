package org.ah.gcc.virtualrover.statemachine;

public class StateMachine<T, S extends State<T>> {

    private S currentState;

    public StateMachine() {
    }

    public S getCurrentState() {
        return currentState;
    }

    public boolean isState(S state) {
        return currentState == state;
    }

    public void setCurrentState(S state) {
        this.currentState = state;
    }

    public void toState(S nextState, T t) {
        if (currentState != null) {
            currentState.exit(t);
        }
        currentState = nextState;
        nextState.enter(t);
    }

    public void update(T t) {
        if (currentState != null) {
            currentState.update(t);
        }
    }
}
