package dev.technici4n.fasttransferlib.impl.fluid.compat.vanilla;

import dev.technici4n.fasttransferlib.api.ItemInteractionContext;

import net.minecraft.util.Hand;

public interface PlayerEntityItemInteractionContextProvider {
	ItemInteractionContext getItemItemInteractionContext(Hand hand);
	ItemInteractionContext getCursorItemItemInteractionContext();
}
