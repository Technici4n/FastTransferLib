package dev.technici4n.fasttransferlib.impl.item.compat.lba;

import alexiil.mc.lib.attributes.item.FixedItemInv;
import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.item.ItemExtractable;
import dev.technici4n.fasttransferlib.api.item.ItemInsertable;
import dev.technici4n.fasttransferlib.api.item.ItemKey;
import dev.technici4n.fasttransferlib.impl.compat.LbaUtil;

class LbaWrappedFixedInv implements ItemInsertable, ItemExtractable {
	private final FixedItemInv wrapped;

	LbaWrappedFixedInv(FixedItemInv wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public int extract(int slot, ItemKey key, int maxCount, Simulation simulation) {
		return wrapped.getSlot(slot).attemptExtraction(key::matches, maxCount, LbaUtil.getSimulation(simulation)).getCount();
	}

	@Override
	public int insert(ItemKey item, int count, Simulation simulation) {
		return wrapped.getInsertable().attemptInsertion(item.toStack(count), LbaUtil.getSimulation(simulation)).getCount();
	}

	@Override
	public int getItemSlotCount() {
		return wrapped.getSlotCount();
	}

	@Override
	public ItemKey getItemKey(int slot) {
		return ItemKey.of(wrapped.getInvStack(slot));
	}

	@Override
	public int getItemCount(int slot) {
		return wrapped.getInvStack(slot).getCount();
	}
}
