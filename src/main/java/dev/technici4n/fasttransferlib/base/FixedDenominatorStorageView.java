package dev.technici4n.fasttransferlib.base;

import dev.technici4n.fasttransferlib.api.transfer.StorageView;

public interface FixedDenominatorStorageView<T> extends StorageView<T> {
	@Override
	long denominator();
	long amountFixedDenominator();

	@Override
	default long amount() {
		return amountFixedDenominator() / denominator();
	}

	@Override
	default long amount(long denominator) {
		return amountFixedDenominator() * denominator / denominator();
	}
}
