package dev.technici4n.fasttransferlib.api.transfer;

import dev.technici4n.fasttransferlib.api.transaction.Transaction;
import dev.technici4n.fasttransferlib.impl.FtlImpl;

public interface StorageFunction<T> {
	long apply(T resource, long amount, Transaction tx);
	long apply(T resource, long numerator, long denominator, Transaction tx);

	default boolean isEmpty() {
		return false;
	}

	@SuppressWarnings("unchecked")
	static <T> StorageFunction<T> empty() {
		return FtlImpl.EMPTY;
	}
}
