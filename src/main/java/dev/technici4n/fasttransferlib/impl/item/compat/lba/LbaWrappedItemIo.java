package dev.technici4n.fasttransferlib.impl.item.compat.lba;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.FixedItemInvView;
import alexiil.mc.lib.attributes.item.ItemTransferable;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import com.google.common.primitives.Ints;
import dev.technici4n.fasttransferlib.api.item.ItemKey;
import dev.technici4n.fasttransferlib.api.transfer.ResourceIo;
import dev.technici4n.fasttransferlib.impl.compat.LbaUtil;

import net.minecraft.item.ItemStack;

class LbaWrappedItemIo implements FixedItemInvView, ItemTransferable {
	private final ResourceIo<ItemKey> io;

	LbaWrappedItemIo(ResourceIo<ItemKey> io) {
		this.io = io;
	}

	@Override
	public int getSlotCount() {
		return io.getSlotCount();
	}

	@Override
	public ItemStack getInvStack(int slot) {
		return io.getResourceKey(slot).toStack(Ints.saturatedCast(io.getAmount(slot)));
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return true; // let's say it is?
	}

	@Override
	public ItemStack attemptExtraction(ItemFilter filter, int maxCount, Simulation simulation) {
		for (int i = 0; i < io.getSlotCount(); ++i) {
			ItemKey key = io.getResourceKey(i);
			ItemStack stack = key.toStack();
			if (!filter.matches(stack)) continue;

			int extracted = (int) io.extract(i, io.getResourceKey(i), maxCount, LbaUtil.getSimulation(simulation));

			if (extracted > 0) {
				stack.setCount(extracted);
				return stack;
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack attemptInsertion(ItemStack stack, Simulation simulation) {
		int leftover = (int) io.insert(ItemKey.of(stack), stack.getCount(), LbaUtil.getSimulation(simulation));
		ItemStack ret = stack.copy();
		ret.setCount(leftover);
		return ret;
	}
}
