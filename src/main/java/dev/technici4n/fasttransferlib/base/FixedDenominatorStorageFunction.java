package dev.technici4n.fasttransferlib.base;

import dev.technici4n.fasttransferlib.api.transfer.StorageFunction;

public interface FixedDenominatorStorageFunction<T> extends StorageFunction<T> {
	long denominator();
	long applyFixedDenominator(T resource, long numerator);

	@Override
	default long apply(T resource, long amount) {
		return apply(resource, amount, 1);
	}

	@Override
	default long apply(T resource, long numerator, long denominator) {
		long ownDenom = denominator();

		if (denominator % ownDenom == 0) {
			// if the passed denominator is a multiple of this denominator, handling is trivial
			long ratio = denominator / ownDenom;
			return applyFixedDenominator(resource, numerator / ratio) * ratio;
		} else {
			// otherwise, the transfer will necessarily happen with the gcd of the denominators
			// TODO: write this nightmare
			return 0;
		}
	}
}
