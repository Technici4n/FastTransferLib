package dev.technici4n.fasttransferlib.api.transfer;

public interface Storage<T> {
	boolean supportsInsertion();
	ResourceFunction<T> insertionFunction();
	boolean supportsExtraction();
	ResourceFunction<T> extractionFunction();

	boolean isEmpty();
	boolean isFull();
	// return true to stop the visit
	void forEach(Visitor<T> visitor);
	int getVersion();

	@FunctionalInterface
	interface Visitor<T> {
		// return true to stop the visit
		boolean visit(Stored<T> stored);
	}
}
