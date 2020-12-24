package dev.technici4n.fasttransferlib.impl.item.compat.lba;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.FixedItemInvView;
import alexiil.mc.lib.attributes.item.ItemTransferable;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import dev.technici4n.fasttransferlib.api.item.ItemIo;
import dev.technici4n.fasttransferlib.api.item.ItemKey;
import dev.technici4n.fasttransferlib.impl.compat.LbaUtil;

import net.minecraft.item.ItemStack;

class LbaWrappedItemIo implements FixedItemInvView, ItemTransferable {
	private final ItemIo io;

	LbaWrappedItemIo(ItemIo io) {
		this.io = io;
	}

	@Override
	public int getSlotCount() {
		return 25; //TODO: This is stupid and won't work great with ItemIo's that have >25 items in it
	}

	@Override
	public ItemStack getInvStack(int slot) {
		ItemKey[] itemKeys = io.getItemKeys();
		int[] itemCounts = io.getItemCounts();
		return slot < itemKeys.length ? itemKeys[slot].toStack(itemCounts[slot]) : ItemStack.EMPTY;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return true; // let's say it is?
	}

	@Override
	public ItemStack attemptExtraction(ItemFilter filter, int maxCount, Simulation simulation) {
		ItemKey[] itemKeys = io.getItemKeys();

		for (int i = 0; i < itemKeys.length; ++i) {
			ItemKey key = itemKeys[i];
			ItemStack stack = key.toStack();
			if (!filter.matches(stack)) continue;

			int extracted = io.extract(itemKeys[i], maxCount, LbaUtil.getSimulation(simulation));

			if (extracted > 0) {
				stack.setCount(extracted);
				return stack;
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack attemptInsertion(ItemStack stack, Simulation simulation) {
		int leftover = io.insert(ItemKey.of(stack), stack.getCount(), LbaUtil.getSimulation(simulation));
		ItemStack ret = stack.copy();
		ret.setCount(leftover);
		return ret;
	}
}
