package dev.technici4n.fasttransferlib.experimental.api.item;

import java.util.Objects;

import dev.technici4n.fasttransferlib.experimental.impl.item.CursorSlotWrapper;
import dev.technici4n.fasttransferlib.experimental.impl.item.InventoryWrappersImpl;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;

/**
 * Wraps {@link Inventory} and {@link PlayerInventory} as {@link Storage}
 * implementations.
 */
public final class InventoryWrappers {
	/**
	 * Return a wrapper around an {@link Inventory} or a {@link SidedInventory}.
	 *
	 * <p>Note: If the inventory is a {@link PlayerInventory}, this function will
	 * return a wrapper around all the slots of the player inventory except the
	 * cursor stack. This may cause insertion to insert arbitrary items into
	 * equipment slots or other unexpected behavior. To prevent this,
	 * {@link PlayerInventoryWrapper}'s specialized functions should be used
	 * instead.
	 *
	 * @param inventory The inventory to wrap.
	 * @param direction The direction to use if the access is sided, or {@code null}
	 *                  if the access is not sided.
	 */
	// TODO: should we throw if we receive a PlayerInventory? (it's probably a
	// mistake)
	public static InventoryWrapper of(Inventory inventory, @Nullable Direction direction) {
		Objects.requireNonNull(inventory, "Null inventory is not supported.");
		return InventoryWrappersImpl.of(inventory, direction);
	}

	/**
	 * Return a wrapper around the inventory of a player.
	 *
	 * @see PlayerInventoryWrapper
	 */
	public static PlayerInventoryWrapper ofPlayer(PlayerEntity player) {
		Objects.requireNonNull(player, "Null player is not supported.");
		return ofPlayerInventory(player.getInventory());
	}

	/**
	 * Return a wrapper around the inventory of a player.
	 *
	 * @see PlayerInventoryWrapper
	 */
	public static PlayerInventoryWrapper ofPlayerInventory(PlayerInventory playerInventory) {
		return (PlayerInventoryWrapper) of(playerInventory, null);
	}

	/**
	 * Return a wrapper around the cursor slot of a screen handler.
	 */
	public static SingleSlotStorage<ItemVariant> ofCursor(ScreenHandler screenHandler) {
		return CursorSlotWrapper.get(screenHandler);
	}

	private InventoryWrappers() {
	}
}
