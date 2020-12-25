package dev.technici4n.fasttransferlib.api.transfer;

import java.util.function.Predicate;

import dev.technici4n.fasttransferlib.api.Simulation;

public class ResourceMovement {
	public static <K extends ResourceKey> long moveRange(Predicate<K> filter, ResourceIo<K> from, ResourceIo<K> to, long maxAmount, int startSlot, int endSlot) {
		if (!from.supportsExtraction() || !to.supportsInsertion()) return 0;

		int totalMoved = 0;

		for (int i = startSlot; i < endSlot && maxAmount > 0; ++i) {
			K key = from.getResourceKey(i);
			if (!filter.test(key)) continue;

			// Try to extract the maximum count
			long moved = from.extract(i, key, maxAmount, Simulation.SIMULATE);
			if (moved <= 0) continue;

			// Try to insert the maximum count that can be extracted
			moved -= to.insert(key, moved, Simulation.SIMULATE);
			if (moved <= 0) continue;

			// Try to extract again with the maximum count for the insertable, and return if that doesn't match
			if (from.extract(i, key, moved, Simulation.SIMULATE) != moved) continue;

			// Move the items at last
			if (from.extract(i, key, moved, Simulation.ACT) != moved) {
				// TODO: throw
			}

			if (to.insert(key, moved, Simulation.ACT) != 0) {
				// TODO: throw
			}

			totalMoved += moved;
			maxAmount -= moved;
		}

		return totalMoved;
	}

	public static <K extends ResourceKey> long moveRange(ResourceIo<K> from, ResourceIo<K> to, long maxAmount, int startSlot, int endSlot) {
		return moveRange(key -> true, from, to, maxAmount, startSlot, endSlot);
	}

	public static <K extends ResourceKey> long moveMultiple(Predicate<K> filter, ResourceIo<K> from, ResourceIo<K> to, long maxAmount) {
		return moveRange(filter, from, to, maxAmount, 0, from.getSlotCount());
	}

	public static <K extends ResourceKey> long moveMultiple(ResourceIo<K> from, ResourceIo<K> to, long maxAmount) {
		// the lambda should be cached because it is stateless
		return moveMultiple(key -> true, from, to, maxAmount);
	}

	private ResourceMovement() {
	}
}
