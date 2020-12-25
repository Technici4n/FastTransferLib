package dev.technici4n.fasttransferlib.impl.item.compat.vanilla;

import com.google.common.primitives.Ints;
import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.item.ItemApi;
import dev.technici4n.fasttransferlib.api.item.ItemKey;
import dev.technici4n.fasttransferlib.api.transfer.ResourceIo;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

/**
 * A wrapper around a vanilla Inventory, for {@link ItemApi#SIDED}.
 */
public class InventorySidedView implements ResourceIo<ItemKey> {
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
	public boolean supportsExtraction() {
		return true;
	}

	@Override
	public long extract(int slot, ItemKey key, long maxCount, Simulation simulation) {
		checkBounds(slot);
		ItemStack stack = wrapped.getStack(slots != null ? slots[slot] : slot);

		if (wrappedSided != null && !wrappedSided.canExtract(slot, stack, direction)) return 0;
		if (!key.matches(stack)) return 0;

		int extracted = Math.min(Ints.saturatedCast(maxCount), stack.getCount());

		if (simulation.isActing()) {
			stack.decrement(extracted);
			wrapped.markDirty();
		}

		return extracted;
	}

	@Override
	public boolean supportsInsertion() {
		return true;
	}

	@Override
	public long insert(ItemKey key, long count, Simulation simulation) {
		ItemStack filter = key.toStack(1);

		if (wrappedSided == null) {
			for (int i = 0; i < wrapped.size() && count > 0; ++i) {
				if (wrapped.isValid(i, filter)) {
					count = insert(filter, key, count, simulation, i);
				}
			}
		} else {
			for (int slot : slots) {
				if (wrapped.isValid(slot, filter) && wrappedSided.canExtract(slot, filter, direction)) {
					long leftover = insert(filter, key, count, simulation, slot);

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
	private long insert(ItemStack filter, ItemKey key, long count, Simulation simulation, int targetSlot) {
		ItemStack target = wrapped.getStack(targetSlot);
		int maxCount = Math.min(wrapped.getMaxCountPerStack(), filter.getMaxCount());

		if (target.isEmpty()) {
			int inserted = (int) Math.min(count, maxCount);

			if (simulation.isActing()) {
				wrapped.setStack(targetSlot, key.toStack(inserted));
				wrapped.markDirty();
			}

			return count - inserted;
		} else if (key.matches(target)) {
			int inserted = (int) Math.min(count, maxCount - target.getCount());

			if (simulation.isActing()) {
				target.increment(inserted);
				wrapped.markDirty();
			}

			return count - inserted;
		} else {
			return count;
		}
	}

	@Override
	public int getSlotCount() {
		return size;
	}

	@Override
	public ItemKey getResourceKey(int slot) {
		checkBounds(slot);
		return ItemKey.of(wrapped.getStack(slot));
	}

	@Override
	public long getAmount(int slot) {
		checkBounds(slot);
		return wrapped.getStack(slot).getCount();
	}

	private void checkBounds(int slot) {
		if (slot < 0 || slot >= size) {
			throw new IndexOutOfBoundsException();
		}
	}
}
