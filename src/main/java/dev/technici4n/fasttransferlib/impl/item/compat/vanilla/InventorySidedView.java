package dev.technici4n.fasttransferlib.impl.item.compat.vanilla;

import java.util.ArrayList;

import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.item.ItemApi;
import dev.technici4n.fasttransferlib.api.item.ItemIo;
import dev.technici4n.fasttransferlib.api.item.ItemKey;
import it.unimi.dsi.fastutil.ints.IntArrayList;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

/**
 * A wrapper around a vanilla Inventory, for {@link ItemApi#SIDED}.
 */
public class InventorySidedView implements ItemIo {
	private final Inventory wrapped;
	private final SidedInventory wrappedSided;
	private final int[] slots;
	private final int size;
	private final Direction direction;

	public InventorySidedView(Inventory wrapped, Direction direction) {
		this.wrapped = wrapped;
		this.wrappedSided = wrapped instanceof SidedInventory ? (SidedInventory) wrapped : null;
		this.slots = wrappedSided != null ? wrappedSided.getAvailableSlots(direction) : null;
		this.size = slots == null ? wrapped.size() : slots.length;
		this.direction = direction;
	}

	@Override
	public boolean supportsItemExtraction() {
		return true;
	}

	public int extract(int slot, ItemKey key, int maxCount, Simulation simulation) {
		ItemStack stack = wrapped.getStack(slots != null ? slots[slot] : slot);

		if (wrappedSided != null && !wrappedSided.canExtract(slot, stack, direction)) return 0;
		if (!key.matches(stack)) return 0;

		int extracted = Math.min(maxCount, stack.getCount());

		if (simulation.isActing()) {
			stack.decrement(extracted);
			wrapped.markDirty();
		}

		return extracted;
	}

	@Override
	public int extract(ItemKey key, int maxCount, Simulation simulation) {
		for (int i = 0; i < getItemSlotCount(); ++i) {
			int extracted = extract(i, key, maxCount, simulation);

			if (extracted > 0) {
				return extracted;
			}
		}

		return 0;
	}

	@Override
	public boolean supportsItemInsertion() {
		return true;
	}

	@Override
	public int insert(ItemKey key, int count, Simulation simulation) {
		ItemStack filter = key.toStack(count);

		if (wrappedSided == null) {
			for (int i = 0; i < wrapped.size() && count > 0; ++i) {
				if (wrapped.isValid(i, filter)) {
					count = insert(filter, key, count, simulation, i);
				}
			}
		} else {
			for (int slot : slots) {
				if (wrapped.isValid(slot, filter) && wrappedSided.canExtract(slot, filter, direction)) {
					int leftover = insert(filter, key, count, simulation, slot);

					// only allow a single insertion for sided inventories, because some slots may appear multiple times
					if (leftover < count) {
						return leftover;
					}
				}
			}
		}

		return count;
	}

	// internal insert, doesn't check for isValid or canInsert
	private int insert(ItemStack filter, ItemKey key, int count, Simulation simulation, int targetSlot) {
		ItemStack target = wrapped.getStack(targetSlot);
		int maxCount = Math.min(wrapped.getMaxCountPerStack(), filter.getMaxCount());

		if (target.isEmpty()) {
			int inserted = Math.min(count, maxCount);

			if (simulation.isActing()) {
				wrapped.setStack(targetSlot, key.toStack(inserted));
				wrapped.markDirty();
			}

			return count - inserted;
		} else if (key.matches(target)) {
			int inserted = Math.min(count, maxCount - target.getCount());

			if (simulation.isActing()) {
				target.increment(inserted);
				wrapped.markDirty();
			}

			return count - inserted;
		} else {
			return count;
		}
	}

	public int getItemSlotCount() {
		return size;
	}

	@Override
	public ItemKey[] getItemKeys() {
		ArrayList<ItemKey> result = new ArrayList<>();

		for (int i = 0; i < getItemSlotCount(); ++i) {
			if (wrapped.getStack(i).isEmpty()) continue;
			ItemKey key = ItemKey.of(wrapped.getStack(i));
			if (!result.contains(key)) result.add(key);
		}

		return result.toArray(ItemKey[]::new);
	}

	@Override
	public int[] getItemCounts() {
		ArrayList<ItemKey> itemKeys = new ArrayList<>();
		IntArrayList result = new IntArrayList();

		for (int i = 0; i < getItemSlotCount(); ++i) {
			if (wrapped.getStack(i).isEmpty()) continue;
			ItemKey key = ItemKey.of(wrapped.getStack(i));
			int index = itemKeys.indexOf(key);

			if (index == -1) {
				itemKeys.add(key);
				result.add(wrapped.getStack(i).getCount());
			} else {
				result.set(index, result.getInt(index) + wrapped.getStack(i).getCount());
			}
		}

		return result.toIntArray();
	}
}
