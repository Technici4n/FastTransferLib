package dev.technici4n.fasttransferlib.api.transfer;

import dev.technici4n.fasttransferlib.impl.FtlImpl;

public interface Storage<T> {
	default ResourceFunction<T> insertionFunction() {
		return ResourceFunction.empty();
	}
	default ResourceFunction<T> extractionFunction() {
		return ResourceFunction.empty();
	}

	// return true to stop the visit
	void forEach(Visitor<T> visitor);

	default int getVersion() {
		return FtlImpl.version++;
	}

	@FunctionalInterface
	interface Visitor<T> {
		// return true to stop the visit
		boolean visit(Stored<T> stored);
	}
}
