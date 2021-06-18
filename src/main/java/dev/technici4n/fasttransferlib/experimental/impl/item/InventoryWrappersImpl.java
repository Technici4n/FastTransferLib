package dev.technici4n.fasttransferlib.experimental.impl.item;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import dev.technici4n.fasttransferlib.experimental.api.item.ItemKey;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;

public class InventoryWrappersImpl {
	// List<Storage<ItemKey>> has 7 values.
	// The 6 first for the various directions, and the last element for a null
	// direction.
	private static final WeakHashMap<Inventory, List<Storage<ItemKey>>> WRAPPERS = new WeakHashMap<>();

	public static Storage<ItemKey> of(Inventory inventory, @Nullable Direction direction) {
		List<Storage<ItemKey>> storages = WRAPPERS.computeIfAbsent(inventory, InventoryWrappersImpl::buildWrappers);
		return direction != null ? storages.get(direction.ordinal()) : storages.get(6);
	}

	private static List<Storage<ItemKey>> buildWrappers(Inventory inventory) {
		List<Storage<ItemKey>> result = new ArrayList<>(7); // 6 directions + null

		// wrapper around the whole inventory
		List<InventorySlotWrapper> slots = IntStream.range(0, inventory.size()).mapToObj(i -> new InventorySlotWrapper(inventory, i))
				.collect(Collectors.toList());
		Storage<ItemKey> fullWrapper = inventory instanceof PlayerInventory ? new PlayerInventoryWrapperImpl(slots, (PlayerInventory) inventory)
				: new CombinedStorage<>(slots);

		if (inventory instanceof SidedInventory) {
			// sided logic, only use the slots returned by SidedInventory#getAvailableSlots,
			// and check canInsert/canExtract
			SidedInventory sidedInventory = (SidedInventory) inventory;

			for (Direction direction : Direction.values()) {
				List<SidedInventorySlotWrapper> sideSlots = IntStream.of(sidedInventory.getAvailableSlots(direction))
						.mapToObj(slot -> new SidedInventorySlotWrapper(slots.get(slot), sidedInventory, direction)).collect(Collectors.toList());
				result.add(new CombinedStorage<>(sideSlots));
			}
		} else {
			// unsided logic, just use the same Storage 7 times
			for (int i = 0; i < 6; ++i) { // 6 directions
				result.add(fullWrapper);
			}
		}

		result.add(fullWrapper);
		return result;
	}
}
