package dev.technici4n.fasttransferlib.impl.item.compat.lba;

import java.util.ArrayList;

import alexiil.mc.lib.attributes.item.FixedItemInv;
import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.item.ItemIo;
import dev.technici4n.fasttransferlib.api.item.ItemKey;
import dev.technici4n.fasttransferlib.impl.compat.LbaUtil;
import it.unimi.dsi.fastutil.ints.IntArrayList;

class LbaWrappedFixedInv implements ItemIo {
	private final FixedItemInv wrapped;

	LbaWrappedFixedInv(FixedItemInv wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public boolean supportsItemExtraction() {
		return true;
	}

	@Override
	public int extract(ItemKey key, int maxCount, Simulation simulation) {
		int result = 0;

		for (int i = 0; i < getItemSlotCount(); ++i) {
			result += wrapped.getSlot(i).attemptExtraction(key::matches, maxCount, LbaUtil.getSimulation(simulation)).getCount();
		}

		return result;
	}

	@Override
	public boolean supportsItemInsertion() {
		return true;
	}

	@Override
	public int insert(ItemKey item, int count, Simulation simulation) {
		return wrapped.getInsertable().attemptInsertion(item.toStack(count), LbaUtil.getSimulation(simulation)).getCount();
	}

	public int getItemSlotCount() {
		return wrapped.getSlotCount();
	}

	public ItemKey getItemKey(int slot) {
		return ItemKey.of(wrapped.getInvStack(slot));
	}

	public int getItemCount(int slot) {
		return wrapped.getInvStack(slot).getCount();
	}

	@Override
	public ItemKey[] getItemKeys() {
		ArrayList<ItemKey> result = new ArrayList<>();

		for (int i = 0; i < getItemSlotCount(); ++i) {
			if (wrapped.getInvStack(i).isEmpty()) continue;
			ItemKey key = getItemKey(i);
			if (!result.contains(key)) result.add(key);
		}

		return result.toArray(ItemKey[]::new);
	}

	@Override
	public int[] getItemCounts() {
		ArrayList<ItemKey> itemKeys = new ArrayList<>();
		IntArrayList result = new IntArrayList();

		for (int i = 0; i < getItemSlotCount(); ++i) {
			if (wrapped.getInvStack(i).isEmpty()) continue;
			ItemKey key = getItemKey(i);
			int index = itemKeys.indexOf(key);

			if (index == -1) {
				itemKeys.add(key);
				result.add(getItemCount(i));
			} else {
				result.set(index, result.getInt(index) + getItemCount(i));
			}
		}

		return result.toIntArray();
	}
}
