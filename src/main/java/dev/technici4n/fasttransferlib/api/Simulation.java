package dev.technici4n.fasttransferlib.api;

import dev.technici4n.fasttransferlib.api.transaction.Participant;
import dev.technici4n.fasttransferlib.api.transaction.Transaction;

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
