package dev.technici4n.fasttransferlib.impl.fluid.compat.vanilla;

import dev.technici4n.fasttransferlib.api.ItemInteractionContext;
import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.fluid.FluidApi;
import dev.technici4n.fasttransferlib.api.fluid.FluidConstants;
import dev.technici4n.fasttransferlib.api.fluid.FluidExtractable;
import dev.technici4n.fasttransferlib.api.fluid.FluidInsertable;

import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;

public class VanillaCompat {
	public static void load() {
		// initialize static
	}

	static {
		FluidApi.SIDED_VIEW.registerForBlocks((world, pos, state, direction) -> new CauldronWrapper(world, pos),
				Blocks.CAULDRON);
		FluidApi.UNSIDED_VIEW.registerForBlocks((world, pos, state, direction) -> new CauldronWrapper(world, pos),
				Blocks.CAULDRON);
		FluidApi.ITEM_VIEW.register(BottleCompat::new, Items.POTION, Items.GLASS_BOTTLE);
	}

	private static class BottleCompat implements FluidExtractable, FluidInsertable {
		ItemStack stack;
		final ItemInteractionContext context;

		private BottleCompat(ItemStack stack, ItemInteractionContext context) {
			this.stack = stack;
			this.context = context;
		}

		@Override
		public int getFluidSlotCount() {
			return 1;
		}

		@Override
		public Fluid getFluid(int slot) {
			return PotionUtil.getPotion(stack) == Potions.WATER ? Fluids.WATER : Fluids.EMPTY;
		}

		@Override
		public long getFluidAmount(int slot) {
			return PotionUtil.getPotion(stack) == Potions.WATER ? FluidConstants.BOTTLE : 0;
		}

		@Override
		public long insert(Fluid fluid, long amount, Simulation simulation) {
			if (PotionUtil.getPotion(stack) != Potions.EMPTY) return amount;
			if (amount < FluidConstants.BOTTLE) return amount;
			if (fluid != Fluids.WATER) return amount;
			if (!context.setStack(new ItemStack(Items.POTION), simulation)) return amount;
			if (simulation.isActing()) stack = new ItemStack(Items.POTION);
			return amount - FluidConstants.BOTTLE;
		}

		@Override
		public long extract(int slot, Fluid fluid, long maxAmount, Simulation simulation) {
			if (PotionUtil.getPotion(stack) != Potions.WATER) return 0;
			if (maxAmount < FluidConstants.BOTTLE) return 0;
			if (!context.setStack(new ItemStack(Items.GLASS_BOTTLE), simulation)) return 0;
			if (simulation.isActing()) stack = new ItemStack(Items.GLASS_BOTTLE);
			return FluidConstants.BOTTLE;
		}
	}
}
