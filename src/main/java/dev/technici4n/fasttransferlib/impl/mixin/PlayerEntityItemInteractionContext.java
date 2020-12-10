package dev.technici4n.fasttransferlib.impl.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import dev.technici4n.fasttransferlib.api.ItemInteractionContext;
import dev.technici4n.fasttransferlib.impl.fluid.compat.vanilla.PlayerEntityItemInteractionContextProvider;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityItemInteractionContext extends LivingEntity implements PlayerEntityItemInteractionContextProvider {
	PlayerEntityItemInteractionContext(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Shadow
	PlayerInventory inventory;

	@Unique
	private ItemInteractionContext mainhandItemInteractionContext = (stack, simulation) -> {
		if (simulation.isActing()) {
			if (getStackInHand(Hand.MAIN_HAND).isEmpty()) {
				setStackInHand(Hand.MAIN_HAND, stack);
			} else {
				inventory.offerOrDrop(world, stack);
			}
		}

		return true;
	};

	@Unique
	private ItemInteractionContext offhandItemInteractionContext = (stack, simulation) -> {
		if (simulation.isActing()) {
			if (getStackInHand(Hand.OFF_HAND).isEmpty()) {
				setStackInHand(Hand.OFF_HAND, stack);
			} else {
				inventory.offerOrDrop(world, stack);
			}
		}

		return true;
	};

	@Unique
	private ItemInteractionContext cursorItemInteractionContext = (stack, simulation) -> {
		if (simulation.isActing()) {
			if (inventory.getCursorStack().isEmpty()) {
				inventory.setCursorStack(stack);
			} else {
				inventory.offerOrDrop(world, stack);
			}
		}

		return true;
	};

	@Override
	public ItemInteractionContext getItemItemInteractionContext(Hand hand) {
		return hand == Hand.MAIN_HAND ? mainhandItemInteractionContext : offhandItemInteractionContext;
	}

	@Override
	public ItemInteractionContext getCursorItemItemInteractionContext() {
		return cursorItemInteractionContext;
	}
}
