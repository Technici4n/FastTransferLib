package dev.technici4n.fasttransferlib.api;

import dev.technici4n.fasttransferlib.api.item.ItemKey;
import dev.technici4n.fasttransferlib.api.item.ItemKeyApiLookup;
import dev.technici4n.fasttransferlib.impl.context.PlayerEntityContainerItemContext;
import dev.technici4n.fasttransferlib.impl.context.StackContainerItemContext;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

/**
 * A context for interaction with item-provided apis, bound to a specific ItemKey that must match that
 * provided to {@link ItemKeyApiLookup#get}.
 *
 * <p>In many cases such as bucket filling/emptying, it is necessary to add stacks other than the current stack.
 * For example, filling a bottle that is in a stack requires putting the water bottle in the inventory.
 */
public interface ContainerItemContext {
	/**
	 * Get the current count. If the ItemKey is not present anymore, return 0 instead.
	 */
	int getCount();

	/**
	 * Transform some of the bound items into another item key.
	 * @param count How much to transform, must be positive.
	 * @param into The target item key.
	 * @param simulation If {@link Simulation#SIMULATE}, do not mutate the underlying object
	 * @return whether the transformation was successful
	 * @throws RuntimeException If the passed count is zero or negative.
	 * @throws RuntimeException If there aren't enough items to replace, that is if {@link ContainerItemContext#getCount this.getCount()} < count.
	 */
	boolean transform(int count, ItemKey into, Simulation simulation);

	static ContainerItemContext ofPlayerHand(PlayerEntity player, Hand hand) {
		return PlayerEntityContainerItemContext.ofHand(player, hand);
	}

	static ContainerItemContext ofPlayerCursor(PlayerEntity player) {
		return PlayerEntityContainerItemContext.ofCursor(player);
	}

	/**
	 * A context that will only with a stack, mutating it if necessary.
	 */
	static ContainerItemContext ofStack(ItemStack stack) {
		return new StackContainerItemContext(stack);
	}
}
