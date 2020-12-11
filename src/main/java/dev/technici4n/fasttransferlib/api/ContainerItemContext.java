package dev.technici4n.fasttransferlib.api;

import dev.technici4n.fasttransferlib.api.item.ItemKey;
import dev.technici4n.fasttransferlib.impl.context.PlayerEntityContainerItemContexts;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

/**
 * A context for interaction with item-provided item and fluid apis, adding new stacks.
 *
 * <p>In many cases such as bucket filling/emptying, it is necessary to add stacks other than the current stack.
 * For example, filling a bottle that is in a stack requires putting the water bottle in the inventory.
 */
public interface ContainerItemContext {
	int getCount(); // must check for the ItemKey and return 0 if the ItemKey is not valid anymore
	boolean transform(ItemKey into, Simulation simulation); // transform one of the stack into the target key

	static ContainerItemContext ofPlayerHand(PlayerEntity player, Hand hand) {
		return new PlayerEntityContainerItemContexts.Hand(player, hand);
	}

	static ContainerItemContext ofCursor(PlayerEntity player) {
		return new PlayerEntityContainerItemContexts.Cursor(player);
	}
}
