package dev.technici4n.fasttransferlib.api.transfer;

import dev.technici4n.fasttransferlib.api.Fraction;

public interface Stored<T> {
	boolean supportsInsertion();
	ResourceFunction<T> insertionFunction();
	boolean supportsExtraction();
	ResourceFunction<T> extractionFunction();

	T resource();
	long count();
	long amount(long denominator);
	Fraction amount();
	boolean isEmpty();
}
