package dev.technici4n.fasttransferlib.experimental.impl.context;

import com.google.common.base.Preconditions;
import dev.technici4n.fasttransferlib.experimental.api.context.ContainerItemContext;
import dev.technici4n.fasttransferlib.experimental.api.item.InventoryWrappers;
import dev.technici4n.fasttransferlib.experimental.api.item.ItemVariant;
import dev.technici4n.fasttransferlib.experimental.api.item.PlayerInventoryWrapper;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Hand;

import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public class PlayerEntityContainerItemContext implements ContainerItemContext {
	private final ItemVariant boundKey;
	private final SingleSlotStorage<ItemVariant> slot;
	private final PlayerInventoryWrapper wrapper;

	public static ContainerItemContext ofHand(PlayerEntity player, Hand hand) {
		PlayerInventoryWrapper wrapper = InventoryWrappers.ofPlayerInventory(player.getInventory());
		int slot = hand == Hand.MAIN_HAND ? player.getInventory().selectedSlot : PlayerInventory.OFF_HAND_SLOT;
		return new PlayerEntityContainerItemContext(wrapper.getSlot(slot), wrapper);
	}

	public static ContainerItemContext ofCursor(PlayerEntity player, ScreenHandler screenHandler) {
		PlayerInventoryWrapper wrapper = InventoryWrappers.ofPlayerInventory(player.getInventory());
		return new PlayerEntityContainerItemContext(InventoryWrappers.ofCursor(screenHandler), wrapper);
	}

	private PlayerEntityContainerItemContext(SingleSlotStorage<ItemVariant> slot, PlayerInventoryWrapper wrapper) {
		this.boundKey = slot.getResource();
		this.slot = slot;
		this.wrapper = wrapper;
	}

	@Override
	public ItemVariant getBoundKey() {
		return boundKey;
	}

	@Override
	public long getCount(TransactionContext tx) {
		return slot.getResource().equals(boundKey) ? slot.getAmount() : 0;
	}

	@Override
	public boolean transform(long count, ItemVariant into, TransactionContext tx) {
		Preconditions.checkArgument(count <= getCount(tx), "Can't transform items that are not available.");

		if (slot.extract(boundKey, count, tx) != count) {
			throw new AssertionError("Implementation error.");
		}

		if (!into.isBlank()) {
			count -= slot.insert(into, count, tx);
			wrapper.offerOrDrop(into, count, tx);
		}

		return true;
	}
}
