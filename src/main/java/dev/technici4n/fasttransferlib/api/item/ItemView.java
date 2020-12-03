package dev.technici4n.fasttransferlib.api.item;

import dev.technici4n.fasttransferlib.impl.item.ItemImpl;

/**
 * A view of an item inventory.
 *
 * @see ItemInsertable
 * @see ItemExtractable
 */
public interface ItemView {
	/**
	 * Return the number of slots in the view.
	 */
	int getItemSlotCount();

	/**
	 * Return the item key stored in a slot, or {@link ItemKey#EMPTY} if there is no item.
	 *
	 * @param slot The slot id, must be between 0 and {@link ItemView#getItemSlotCount()}.
	 * @return the item key stored in the slot, or {@link ItemKey#EMPTY} if there is no item.
	 * @throws IndexOutOfBoundsException if the slot is not in the range [0, {@link ItemView#getItemSlotCount()}).
	 */
	ItemKey getItemKey(int slot);

	/**
	 * Return the number of items stored in a slot, or 0 if there is no item.
	 *
	 * @param slot The slot id, must be between 0 and {@link ItemView#getItemSlotCount()}.
	 * @return the number of items stored in the slot, or 0 if there is no item.
	 * @throws IndexOutOfBoundsException if the slot is not in the range [0, {@link ItemView#getItemSlotCount()}).
	 */
	int getItemCount(int slot);

	/**
	 * Return the version of this inventory. If this number is the same for two calls, it is expected
	 * that the underlying inventory hasn't changed. There is however no guarantee that the inventory has changed if this number has changed.
	 */
	default int getVersion() {
		return ItemImpl.version++;
	}
}
