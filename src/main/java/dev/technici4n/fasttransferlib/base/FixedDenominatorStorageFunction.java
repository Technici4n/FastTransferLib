package dev.technici4n.fasttransferlib.base;

import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.transfer.StorageFunction;

public interface FixedDenominatorStorageFunction<T> extends StorageFunction<T> {
	long denominator();
	long applyFixedDenominator(T resource, long numerator, Simulation simulation);

	@Override
	default long apply(T resource, long count, Simulation simulation) {
		return apply(resource, count, 1, simulation);
	}

	@Override
	default long apply(T resource, long numerator, long denominator, Simulation simulation) {
		long ownDenom = denominator();

		if (denominator % ownDenom == 0) {
			// if the passed denominator is a multiple of this denominator, handling is trivial
			long ratio = denominator / ownDenom;
			return applyFixedDenominator(resource, numerator / ratio, simulation) * ratio;
		} else {
			// otherwise, the transfer will necessarily happen with the gcd of the denominators
			// TODO: write this nightmare
			return 0;
		}
	}
}
