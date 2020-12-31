package dev.technici4n.fasttransferlib.base;

import dev.technici4n.fasttransferlib.api.transfer.StorageView;

public interface IntegerStorageView<T> extends StorageView<T> {
	@Override
	default long amount(long denominator) {
		return amount() * denominator;
	}

	@Override
	default long denominator() {
		return 1;
	}
}
