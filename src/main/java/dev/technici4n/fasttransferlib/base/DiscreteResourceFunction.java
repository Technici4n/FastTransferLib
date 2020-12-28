package dev.technici4n.fasttransferlib.base;

import dev.technici4n.fasttransferlib.api.Fraction;
import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.transfer.ResourceFunction;

// TODO: validate that passed integers are >= 0
public interface DiscreteResourceFunction<T> extends ResourceFunction<T> {
	@Override
	default long apply(T resource, long numerator, long denominator, Simulation simulation) {
		long whole = numerator / denominator;
		return numerator - denominator * (whole - apply(resource, whole, simulation));
	}

	@Override
	default Fraction apply(T resource, Fraction amount, Simulation simulation) {
		long whole = amount.whole();
		return amount.withSubtraction(Fraction.of(apply(resource, whole, simulation)));
	}
}
