package dev.technici4n.fasttransferlib.impl.context;

import dev.technici4n.fasttransferlib.api.ContainerItemContext;
import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.item.ItemKey;

import net.minecraft.item.ItemStack;

public class StackContainerItemContext implements ContainerItemContext {
	private final ItemKey key;
	private final ItemStack stack;

	public StackContainerItemContext(ItemStack stack) {
		this.key = ItemKey.of(stack);
		this.stack = stack;
	}

	@Override
	public int getCount() {
		if (key.isEmpty() || !key.matches(stack)) {
			return 0;
		} else {
			return stack.getCount();
		}
	}

	@Override
	public boolean transform(int count, ItemKey into, Simulation simulation) {
		if (count <= 0) throw new RuntimeException("Must transform at least 1 item!");
		if (getCount() < count) throw new RuntimeException("Not enough items to transform!");

		if (stack.getCount() == count && into.getItem() == stack.getItem()) {
			if (simulation.isActing()) {
				stack.setTag(into.copyTag());
			}

			return true;
		}

		return false;
	}
}
