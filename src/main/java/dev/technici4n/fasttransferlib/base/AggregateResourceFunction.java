package dev.technici4n.fasttransferlib.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dev.technici4n.fasttransferlib.api.Fraction;
import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.transfer.ResourceFunction;

public class AggregateResourceFunction<T> implements ResourceFunction<T> {
	private final List<ResourceFunction<T>> parts;

	public AggregateResourceFunction(List<ResourceFunction<T>> parts) {
		this.parts = new ArrayList<>(parts);
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
	public Fraction apply(T resource, Fraction amount, Simulation simulation) {
		// TODO: use MutableFraction to minimize allocation
		Fraction total = new Fraction();

		for (ResourceFunction<T> part : parts) {
			total = total.withAddition(part.apply(resource, amount.withSubtraction(total), simulation));
		}

		return total;
	}

	@Override
	public boolean canApply() {
		for (ResourceFunction<T> part : parts) {
			if (part.canApply()) {
				return true;
			}
		}

		return false;
	}
}
