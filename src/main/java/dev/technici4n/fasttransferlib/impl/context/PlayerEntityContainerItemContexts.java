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
			if (getCount() <= 0) throw new RuntimeException("No item to transform!");

			if (simulation.isActing()) {
				ItemStack cursorItemStack = player.inventory.getCursorStack();
				cursorItemStack.decrement(1);

				if (cursorItemStack.isEmpty()) {
					player.inventory.setCursorStack(into.toStack(1));
				} else if (into.matches(cursorItemStack) && cursorItemStack.getCount() < cursorItemStack.getMaxCount()) {
					cursorItemStack.increment(1);
				} else {
					player.inventory.offerOrDrop(player.world, into.toStack());
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
			this.itemKey = ItemKey.of(player.getStackInHand(hand));
			this.hand = hand;
		}

		@Override
		public boolean transform(ItemKey into, Simulation simulation) {
			if (getCount() <= 0) throw new RuntimeException("No item to transform!");

			if (simulation.isActing()) {
				ItemStack handStack = player.getStackInHand(hand);
				handStack.decrement(1);

				if (handStack.isEmpty()) {
					player.setStackInHand(hand, into.toStack(1));
				} else if (into.matches(handStack) && handStack.getCount() < handStack.getMaxCount()) {
					handStack.increment(1);
				} else {
					player.inventory.offerOrDrop(player.world, into.toStack());
				}
			}

			return true;
		}

		@Override
		public int getCount() {
			ItemStack handItemStack = player.getStackInHand(hand);
			return itemKey.matches(handItemStack) ? handItemStack.getCount() : 0;
		}
	}
}
