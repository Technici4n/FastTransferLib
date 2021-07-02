package dev.technici4n.fasttransferlib.experimental.impl.item;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import dev.technici4n.fasttransferlib.experimental.api.item.InventoryWrapper;
import dev.technici4n.fasttransferlib.experimental.api.item.ItemVariant;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;

public class InventoryWrappersImpl {
	// List<Storage<ItemVariant>> has 7 values.
	// The 6 first for the various directions, and the last element for a null
	// direction.
	private static final WeakHashMap<Inventory, List<InventoryWrapper>> WRAPPERS = new WeakHashMap<>();

	public static InventoryWrapper of(Inventory inventory, @Nullable Direction direction) {
		List<InventoryWrapper> storages = WRAPPERS.computeIfAbsent(inventory, InventoryWrappersImpl::buildWrappers);
		return direction != null ? storages.get(direction.ordinal()) : storages.get(6);
	}

	private static List<InventoryWrapper> buildWrappers(Inventory inventory) {
		List<InventoryWrapper> result = new ArrayList<>(7); // 6 directions + null

		// wrapper around the whole inventory
		List<InventorySlotWrapper> slots = IntStream.range(0, inventory.size()).mapToObj(i -> new InventorySlotWrapper(inventory, i))
				.collect(Collectors.toList());
		InventoryWrapper fullWrapper = inventory instanceof PlayerInventory ? new PlayerInventoryWrapperImpl(slots, (PlayerInventory) inventory)
				: new InventoryWrapperImpl((List) slots);

		if (inventory instanceof SidedInventory sidedInventory) {
			// sided logic, only use the slots returned by SidedInventory#getAvailableSlots, and check canInsert/canExtract
			for (Direction direction : Direction.values()) {
				List<SidedInventorySlotWrapper> sideSlots = IntStream.of(sidedInventory.getAvailableSlots(direction))
						.mapToObj(slot -> new SidedInventorySlotWrapper(slots.get(slot), sidedInventory, direction)).toList();
				result.add(new InventoryWrapperImpl((List) sideSlots));
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

	private static class InventoryWrapperImpl extends CombinedStorage<ItemVariant, SingleSlotStorage<ItemVariant>> implements InventoryWrapper {
		InventoryWrapperImpl(List<SingleSlotStorage<ItemVariant>> parts) {
			super(parts);
		}

		@Override
		public SingleSlotStorage<ItemVariant> getSlot(int index) {
			return parts.get(index);
		}
	}
}
