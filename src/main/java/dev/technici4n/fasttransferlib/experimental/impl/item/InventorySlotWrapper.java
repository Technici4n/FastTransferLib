package dev.technici4n.fasttransferlib.experimental.impl.item;

import dev.technici4n.fasttransferlib.experimental.api.item.ItemKey;
import dev.technici4n.fasttransferlib.experimental.api.item.ItemPreconditions;
import dev.technici4n.fasttransferlib.experimental.api.storage.SingleSlotStorage;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;

// A wrapper around a single slot of an inventory
// We must ensure that only one instance of this class exists for every inventory slot,
// or the transaction logic will not work correct.
class InventorySlotWrapper extends SnapshotParticipant<ItemStack> implements SingleSlotStorage<ItemKey> {
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
	public long insert(ItemKey key, long maxAmount, Transaction transaction) {
		// TODO: clean this up
		ItemPreconditions.notEmpty(key);
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
	public long extract(ItemKey key, long maxAmount, Transaction transaction) {
		ItemPreconditions.notEmpty(key);
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
	public ItemKey resource() {
		return ItemKey.of(inventory.getStack(slot));
	}

	@Override
	public boolean isEmpty() {
		return inventory.getStack(slot).isEmpty();
	}

	@Override
	public long amount() {
		return inventory.getStack(slot).getCount();
	}

	@Override
	public long capacity() {
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
