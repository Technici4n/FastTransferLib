package dev.technici4n.fasttransferlib.experimental.api.item;

import net.minecraft.block.Block;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;

public final class ItemStorage {
	public static final BlockApiLookup<Storage<ItemKey>, Direction> SIDED = BlockApiLookup.get(new Identifier("ftl:sided_item_api"),
			Storage.asClass(), Direction.class);

	private ItemStorage() {
	}

	static {
		// Load generic vanilla api fallback
		ItemStorage.SIDED.registerFallback((world, pos, state, blockEntity, direction) -> {
			Block block = state.getBlock();
			Inventory inventory = null;

			if (block instanceof InventoryProvider) {
				inventory = ((InventoryProvider) block).getInventory(state, world, pos);
			} else if (blockEntity instanceof Inventory) {
				inventory = (Inventory) blockEntity;

				if (blockEntity instanceof ChestBlockEntity && block instanceof ChestBlock) {
					inventory = ChestBlock.getInventory((ChestBlock) block, state, world, pos, true);
				}
			}

			return inventory == null ? null : InventoryWrappers.of(inventory, direction);
		});
	}
}
