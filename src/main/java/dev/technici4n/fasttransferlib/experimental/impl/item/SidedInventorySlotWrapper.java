package dev.technici4n.fasttransferlib.experimental.impl.item;

import java.util.Iterator;

import dev.technici4n.fasttransferlib.experimental.api.item.ItemVariant;

import net.minecraft.inventory.SidedInventory;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

// Wraps an InventorySlotWrapper with SidedInventory#canInsert and SidedInventory#canExtract checks for a given direction.
class SidedInventorySlotWrapper implements Storage<ItemVariant> {
	private final InventorySlotWrapper slotWrapper;
	private final SidedInventory sidedInventory; // TODO: should we just cast slotWrapper.inventory instead?
	private final Direction direction;

	SidedInventorySlotWrapper(InventorySlotWrapper slotWrapper, SidedInventory sidedInventory, Direction direction) {
		this.slotWrapper = slotWrapper;
		this.sidedInventory = sidedInventory;
		this.direction = direction;
	}

	@Override
	public boolean supportsInsertion() {
		return true;
	}

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		if (!sidedInventory.canInsert(slotWrapper.slot, resource.toStack(), direction)) {
			return 0;
		} else {
			return slotWrapper.insert(resource, maxAmount, transaction);
		}
	}

	@Override
	public boolean supportsExtraction() {
		return true;
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		if (!sidedInventory.canExtract(slotWrapper.slot, resource.toStack(), direction)) {
			return 0;
		} else {
			return slotWrapper.insert(resource, maxAmount, transaction);
		}
	}

	@Override
	public Iterator<StorageView<ItemVariant>> iterator(TransactionContext transaction) {
		return slotWrapper.iterator(transaction);
	}
}
