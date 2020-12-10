package dev.technici4n.fasttransferlib.impl.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import dev.technici4n.fasttransferlib.api.ItemInteractionContext;
import dev.technici4n.fasttransferlib.api.Simulation;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityItemInteractionContext extends LivingEntity implements ItemInteractionContext {
	PlayerEntityItemInteractionContext(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Shadow
	PlayerInventory inventory;

	@Override
	public boolean addStack(ItemStack stack, Simulation simulation) {
		if (simulation.isActing()) {
			if (this.getStackInHand(Hand.MAIN_HAND).isEmpty()) {
				this.setStackInHand(Hand.MAIN_HAND, stack);
			} else {
				this.inventory.offerOrDrop(world, stack);
			}
		}

		return true;
	}
}
