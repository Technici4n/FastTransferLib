package dev.technici4n.fasttransferlib.api.transfer;

public interface Storage<T> {
	ResourceFunction<T> insertionFunction();
	ResourceFunction<T> extractionFunction();

	boolean isEmpty();
	// return true to stop the visit
	void forEach(Visitor<T> visitor);
	int getVersion();

	@FunctionalInterface
	interface Visitor<T> {
		// return true to stop the visit
		boolean visit(Stored<T> stored);
	}
}
