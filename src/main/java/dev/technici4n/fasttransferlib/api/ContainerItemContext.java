package dev.technici4n.fasttransferlib.api;

import dev.technici4n.fasttransferlib.api.item.ItemKey;
import dev.technici4n.fasttransferlib.api.item.ItemKeyApiLookup;
import dev.technici4n.fasttransferlib.impl.context.PlayerEntityContainerItemContexts;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

/**
 * A context for interaction with item-provided item and fluid apis, bound to a specific ItemKey that must match that
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
	 * Transform one of the bound items into another item key.
	 * @param into The target item key.
	 * @param simulation If {@link Simulation#SIMULATE}, do not mutate the inventory
	 * @return whether the transformation was successful
	 * @throws RuntimeException If there is no item to replace, that is if {@link ContainerItemContext#getCount getCount} would return 0.
	 */
	boolean transform(ItemKey into, Simulation simulation);

	static ContainerItemContext ofPlayerHand(PlayerEntity player, Hand hand) {
		return new PlayerEntityContainerItemContexts.Hand(player, hand);
	}

	static ContainerItemContext ofPlayerCursor(PlayerEntity player) {
		return new PlayerEntityContainerItemContexts.Cursor(player);
	}
}
