package dev.technici4n.fasttransferlib.base;

import java.util.ArrayList;
import java.util.List;

import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.transfer.ResourceFunction;

public class AggregateResourceFunction<T> implements ResourceFunction<T> {
	private final List<ResourceFunction<T>> parts;
	private final boolean canApply;

	public AggregateResourceFunction(List<? extends ResourceFunction<T>> parts) {
		this.parts = new ArrayList<>(parts);
		boolean canApply = false;
		for (ResourceFunction<T> part : parts) {
			if (part.canApply()) {
				canApply = true;
			}
		}
		this.canApply = canApply;
	}

	@Override
	public long apply(T resource, long count, Simulation simulation) {
		long total = 0;

		for (ResourceFunction<T> part : parts) {
			total += part.apply(resource, count - total, simulation);
		}

		return total;
	}

	@Override
	public long apply(T resource, long numerator, long denominator, Simulation simulation) {
		long total = 0;

		for (ResourceFunction<T> part : parts) {
			total += part.apply(resource, numerator - total, denominator, simulation);
		}

		return total;
	}

	@Override
	public boolean canApply() {
		return canApply;
	}
}
