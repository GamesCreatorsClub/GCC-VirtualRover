package org.ah.gcc.virtualrover.statemachine;

public interface State<T> {
    void enter(T o);
    void exit(T o);
    void update(T o);
}
