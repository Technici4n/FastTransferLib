package dev.technici4n.fasttransferlib.base;

import dev.technici4n.fasttransferlib.api.Fraction;
import dev.technici4n.fasttransferlib.api.transfer.Stored;

public interface DiscreteStored<T> extends Stored<T> {
	@Override
	default long amount(long denominator) {
		return count() * denominator;
	}

	@Override
	default Fraction amount() {
		return Fraction.of(count());
	}
}
