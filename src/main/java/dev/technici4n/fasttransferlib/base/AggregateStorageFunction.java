package dev.technici4n.fasttransferlib.base;

import java.util.ArrayList;
import java.util.List;

import dev.technici4n.fasttransferlib.api.transfer.StorageFunction;

public class AggregateStorageFunction<T> implements StorageFunction<T> {
	private final List<StorageFunction<T>> parts;
	private final boolean isEmpty;

	public AggregateStorageFunction(List<? extends StorageFunction<T>> parts) {
		this.parts = new ArrayList<>(parts);
		boolean isEmpty = true;

		for (StorageFunction<T> part : parts) {
			if (!part.isEmpty()) {
				isEmpty = false;
			}
		}

		this.isEmpty = isEmpty;
	}

	@Override
	public long apply(T resource, long amount) {
		long total = 0;

		for (StorageFunction<T> part : parts) {
			total += part.apply(resource, amount - total);
		}

		return total;
	}

	@Override
	public long apply(T resource, long numerator, long denominator) {
		long total = 0;

		for (StorageFunction<T> part : parts) {
			total += part.apply(resource, numerator - total, denominator);
		}

		return total;
	}

	@Override
	public boolean isEmpty() {
		return isEmpty;
	}
}
