package dev.technici4n.fasttransferlib.api.transfer;

import dev.technici4n.fasttransferlib.api.Fraction;

public interface Stored<T> {
	ResourceFunction<T> extractionFunction();

	T resource();
	long count();
	long amount(long denominator);
	Fraction amount();
	boolean isEmpty();
}
