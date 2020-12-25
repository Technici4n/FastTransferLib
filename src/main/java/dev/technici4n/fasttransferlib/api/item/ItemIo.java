package dev.technici4n.fasttransferlib.api.item;

import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.impl.item.ItemImpl;

/**
 * An item inventory.
 */
public interface ItemIo {
	/**
	 * Return the number of slots in the view.
	 */
	int getItemSlotCount();

	/**
	 * Return the item key stored in a slot, or {@link ItemKey#EMPTY} if there is no item.
	 *
	 * @param slot The slot id, must be between 0 and {@link ItemIo#getItemSlotCount()}.
	 * @return the item key stored in the slot, or {@link ItemKey#EMPTY} if there is no item.
	 * @throws IndexOutOfBoundsException if the slot is not in the range [0, {@link ItemIo#getItemSlotCount()}).
	 */
	ItemKey getItemKey(int slot);

	/**
	 * Return the number of items stored in a slot, or 0 if there is no item.
	 *
	 * @param slot The slot id, must be between 0 and {@link ItemIo#getItemSlotCount()}.
	 * @return the number of items stored in the slot, or 0 if there is no item.
	 * @throws IndexOutOfBoundsException if the slot is not in the range [0, {@link ItemIo#getItemSlotCount()}).
	 */
	int getItemCount(int slot);

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
	 * Extract some items from this extractable, with the same semantics as {@link ItemIo#extract(ItemKey, int, Simulation) the slotless variant}.
	 * The slot parameter, as long as it is in range, can be anything.
	 * It is however expected that calling this in a loop will be faster for callers that need to move a lot of items, with the following snippet for example:
	 * <pre>{@code
	 * for(int i = 0; i < extractable.getSlotCount(); i++) {
	 *     ItemStack extractedStack = extractable.extract(i, extractable.getStack(i), Simulation.ACT);
	 *     // use the extracted slot
	 * }
	 * }</pre>
	 *
	 * @param slot       The slot id, must be between 0 and {@link ItemIo#getItemSlotCount()}.
	 * @param key        The filter for the items to extract
	 * @param maxCount   the number of items to extract at most.
	 * @param simulation If {@link Simulation#SIMULATE}, do not mutate the insertable
	 * @return The extracted stack
	 */
	default int extract(int slot, ItemKey key, int maxCount, Simulation simulation) {
		return 0;
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
		if (!supportsItemExtraction()) return 0;

		for (int i = 0; i < getItemSlotCount(); ++i) {
			int extracted = extract(i, key, maxCount, simulation);

			if (extracted > 0) {
				return extracted;
			}
		}

		return 0;
	}
}
