package dev.technici4n.fasttransferlib.api.item;

import java.util.function.Predicate;

import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.impl.FtlImpl;

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
			// Check the item key
			ItemKey key = from.getItemKey(i);
			if (key.isEmpty() || !filter.test(key)) continue;

			// Try to extract the maximum count
			int moved = from.extract(i, key, maxCount, Simulation.SIMULATE);
			if (moved <= 0) continue;

			// Try to insert the maximum count that can be extracted
			moved -= to.insert(key, moved, Simulation.SIMULATE);
			if (moved <= 0) continue;

			// Try to extract again with the maximum count for the insertable, and return if that doesn't match
			if (from.extract(i, key, moved, Simulation.SIMULATE) != moved) continue;

			// Move the item, extraction part
			int extracted = from.extract(i, key, moved, Simulation.ACT);

			// This extraction should never fail, as it was simulated just before being acted.
			// If it fails nonetheless, throw an exception.
			if (extracted != moved) {
				String errorMessage = String.format(
						"Bad ItemIo implementation: %s.\n"
								+ "Extraction of %d items was simulated, but only %d items could really be extracted.\n"
								+ "Slot: %d, item key: %s",
						from, moved, extracted, i, key.toString()
				);
				throw new AssertionError(errorMessage);
			}

			// Move the item, insertion part
			int leftover = to.insert(key, moved, Simulation.ACT);

			// This insertion can fail in some edge cases. If that happens, we try to re-insert in the source.
			// If that fails, we simply void the items that could not be inserted, and we print a warning in the console.
			if (leftover != 0) {
				long reinsertLeftover = from.insert(key, leftover, Simulation.ACT);

				if (reinsertLeftover != 0) {
					String errorMessage = String.format(
							"Bad ItemIo interaction while moving from %s to %s.\n"
									+ "Insertion of %d items was simulated, but only %d items could really be inserted.\n"
									+ "%d items could be re-inserted, and %d items were voided.\n"
									+ "Item key: %s",
							from, to, moved, moved - leftover, moved - reinsertLeftover, reinsertLeftover, key.toString()
					);
					FtlImpl.LOGGER.warn(errorMessage);
				}
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
