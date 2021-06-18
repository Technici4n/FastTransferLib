package dev.technici4n.fasttransferlib.experimental.impl.context;

import com.google.common.base.Preconditions;
import dev.technici4n.fasttransferlib.experimental.api.context.ContainerItemContext;
import dev.technici4n.fasttransferlib.experimental.api.item.InventoryWrappers;
import dev.technici4n.fasttransferlib.experimental.api.item.ItemKey;
import dev.technici4n.fasttransferlib.experimental.api.item.PlayerInventoryWrapper;
import dev.technici4n.fasttransferlib.experimental.api.storage.SingleSlotStorage;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Hand;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

public class PlayerEntityContainerItemContext implements ContainerItemContext {
	private final ItemKey boundKey;
	private final Storage<ItemKey> slot;
	private final PlayerInventoryWrapper wrapper;

	public static ContainerItemContext ofHand(PlayerEntity player, Hand hand) {
		PlayerInventoryWrapper wrapper = InventoryWrappers.ofPlayerInventory(player.getInventory());
		int slot = hand == Hand.MAIN_HAND ? player.getInventory().selectedSlot : PlayerInventory.OFF_HAND_SLOT;
		return new PlayerEntityContainerItemContext(wrapper.slotWrapper(slot), wrapper);
	}

	public static ContainerItemContext ofCursor(PlayerEntity player, ScreenHandler screenHandler) {
		PlayerInventoryWrapper wrapper = InventoryWrappers.ofPlayerInventory(player.getInventory());
		return new PlayerEntityContainerItemContext(InventoryWrappers.ofCursor(screenHandler), wrapper);
	}

	private PlayerEntityContainerItemContext(SingleSlotStorage<ItemKey> slot, PlayerInventoryWrapper wrapper) {
		this.boundKey = slot.resource();
		this.slot = slot;
		this.wrapper = wrapper;
	}

	@Override
	public ItemKey getBoundKey() {
		return boundKey;
	}

	@Override
	public long getCount(Transaction tx) {
		long count = 0;

		for (StorageView<ItemKey> view : slot.iterable(tx)) {
			if (view.resource().equals(boundKey)) {
				count = view.amount();
			}
		}

		return count;
	}

	@Override
	public boolean transform(long count, ItemKey into, Transaction tx) {
		Preconditions.checkArgument(count <= getCount(tx), "Can't transform items that are not available.");

		if (slot.extract(boundKey, count, tx) != count) {
			throw new AssertionError("Implementation error.");
		}

		if (!into.isEmpty()) {
			count -= slot.insert(into, count, tx);
			wrapper.offerOrDrop(into, count, tx);
		}

		return true;
	}
}
