package dev.technici4n.fasttransferlib.api;

public enum Simulation {
    SIMULATE,
    ACT;

    public boolean isSimulate() {
        return this == SIMULATE;
    }

    public boolean isAct() {
        return this == ACT;
    }
}
