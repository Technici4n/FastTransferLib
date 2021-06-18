package dev.technici4n.fasttransferlib.experimental.api.item;

import dev.technici4n.fasttransferlib.experimental.api.storage.SingleSlotStorage;
import org.jetbrains.annotations.ApiStatus;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

/**
 * A wrapper around a PlayerInventory.
 *
 * <p>Do not implement. Obtain an instance through
 * {@link InventoryWrappers#ofPlayerInventory} instead.
 */
@ApiStatus.NonExtendable
public interface PlayerInventoryWrapper extends Storage<ItemKey> {
	/**
	 * Return a wrapper around a specific slot of the player inventory.
	 *
	 * <p>Slots 0 to 35 are for the main inventory, slots 36 to 39 are for the armor,
	 * and slot 40 is the offhand slot.
	 */
	SingleSlotStorage<ItemKey> slotWrapper(int index);

	/**
	 * Add items to the inventory if possible, and drop any leftover items in the
	 * world.
	 */
	void offerOrDrop(ItemKey key, long amount, Transaction transaction);
}
