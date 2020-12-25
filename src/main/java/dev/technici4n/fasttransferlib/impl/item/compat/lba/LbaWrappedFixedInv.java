package dev.technici4n.fasttransferlib.impl.item.compat.lba;

import alexiil.mc.lib.attributes.item.FixedItemInv;
import com.google.common.primitives.Ints;
import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.item.ItemKey;
import dev.technici4n.fasttransferlib.api.transfer.ResourceIo;
import dev.technici4n.fasttransferlib.impl.compat.LbaUtil;

class LbaWrappedFixedInv implements ResourceIo<ItemKey> {
	private final FixedItemInv wrapped;

	LbaWrappedFixedInv(FixedItemInv wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public boolean supportsExtraction() {
		return true;
	}

	@Override
	public long extract(int slot, ItemKey key, long maxAmount, Simulation simulation) {
		return wrapped.getSlot(slot).attemptExtraction(key::matches, Ints.saturatedCast(maxAmount), LbaUtil.getSimulation(simulation)).getCount();
	}

	@Override
	public boolean supportsInsertion() {
		return true;
	}

	@Override
	public long insert(ItemKey item, long amount, Simulation simulation) {
		return wrapped.getInsertable().attemptInsertion(item.toStack(Ints.saturatedCast(amount)), LbaUtil.getSimulation(simulation)).getCount();
	}

	@Override
	public int getSlotCount() {
		return wrapped.getSlotCount();
	}

	@Override
	public ItemKey getResourceKey(int slot) {
		return ItemKey.of(wrapped.getInvStack(slot));
	}

	@Override
	public long getAmount(int slot) {
		return wrapped.getInvStack(slot).getCount();
	}
}
