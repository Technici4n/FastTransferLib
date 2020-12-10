package dev.technici4n.fasttransferlib.api;

import dev.technici4n.fasttransferlib.impl.fluid.compat.vanilla.PlayerEntityItemInteractionContextProvider;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

/**
 * A context for interaction with item-provided item and fluid apis, adding new stacks.
 *
 * <p>In many cases such as bucket filling/emptying, it is necessary to add stacks other than the current stack.
 * For example, filling a bottle that is in a stack requires putting the water bottle in the inventory.
 */
public interface ItemInteractionContext {
	/**
	 * Add a stack if possible and return whether the modification was successful.
	 *
	 * @param stack     The extra stacks
	 * @param simulation If {@link Simulation#SIMULATE}, do not mutate anything
	 * @return whether the modification was successful
	 * @apiNote If a simulation succeeds twice, it is not guaranteed that the action will succeed twice, so it is recommended to only call this function once.
	 */
	boolean addStack(ItemStack stack, Simulation simulation);

	static ItemInteractionContext ofPlayerHand(PlayerEntity player, Hand hand) {
		return ((PlayerEntityItemInteractionContextProvider) player).getHandContext(hand);
	}

	static ItemInteractionContext ofCursor(PlayerEntity player) {
		return ((PlayerEntityItemInteractionContextProvider) player).getCursorContext();
	}
}
