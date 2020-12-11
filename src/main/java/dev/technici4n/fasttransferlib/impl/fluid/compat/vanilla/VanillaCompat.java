package dev.technici4n.fasttransferlib.impl.fluid.compat.vanilla;

import dev.technici4n.fasttransferlib.api.ItemInteractionContext;
import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.fluid.FluidApi;
import dev.technici4n.fasttransferlib.api.fluid.FluidConstants;
import dev.technici4n.fasttransferlib.api.fluid.FluidIo;
import dev.technici4n.fasttransferlib.impl.mixin.BucketItemAccess;

import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.FishBucketItem;
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
		FluidApi.ITEM.registerFallback((stack, context) -> {
			if (!(stack.getItem() instanceof BucketItem)) return null;
			if (stack.getItem() instanceof FishBucketItem) return null;
			return new BucketCompat(stack, context);
		});
	}

	private static class BucketCompat implements FluidIo {
		ItemStack stack;
		final ItemInteractionContext context;

		private BucketCompat(ItemStack stack, ItemInteractionContext context) {
			this.stack = stack;
			this.context = context;
		}

		@Override
		public int getFluidSlotCount() {
			return 1;
		}

		@Override
		public Fluid getFluid(int slot) {
			if (slot != 0) throw new IllegalArgumentException("Only 1 Slot In This Item");
			if (!(stack.getItem() instanceof BucketItem)) return Fluids.EMPTY;
			return ((BucketItemAccess) stack.getItem()).getFluid();
		}

		@Override
		public long getFluidAmount(int slot) {
			if (slot != 0) throw new IllegalArgumentException("Only 1 Slot In This Item");
			return getFluid(0) == Fluids.EMPTY ? 0 : FluidConstants.BUCKET;
		}

		@Override
		public boolean supportsFluidInsertion() {
			return true;
		}

		@Override
		public long insert(Fluid fluid, long amount, Simulation simulation) {
			if (!(stack.getItem() instanceof BucketItem)) return amount;
			if (getFluid(0) != Fluids.EMPTY) return amount;
			if (amount < FluidConstants.BUCKET) return amount;
			if (!context.addStack(new ItemStack(fluid.getBucketItem()), Simulation.SIMULATE)) return amount;

			if (simulation.isActing()) {
				stack.decrement(1);
				context.addStack(new ItemStack(fluid.getBucketItem()), Simulation.ACT);
			}

			return amount - FluidConstants.BUCKET;
		}

		@Override
		public boolean supportsFluidExtraction() {
			return true;
		}

		@Override
		public long extract(int slot, Fluid fluid, long maxAmount, Simulation simulation) {
			if (slot != 0) throw new IllegalArgumentException("Only 1 Slot In This Item");
			if (getFluid(0) == Fluids.EMPTY || getFluid(0) != fluid) return 0;
			if (!context.addStack(new ItemStack(Items.BUCKET), Simulation.SIMULATE)) return 0;

			if (simulation.isActing()) {
				stack.decrement(1);
				context.addStack(new ItemStack(Items.BUCKET), Simulation.ACT);
			}

			return FluidConstants.BUCKET;
		}
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
			if (slot != 0) throw new IllegalArgumentException("Only 1 Slot In This Item");
			return PotionUtil.getPotion(stack) == Potions.WATER ? Fluids.WATER : Fluids.EMPTY;
		}

		@Override
		public long getFluidAmount(int slot) {
			if (slot != 0) throw new IllegalArgumentException("Only 1 Slot In This Item");
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
			if (!context.addStack(new ItemStack(Items.POTION), Simulation.SIMULATE)) return amount;

			if (simulation.isActing()) {
				stack.decrement(1);
				context.addStack(new ItemStack(Items.POTION), Simulation.ACT);
			}

			return amount - FluidConstants.BOTTLE;
		}

		@Override
		public boolean supportsFluidExtraction() {
			return true;
		}

		@Override
		public long extract(int slot, Fluid fluid, long maxAmount, Simulation simulation) {
			if (slot != 0) throw new IllegalArgumentException("Only 1 Slot In This Item");
			if (stack.isEmpty()) return 0;
			if (PotionUtil.getPotion(stack) != Potions.WATER) return 0;
			if (maxAmount < FluidConstants.BOTTLE) return 0;
			if (!context.addStack(new ItemStack(Items.GLASS_BOTTLE), Simulation.SIMULATE)) return 0;

			if (simulation.isActing()) {
				stack.decrement(1);
				context.addStack(new ItemStack(Items.GLASS_BOTTLE), Simulation.ACT);
			}

			return FluidConstants.BOTTLE;
		}
	}
}
