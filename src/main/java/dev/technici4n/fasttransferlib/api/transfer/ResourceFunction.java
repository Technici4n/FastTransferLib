package dev.technici4n.fasttransferlib.api.transfer;

import dev.technici4n.fasttransferlib.api.Fraction;
import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.transaction.Participant;

public interface ResourceFunction<T> extends Participant {
	long apply(T resource, long count, Simulation simulation);
	long apply(T resource, long numerator, long denominator, Simulation simulation);
	Fraction apply(T resource, Fraction amount, Simulation simulation);

	boolean isReturningZero();
}
