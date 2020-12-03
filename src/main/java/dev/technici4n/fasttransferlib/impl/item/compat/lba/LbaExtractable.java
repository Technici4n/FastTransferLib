package dev.technici4n.fasttransferlib.impl.item.compat.lba;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import dev.technici4n.fasttransferlib.api.item.ItemExtractable;
import dev.technici4n.fasttransferlib.api.item.ItemKey;
import dev.technici4n.fasttransferlib.impl.compat.LbaUtil;

import net.minecraft.item.ItemStack;

class LbaExtractable implements alexiil.mc.lib.attributes.item.ItemExtractable {
	private final ItemExtractable extractable;

	LbaExtractable(ItemExtractable extractable) {
		this.extractable = extractable;
	}

	@Override
	public ItemStack attemptExtraction(ItemFilter filter, int maxCount, Simulation simulation) {
		for (int i = 0; i < extractable.getItemSlotCount(); ++i) {
			ItemKey key = extractable.getItemKey(i);
			ItemStack stack = key.toStack();
			if (!filter.matches(stack)) continue;

			int extracted = extractable.extract(i, extractable.getItemKey(i), maxCount, LbaUtil.getSimulation(simulation));

			if (extracted > 0) {
				stack.setCount(extracted);
				return stack;
			}
		}

		return ItemStack.EMPTY;
	}
}
