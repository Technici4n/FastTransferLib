package dev.technici4n.fasttransferlib.api.transfer;

public interface StorageView<T> {
	default StorageFunction<T> extractionFunction() {
		return StorageFunction.empty();
	}

	T resource();
	long amount();
	long amount(long denominator);
	long denominator();
}
