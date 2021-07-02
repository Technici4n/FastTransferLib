package dev.technici4n.fasttransferlib.experimental.api.item;

import org.jetbrains.annotations.ApiStatus;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

/**
 * A wrapper around a PlayerInventory.
 *
 * <p>Do not implement. Obtain an instance through
 * {@link InventoryWrappers#ofPlayerInventory} instead.
 */
@ApiStatus.NonExtendable
public interface PlayerInventoryWrapper extends InventoryWrapper {
	/**
	 * Add items to the inventory if possible, and drop any leftover items in the
	 * world.
	 */
	void offerOrDrop(ItemVariant key, long amount, Transaction transaction);
}
