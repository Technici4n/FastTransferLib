package dev.technici4n.fasttransferlib.api.item;

import dev.technici4n.fasttransferlib.api.Simulation;

/**
 * An item inventory that supports extracting items.
 *
 * @see ItemInsertable
 */
public interface ItemExtractable extends ItemView {
	/**
	 * Extract some items from this extractable, with the same semantics as {@link ItemExtractable#extract(ItemKey, int, Simulation) the slotless variant}.
	 * The slot parameter, as long as it is in range, can be anything.
	 * It is however expected that calling this in a loop will be faster for callers that need to move a lot of items, with the following snippet for example:
	 * <pre>{@code
	 * for(int i = 0; i < extractable.getSlotCount(); i++) {
	 *     ItemStack extractedStack = extractable.extract(i, extractable.getStack(i), Simulation.ACT);
	 *     // use the extracted slot
	 * }
	 * }</pre>
	 *
	 * @param slot       The slot id, must be between 0 and {@link ItemView#getItemSlotCount()}.
	 * @param key        The filter for the items to extract
	 * @param maxCount   the number of items to extract at most.
	 * @param simulation If {@link Simulation#SIMULATE}, do not mutate the insertable
	 * @return The extracted stack
	 */
	int extract(int slot, ItemKey key, int maxCount, Simulation simulation);

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
		for (int i = 0; i < getItemSlotCount(); ++i) {
			int extracted = extract(i, key, maxCount, simulation);

			if (extracted > 0) {
				return extracted;
			}
		}

		return 0;
	}
}
