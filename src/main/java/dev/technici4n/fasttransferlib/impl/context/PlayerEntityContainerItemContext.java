package dev.technici4n.fasttransferlib.impl.context;

import java.util.function.Consumer;
import java.util.function.Supplier;

import dev.technici4n.fasttransferlib.api.ContainerItemContext;
import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.item.ItemKey;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class PlayerEntityContainerItemContext implements ContainerItemContext {
	private final PlayerEntity player;
	private final ItemKey itemKey;
	private final Supplier<ItemStack> stackGetter;
	private final Consumer<ItemStack> stackSetter;

	public static PlayerEntityContainerItemContext ofCursor(PlayerEntity player) {
		return new PlayerEntityContainerItemContext(player, player.inventory::getCursorStack, player.inventory::setCursorStack);
	}

	public static PlayerEntityContainerItemContext ofHand(PlayerEntity player, Hand hand) {
		return new PlayerEntityContainerItemContext(player, () -> player.getStackInHand(hand), stack -> player.setStackInHand(hand, stack));
	}

	private PlayerEntityContainerItemContext(PlayerEntity player, Supplier<ItemStack> stackGetter, Consumer<ItemStack> stackSetter) {
		this.player = player;
		this.itemKey = ItemKey.of(stackGetter.get());
		this.stackGetter = stackGetter;
		this.stackSetter = stackSetter;
	}

	@Override
	public boolean transform(int count, ItemKey into, Simulation simulation) {
		if (count <= 0) throw new RuntimeException("Must transform at least 1 item!");
		if (getCount() < count) throw new RuntimeException("Not enough items to transform!");

		if (simulation.isActing()) {
			ItemStack stack = stackGetter.get();
			stack.decrement(count);

			if (!into.isEmpty()) {
				while (count > 0) {
					ItemStack targetStack = stackGetter.get();

					if (into.matches(targetStack) && targetStack.getCount() < targetStack.getMaxCount()) {
						int inserted = Math.min(count, targetStack.getMaxCount() - targetStack.getCount());
						count -= inserted;
						targetStack.increment(inserted);
					} else {
						ItemStack newStack = into.toStack(Math.min(count, into.getItem().getMaxCount()));
						count -= newStack.getCount();

						if (targetStack.isEmpty()) {
							stackSetter.accept(newStack);
						} else {
							player.inventory.offerOrDrop(player.world, newStack);
						}
					}
				}
			}
		}

		return true;
	}

	@Override
	public int getCount() {
		ItemStack cursorItemStack = stackGetter.get();
		return itemKey.matches(cursorItemStack) ? cursorItemStack.getCount() : 0;
	}
}
