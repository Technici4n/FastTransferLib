package dev.technici4n.fasttransferlib.impl.fluid.compat.vanilla;

import dev.technici4n.fasttransferlib.api.ContainerItemContext;
import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.fluid.FluidApi;
import dev.technici4n.fasttransferlib.api.fluid.FluidConstants;
import dev.technici4n.fasttransferlib.api.fluid.FluidIo;
import dev.technici4n.fasttransferlib.api.item.ItemKey;
import dev.technici4n.fasttransferlib.impl.mixin.BucketItemAccess;

import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.FishBucketItem;
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
		FluidApi.ITEM.registerFallback((itemKey, context) -> {
			if (!(itemKey.getItem() instanceof BucketItem)) return null;
			if (itemKey.getItem() instanceof FishBucketItem) return null;
			return new BucketCompat(itemKey, context);
		});
	}

	private static class BucketCompat implements FluidIo {
		final ItemKey itemKey;
		final ContainerItemContext context;

		private BucketCompat(ItemKey itemKey, ContainerItemContext context) {
			this.itemKey = itemKey;
			this.context = context;
		}

		@Override
		public int getFluidSlotCount() {
			return 1;
		}

		@Override
		public Fluid getFluid(int slot) {
			if (slot != 0) throw new IllegalArgumentException("Only 1 Slot In This Item");
			if (!(itemKey.getItem() instanceof BucketItem)) return Fluids.EMPTY;
			return ((BucketItemAccess) itemKey.getItem()).getFluid();
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
			if (context.getCount() == 0) return amount;
			if (!(itemKey.getItem() instanceof BucketItem)) return amount;
			if (getFluid(0) != Fluids.EMPTY) return amount;
			if (amount < FluidConstants.BUCKET) return amount;
			if (!context.transform(ItemKey.of(fluid.getBucketItem()), simulation)) return amount;
			return amount - FluidConstants.BUCKET;
		}

		@Override
		public boolean supportsFluidExtraction() {
			return true;
		}

		@Override
		public long extract(int slot, Fluid fluid, long maxAmount, Simulation simulation) {
			if (slot != 0) throw new IllegalArgumentException("Only 1 Slot In This Item");
			if (context.getCount() == 0) return 0;
			if (getFluid(0) == Fluids.EMPTY || getFluid(0) != fluid) return 0;
			if (!context.transform(ItemKey.of(Items.BUCKET), simulation)) return 0;
			return FluidConstants.BUCKET;
		}
	}

	private static class BottleCompat implements FluidIo {
		final ItemKey itemKey;
		final ContainerItemContext context;

		private BottleCompat(ItemKey itemKey, ContainerItemContext context) {
			this.itemKey = itemKey;
			this.context = context;
		}

		@Override
		public int getFluidSlotCount() {
			return 1;
		}

		@Override
		public Fluid getFluid(int slot) {
			if (slot != 0) throw new IllegalArgumentException("Only 1 Slot In This Item");
			return PotionUtil.getPotion(itemKey.toStack()) == Potions.WATER ? Fluids.WATER : Fluids.EMPTY;
		}

		@Override
		public long getFluidAmount(int slot) {
			if (slot != 0) throw new IllegalArgumentException("Only 1 Slot In This Item");
			return PotionUtil.getPotion(itemKey.toStack()) == Potions.WATER ? FluidConstants.BOTTLE : 0;
		}

		@Override
		public boolean supportsFluidInsertion() {
			return true;
		}

		@Override
		public long insert(Fluid fluid, long amount, Simulation simulation) {
			if (context.getCount() == 0) return amount;
			if (PotionUtil.getPotion(itemKey.toStack()) != Potions.EMPTY) return amount;
			if (amount < FluidConstants.BOTTLE) return amount;
			if (fluid != Fluids.WATER) return amount;
			if (!context.transform(ItemKey.of(Items.POTION), simulation)) return amount;
			return amount - FluidConstants.BOTTLE;
		}

		@Override
		public boolean supportsFluidExtraction() {
			return true;
		}

		@Override
		public long extract(int slot, Fluid fluid, long maxAmount, Simulation simulation) {
			if (slot != 0) throw new IllegalArgumentException("Only 1 Slot In This Item");
			if (context.getCount() == 0) return 0;
			if (PotionUtil.getPotion(itemKey.toStack()) != Potions.WATER) return 0;
			if (maxAmount < FluidConstants.BOTTLE) return 0;
			if (!context.transform(ItemKey.of(Items.GLASS_BOTTLE), simulation)) return 0;
			return FluidConstants.BOTTLE;
		}
	}
}
