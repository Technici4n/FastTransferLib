package dev.technici4n.fasttransferlib.api.transfer;

import dev.technici4n.fasttransferlib.impl.FtlImpl;

public interface StorageFunction<T> {
	long apply(T resource, long amount);
	long apply(T resource, long numerator, long denominator);

	default boolean isEmpty() {
		return false;
	}

	@SuppressWarnings("unchecked")
	static <T> StorageFunction<T> empty() {
		return FtlImpl.EMPTY;
	}
}
