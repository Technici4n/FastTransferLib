package dev.technici4n.fasttransferlib.impl.fluid.compat.vanilla;

import dev.technici4n.fasttransferlib.api.ItemInteractionContext;
import dev.technici4n.fasttransferlib.api.Simulation;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class PlayerEntityItemInteractionContext implements ItemInteractionContext {
	private final PlayerEntity player;
	private final Hand hand;

	public PlayerEntityItemInteractionContext(PlayerEntity player, Hand hand) {
		this.player = player;
		this.hand = hand;
	}

	@Override
	public boolean setStack(ItemStack stack, Simulation simulation) {
		if (simulation.isActing()) player.setStackInHand(hand, stack);
		return true;
	}

	@Override
	public boolean addExtraStacks(ItemStack stacks, Simulation simulation) {
		player.inventory.offerOrDrop(player.world, stacks);
		return true;
	}
}
