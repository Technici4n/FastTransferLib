package dev.technici4n.fasttransferlib.impl.item.compat.vanilla;

import dev.technici4n.fasttransferlib.api.item.ItemApi;
import dev.technici4n.fasttransferlib.api.item.ItemIo;
import org.jetbrains.annotations.NotNull;

import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;

public class VanillaCompat {
	public static void init() {
		// initialize static
	}

	static {
		BlockApiLookup.BlockEntityApiProvider<ItemIo, @NotNull Direction> inventoryProvider = (blockEntity, direction) -> {
			if (blockEntity instanceof Inventory) {
				return new InventorySidedView((Inventory) blockEntity, direction);
			} else {
				return null;
			}
		};

		// Vanilla containers, for optimal performance
		ItemApi.SIDED.registerForBlockEntities(inventoryProvider,
				BlockEntityType.DISPENSER, BlockEntityType.DROPPER, BlockEntityType.FURNACE, BlockEntityType.BLAST_FURNACE,
				BlockEntityType.SMOKER, BlockEntityType.BARREL, BlockEntityType.BREWING_STAND, BlockEntityType.HOPPER,
				BlockEntityType.SHULKER_BOX);
		ItemApi.SIDED.registerForBlocks((world, pos, state, direction) -> {
			Inventory inv = ChestBlock.getInventory((ChestBlock) state.getBlock(), state, world, pos, true);
			return inv == null ? null : new InventorySidedView(inv, direction);
		}, Blocks.CHEST, Blocks.TRAPPED_CHEST);

		// Fallback for vanilla interfaces
		ItemApi.SIDED.registerFallback((world, pos, state, be, direction) -> inventoryProvider.get(be, direction));
		ItemApi.SIDED.registerFallback((world, pos, state, be, direction) -> {
			if (state.getBlock() instanceof InventoryProvider) {
				Inventory inv = ((InventoryProvider) state.getBlock()).getInventory(state, world, pos);

				if (inv != null) return new InventorySidedView(inv, direction);
			}

			return null;
		});
	}
}
