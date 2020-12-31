package dev.technici4n.fasttransferlib.base;

import dev.technici4n.fasttransferlib.api.transaction.Transaction;
import dev.technici4n.fasttransferlib.api.transfer.StorageFunction;

// TODO: validate that passed integers are >= 0
public interface IntegerStorageFunction<T> extends StorageFunction<T> {
	@Override
	default long apply(T resource, long numerator, long denominator, Transaction tx) {
		long whole = numerator / denominator;
		return numerator - denominator * (whole - apply(resource, whole, tx));
	}
}
