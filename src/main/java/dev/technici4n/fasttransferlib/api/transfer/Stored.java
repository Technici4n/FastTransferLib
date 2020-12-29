package dev.technici4n.fasttransferlib.api.transfer;

public interface Stored<T> {
	default ResourceFunction<T> extractionFunction() {
		return ResourceFunction.empty();
	}

	T resource();
	long count();
	long amount(long denominator);
	long denominator();
}
