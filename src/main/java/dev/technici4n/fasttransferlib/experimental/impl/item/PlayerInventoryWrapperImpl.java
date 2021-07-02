package dev.technici4n.fasttransferlib.experimental.impl.item;

import java.util.ArrayList;
import java.util.List;

import dev.technici4n.fasttransferlib.experimental.api.item.ItemVariant;
import dev.technici4n.fasttransferlib.experimental.api.item.ItemPreconditions;
import dev.technici4n.fasttransferlib.experimental.api.item.PlayerInventoryWrapper;

import net.minecraft.entity.player.PlayerInventory;

import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;

// A wrapper around a PlayerInventory with the additional functions in PlayerInventoryWrapper.
class PlayerInventoryWrapperImpl extends CombinedStorage<ItemVariant, InventorySlotWrapper> implements PlayerInventoryWrapper {
	private final PlayerInventory playerInventory;
	private final DroppedStacks droppedStacks;

	PlayerInventoryWrapperImpl(List<InventorySlotWrapper> slots, PlayerInventory playerInventory) {
		super(slots);
		this.playerInventory = playerInventory;
		this.droppedStacks = new DroppedStacks();
	}

	@Override
	public void offerOrDrop(ItemVariant resource, long amount, Transaction tx) {
		ItemPreconditions.notEmptyNotNegative(resource, amount);

		for (int iteration = 0; iteration < 2; iteration++) {
			boolean allowEmptySlots = iteration == 1;

			for (InventorySlotWrapper slot : parts) {
				if (!slot.inventory.getStack(slot.slot).isEmpty() || allowEmptySlots) {
					amount -= slot.insert(resource, amount, tx);
				}
			}
		}

		// Drop leftover in the world on the server side (will be synced by the game
		// with the client).
		// Dropping items is server-side only because it involves randomness.
		if (amount > 0 && playerInventory.player.world.isClient()) {
			droppedStacks.addDrop(resource, amount, tx);
		}
	}

	@Override
	public SingleSlotStorage<ItemVariant> getSlot(int slot) {
		return parts.get(slot);
	}

	private class DroppedStacks extends SnapshotParticipant<Integer> {
		final List<ItemVariant> droppedKeys = new ArrayList<>();
		final List<Long> droppedCounts = new ArrayList<>();

		void addDrop(ItemVariant key, long count, Transaction transaction) {
			updateSnapshots(transaction);
			droppedKeys.add(key);
			droppedCounts.add(count);
		}

		@Override
		protected Integer createSnapshot() {
			return droppedKeys.size();
		}

		@Override
		protected void readSnapshot(Integer snapshot) {
			// effectively cancel dropping the stacks
			int previousSize = snapshot;

			while (droppedKeys.size() > previousSize) {
				droppedKeys.remove(droppedKeys.size() - 1);
				droppedCounts.remove(droppedCounts.size() - 1);
			}
		}

		@Override
		protected void onFinalCommit() {
			// drop the stacks and mark dirty
			for (int i = 0; i < droppedKeys.size(); ++i) {
				ItemVariant key = droppedKeys.get(i);

				while (droppedCounts.get(i) > 0) {
					int dropped = (int) Math.min(key.getItem().getMaxCount(), droppedCounts.get(i));
					playerInventory.player.dropStack(key.toStack(dropped));
					droppedCounts.set(i, droppedCounts.get(i) - dropped);
				}
			}

			droppedKeys.clear();
			droppedCounts.clear();
		}
	}
}
