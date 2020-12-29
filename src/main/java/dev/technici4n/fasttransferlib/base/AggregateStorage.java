package dev.technici4n.fasttransferlib.base;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dev.technici4n.fasttransferlib.api.transfer.ResourceFunction;
import dev.technici4n.fasttransferlib.api.transfer.Storage;

// sadly doesn't support versioning :(
public class AggregateStorage<T> implements Storage<T> {
	private final List<Storage<T>> parts;
	private final ResourceFunction<T> insertionFunction;
	private final ResourceFunction<T> extractionFunction;

	public AggregateStorage(List<? extends Storage<T>> parts) {
		this.parts = new ArrayList<>(parts);
		this.insertionFunction = new AggregateResourceFunction<>(parts.stream().map(Storage::insertionFunction).collect(Collectors.toList()));
		this.extractionFunction = new AggregateResourceFunction<>(parts.stream().map(Storage::extractionFunction).collect(Collectors.toList()));
	}

	@Override
	public ResourceFunction<T> insertionFunction() {
		return insertionFunction;
	}

	@Override
	public ResourceFunction<T> extractionFunction() {
		return extractionFunction;
	}

	@Override
	public boolean forEach(Visitor<T> visitor) {
		for (Storage<T> part : parts) {
			if (part.forEach(visitor)) {
				return true;
			}
		}

		return false;
	}
}
