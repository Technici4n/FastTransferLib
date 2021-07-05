package dev.technici4n.fasttransferlib.experimental.impl.item;

import dev.technici4n.fasttransferlib.experimental.api.item.ItemVariant;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;

// A wrapper around a single slot of an inventory
// We must ensure that only one instance of this class exists for every inventory slot,
// or the transaction logic will not work correct.
class InventorySlotWrapper extends SnapshotParticipant<ItemStack> implements SingleSlotStorage<ItemVariant> {
	final Inventory inventory;
	final int slot;

	InventorySlotWrapper(Inventory inventory, int slot) {
		this.inventory = inventory;
		this.slot = slot;
	}

	@Override
	public boolean supportsInsertion() {
		return true;
	}

	@Override
	public long insert(ItemVariant key, long maxAmount, TransactionContext transaction) {
		// TODO: clean this up
		StoragePreconditions.notBlank(key);
		int count = (int) Math.min(maxAmount, inventory.getMaxCountPerStack());
		ItemStack stack = inventory.getStack(slot);

		if (stack.isEmpty()) {
			ItemStack keyStack = key.toStack(count);

			if (inventory.isValid(slot, keyStack)) {
				int inserted = Math.min(keyStack.getMaxCount(), count);
				this.updateSnapshots(transaction);
				keyStack.setCount(inserted);
				inventory.setStack(slot, keyStack);
				return inserted;
			}
		} else if (key.matches(stack)) {
			int inserted = Math.min(stack.getMaxCount() - stack.getCount(), count);
			this.updateSnapshots(transaction);
			stack.increment(inserted);
			return inserted;
		}

		return 0;
	}

	@Override
	public boolean supportsExtraction() {
		return true;
	}

	@Override
	public long extract(ItemVariant key, long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notBlank(key);
		ItemStack stack = inventory.getStack(slot);

		if (key.matches(stack)) {
			int extracted = (int) Math.min(stack.getCount(), maxAmount);
			this.updateSnapshots(transaction);
			stack.decrement(extracted);
			return extracted;
		}

		return 0;
	}

	@Override
	public ItemVariant getResource() {
		return ItemVariant.of(inventory.getStack(slot));
	}

	@Override
	public boolean isResourceBlank() {
		return inventory.getStack(slot).isEmpty();
	}

	@Override
	public long getAmount() {
		return inventory.getStack(slot).getCount();
	}

	@Override
	public long getCapacity() {
		return inventory.getStack(slot).getMaxCount();
	}

	@Override
	protected ItemStack createSnapshot() {
		return inventory.getStack(slot).copy();
	}

	@Override
	protected void readSnapshot(ItemStack snapshot) {
		inventory.setStack(slot, snapshot);
	}

	@Override
	public void onFinalCommit() {
		inventory.markDirty();
	}
}
