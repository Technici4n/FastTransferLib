package dev.technici4n.fasttransferlib.api.item;

import java.util.function.Predicate;

import dev.technici4n.fasttransferlib.api.Simulation;

/**
 * Utilities related to moving items between two {@link ItemIo}'s.
 */
public final class ItemMovement {
	/**
	 * Move some items from some slots of an {@link ItemIo} to another {@link ItemIo}.
	 *
	 * @param filter    a predicate deciding which items may be moved
	 * @param from      the io to move items from
	 * @param to        the io to move the items to
	 * @param maxCount  The maximum number of items to move.
	 * @param startSlot The first slot of the range to move, inclusive.
	 * @param endSlot   The last slot of the range to move, exclusive.
	 * @return The number of items that were moved.
	 * @throws IndexOutOfBoundsException if the start or end slot is out of bounds of the source {@link ItemIo}'s slot count.
	 */
	public static int moveRange(Predicate<ItemKey> filter, ItemIo from, ItemIo to, int maxCount, int startSlot, int endSlot) {
		if (!from.supportsItemExtraction() || !to.supportsItemInsertion()) return 0;

		int totalMoved = 0;

		for (int i = startSlot; i < endSlot && maxCount > 0; ++i) {
			ItemKey key = from.getItemKey(i);
			if (!filter.test(key)) continue;

			// Try to extract the maximum count
			int moved = from.extract(i, key, maxCount, Simulation.SIMULATE);
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
			maxCount -= moved;
		}

		return totalMoved;
	}

	/**
	 * Move some items from some slots of an {@link ItemIo} to another {@link ItemIo}.
	 *
	 * @param from      the io to move items from
	 * @param to        the io to move the items to
	 * @param maxCount  The maximum number of items to move.
	 * @param startSlot The first slot of the range to move, inclusive.
	 * @param endSlot   The last slot of the range to move, exclusive.
	 * @return The number of items that were moved.
	 * @throws IndexOutOfBoundsException if the start or end slot is out of bounds of the source {@link ItemIo}'s slot count.
	 */
	public static int moveRange(ItemIo from, ItemIo to, int maxCount, int startSlot, int endSlot) {
		return moveRange(key -> true, from, to, maxCount, startSlot, endSlot);
	}

	/**
	 * Move some items from some slots of an {@link ItemIo} to another {@link ItemIo}.
	 *
	 * @param filter    a predicate deciding which items may be moved
	 * @param from      the io to move items from
	 * @param to        the io to move the items to
	 * @param maxCount The maximum number of items to move.
	 * @return The number of items that were moved.
	 */
	public static int moveMultiple(Predicate<ItemKey> filter, ItemIo from, ItemIo to, int maxCount) {
		return moveRange(filter, from, to, maxCount, 0, from.getItemSlotCount());
	}

	/**
	 * Move some items from an {@link ItemIo} to another {@link ItemIo}.
	 *
	 * @param from     the extractable to move items from
	 * @param to       the insertable to move the items to
	 * @param maxCount The maximum number of items to move.
	 * @return The number of items that were moved.
	 */
	public static int moveMultiple(ItemIo from, ItemIo to, int maxCount) {
		// the lambda should be cached because it is stateless
		return moveMultiple(key -> true, from, to, maxCount);
	}

	private ItemMovement() {
	}
}
