package dev.technici4n.fasttransferlib.api.item;

import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.impl.item.ItemImpl;

/**
 * An item inventory.
 */
public interface ItemIo {
	/**
	 * Returns the items contained in this, no duplicate fluids or empty items should be returned. The return value should not be mutated by the caller or this.
	 * @return
	 */
	ItemKey[] getItemKeys();

	/**
	 * Returns the amount of each fluid gotten by getFluids. The return value should not be mutated by the caller or this.
	 */
	int[] getItemCounts();

	/**
	 * Return the version of this inventory. If this number is the same for two calls, it is expected
	 * that the underlying inventory hasn't changed. There is however no guarantee that the inventory has changed if this number has changed.
	 */
	default int getVersion() {
		return ItemImpl.version++;
	}

	/**
	 * Return false if this object does not support insertion at all, meaning that insertion will always return the passed amount,
	 * and insert-only pipes should not connect.
	 */
	default boolean supportsItemInsertion() {
		return false;
	}

	/**
	 * Insert items into this inventory, and return the number of leftover items.
	 * Distribution is left entirely to the implementor.
	 *
	 * <p>If simulation is {@link Simulation#SIMULATE}, the result of the operation must be returned, but the underlying state of the item insertable must not change.
	 *
	 * @param key        The ItemKey to insert
	 * @param count      The number of items to insert
	 * @param simulation If {@link Simulation#SIMULATE}, do not mutate the insertable
	 * @return the number of items that could not be inserted
	 */
	default int insert(ItemKey key, int count, Simulation simulation) {
		return count;
	}

	/**
	 * Return false if this object does not support extraction at all, meaning that extraction will always return 0,
	 * and extract-only pipes should not connect.
	 */
	default boolean supportsItemExtraction() {
		return false;
	}

	/**
	 * Extract some items from this extractable, matching the passed item key.
	 *
	 * <p>If simulation is {@link Simulation#SIMULATE}, the result of the operation must be returned, but the underlying state of the item extractable must not change.
	 *
	 * @param key        The filter for the items to extract
	 * @param maxCount   the number of items to extract at most.
	 * @param simulation If {@link Simulation#SIMULATE}, do not mutate the insertable
	 * @return The extracted stack
	 * @implNote Implementations are encouraged to override this method with a more performant implementation.
	 */
	default int extract(ItemKey key, int maxCount, Simulation simulation) {
		return 0;
	}
}
