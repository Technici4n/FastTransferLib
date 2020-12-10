package dev.technici4n.fasttransferlib.impl.fluid.compat.vanilla;

import dev.technici4n.fasttransferlib.api.ItemInteractionContext;
import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.fluid.FluidApi;
import dev.technici4n.fasttransferlib.api.fluid.FluidConstants;
import dev.technici4n.fasttransferlib.api.fluid.FluidIo;

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
		FluidApi.SIDED.registerForBlocks((world, pos, state, direction) -> new CauldronWrapper(world, pos),
				Blocks.CAULDRON);
		FluidApi.UNSIDED.registerForBlocks((world, pos, state, direction) -> new CauldronWrapper(world, pos),
				Blocks.CAULDRON);
		FluidApi.ITEM.register(BottleCompat::new, Items.POTION, Items.GLASS_BOTTLE);
	}

	private static class BottleCompat implements FluidIo {
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
		public boolean supportsFluidInsertion() {
			return true;
		}

		@Override
		public long insert(Fluid fluid, long amount, Simulation simulation) {
			if (stack.isEmpty()) return amount;
			if (PotionUtil.getPotion(stack) != Potions.EMPTY) return amount;
			if (amount < FluidConstants.BOTTLE) return amount;
			if (fluid != Fluids.WATER) return amount;

			if (simulation.isActing()) {
				stack.decrement(1);
				context.addStack(new ItemStack(Items.POTION), Simulation.ACT);
			} else if (stack.getCount() > 1 && !context.addStack(new ItemStack(Items.POTION), Simulation.SIMULATE)) {
				return amount;
			}

			return amount - FluidConstants.BOTTLE;
		}

		@Override
		public boolean supportsFluidExtraction() {
			return true;
		}

		@Override
		public long extract(int slot, Fluid fluid, long maxAmount, Simulation simulation) {
			if (stack.isEmpty()) return 0;
			if (PotionUtil.getPotion(stack) != Potions.WATER) return 0;
			if (maxAmount < FluidConstants.BOTTLE) return 0;
			if (simulation.isActing()) stack.decrement(1);
			if (!context.addStack(new ItemStack(Items.GLASS_BOTTLE), simulation)) return 0;
			return FluidConstants.BOTTLE;
		}
	}
}
