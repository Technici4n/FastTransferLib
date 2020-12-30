package dev.technici4n.fasttransferlib.api;

public enum Simulation {
	SIMULATE,
	ACT;

	public boolean isSimulating() {
		return this == SIMULATE;
	}

	public boolean isActing() {
		return this == ACT;
	}
}
