package dev.technici4n.fasttransferlib.api.transfer;

import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.impl.FtlImpl;

public interface StorageFunction<T> {
	long apply(T resource, long count, Simulation simulation);
	long apply(T resource, long numerator, long denominator, Simulation simulation);

	default boolean isEmpty() {
		return false;
	}

	@SuppressWarnings("unchecked")
	static <T> StorageFunction<T> empty() {
		return FtlImpl.EMPTY;
	}
}
