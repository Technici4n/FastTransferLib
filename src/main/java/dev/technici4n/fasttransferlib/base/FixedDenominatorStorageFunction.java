package dev.technici4n.fasttransferlib.base;

import com.google.common.math.LongMath;
import dev.technici4n.fasttransferlib.api.transaction.Transaction;
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
			long g = LongMath.gcd(ownDenom, denominator);
			long factor = denominator / g;
			long ownFactor = ownDenom / g;
			long commonAmount = numerator / factor;

			// the first try uses commonAmount, and returns if it is successful
			// the second try uses the rounded-down amount returned by the first try
			for (int tries = 0; tries < 2 && commonAmount > 0; ++tries) {
				try (Transaction tx = Transaction.open()) {
					// try to apply with the common amount
					long result = applyFixedDenominator(resource, commonAmount * ownFactor);

					// if the result can be converted back to the gcd, this is successful.
					if (result % ownFactor == 0) {
						tx.commit();
						return result / ownFactor * factor;
					} else {
						// otherwise, rollback and try rounding down
						commonAmount = result / ownFactor;
					}
				}
			}

			return 0;
		}
	}
}
