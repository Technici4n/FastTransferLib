package dev.technici4n.fasttransferlib.impl.context;

import dev.technici4n.fasttransferlib.api.ContainerItemContext;
import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.item.ItemKey;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class PlayerEntityContainerItemContexts {
	private PlayerEntityContainerItemContexts() { }

	public static class Cursor implements ContainerItemContext {
		final PlayerEntity player;
		final ItemKey itemKey;

		public Cursor(PlayerEntity player) {
			this.player = player;
			this.itemKey = ItemKey.of(player.inventory.getCursorStack());
		}

		@Override
		public boolean transform(ItemKey into, Simulation simulation) {
			if (simulation.isActing()) {
				ItemStack cursorItemStack = player.inventory.getCursorStack();
				cursorItemStack.decrement(1);

				if (cursorItemStack.isEmpty()) {
					player.inventory.setCursorStack(into.toStack(1));
				} else {
					if (into.equals(ItemKey.of(cursorItemStack))) {
						int total = cursorItemStack.getCount() + 1;
						int firstStack = Math.min(total, into.getItem().getMaxCount());
						cursorItemStack.setCount(firstStack);
						int secondStack = total - firstStack;

						if (secondStack > 0) {
							player.inventory.offerOrDrop(player.world, into.toStack(secondStack));
						}
					} else {
						player.inventory.offerOrDrop(player.world, into.toStack(1));
					}
				}
			}

			return true;
		}

		@Override
		public int getCount() {
			ItemStack cursorItemStack = player.inventory.getCursorStack();
			return itemKey.equals(ItemKey.of(cursorItemStack)) ? cursorItemStack.getCount() : 0;
		}
	}

	public static class Hand implements ContainerItemContext {
		final PlayerEntity player;
		final ItemKey itemKey;
		final net.minecraft.util.Hand hand;

		public Hand(PlayerEntity player, net.minecraft.util.Hand hand) {
			this.player = player;
			this.itemKey = ItemKey.of(player.getMainHandStack());
			this.hand = hand;
		}

		@Override
		public boolean transform(ItemKey into, Simulation simulation) {
			if (simulation.isActing()) {
				ItemStack handItemStack = player.getStackInHand(hand);
				handItemStack.decrement(1);

				if (handItemStack.isEmpty()) {
					player.setStackInHand(hand, into.toStack(1));
				} else {
					if (into.equals(ItemKey.of(handItemStack))) {
						int total = handItemStack.getCount() + 1;
						int firstStack = Math.min(total, into.getItem().getMaxCount());
						handItemStack.setCount(firstStack);
						int secondStack = total - firstStack;

						if (secondStack > 0) {
							player.inventory.offerOrDrop(player.world, into.toStack(secondStack));
						}
					} else {
						player.inventory.offerOrDrop(player.world, into.toStack(1));
					}
				}
			}

			return true;
		}

		@Override
		public int getCount() {
			ItemStack cursorItemStack = player.inventory.getCursorStack();
			return itemKey.equals(ItemKey.of(cursorItemStack)) ? cursorItemStack.getCount() : 0;
		}
	}
}
