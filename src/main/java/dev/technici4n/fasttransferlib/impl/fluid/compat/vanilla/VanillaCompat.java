package dev.technici4n.fasttransferlib.impl.fluid.compat.vanilla;

import dev.technici4n.fasttransferlib.api.ContainerItemContext;
import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.fluid.FluidApi;
import dev.technici4n.fasttransferlib.api.fluid.FluidConstants;
import dev.technici4n.fasttransferlib.api.fluid.FluidIo;
import dev.technici4n.fasttransferlib.api.item.ItemKey;
import dev.technici4n.fasttransferlib.impl.mixin.BucketItemAccess;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.FishBucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;

public class VanillaCompat {
	public static void load() {
		// initialize static
	}

	static {
		FluidApi.SIDED.registerForBlocks((world, pos, state, direction) -> new CauldronWrapper(world, pos),
				Blocks.CAULDRON);
		FluidApi.ITEM.register(BottleCompat::of, Items.POTION, Items.GLASS_BOTTLE);
		FluidApi.ITEM.registerFallback((itemKey, context) -> {
			if (!(itemKey.getItem() instanceof BucketItem)) return null;
			if (itemKey.getItem() instanceof FishBucketItem) return null;
			return new BucketCompat((BucketItem) itemKey.getItem(), context);
		});
	}

	private static class BucketCompat implements FluidIo {
		private final Fluid fluid;
		private final ContainerItemContext context;

		private BucketCompat(BucketItem item, ContainerItemContext context) {
			this.fluid = ((BucketItemAccess) item).getFluid();
			this.context = context;
		}

		@Override
		public int getFluidSlotCount() {
			return 1;
		}

		@Override
		public Fluid getFluid(int slot) {
			checkSingleSlot(slot);
			return fluid;
		}

		@Override
		public long getFluidAmount(int slot) {
			checkSingleSlot(slot);
			return fluid == Fluids.EMPTY ? 0 : FluidConstants.BUCKET;
		}

		@Override
		public boolean supportsFluidInsertion() {
			return true;
		}

		@Override
		public long insert(Fluid fluid, long amount, Simulation simulation) {
			if (context.getCount() == 0) return amount;
			if (this.fluid != Fluids.EMPTY) return amount;
			if (amount < FluidConstants.BUCKET) return amount;
			if (!context.transform(1, ItemKey.of(fluid.getBucketItem()), simulation)) return amount;
			return amount - FluidConstants.BUCKET;
		}

		@Override
		public boolean supportsFluidExtraction() {
			return true;
		}

		@Override
		public long extract(int slot, Fluid fluid, long maxAmount, Simulation simulation) {
			checkSingleSlot(slot);
			if (context.getCount() == 0) return 0;
			if (this.fluid == Fluids.EMPTY || this.fluid != fluid) return 0;
			if (!context.transform(1, ItemKey.of(Items.BUCKET), simulation)) return 0;
			return FluidConstants.BUCKET;
		}
	}

	private static class BottleCompat implements FluidIo {
		private static final ItemKey WATER_BOTTLE;
		private final Potion potion;
		private final ContainerItemContext context;

		static {
			ItemStack waterBottle = new ItemStack(Items.POTION);
			PotionUtil.setPotion(waterBottle, Potions.WATER);
			WATER_BOTTLE = ItemKey.of(waterBottle);
		}

		private static @Nullable BottleCompat of(ItemKey key, ContainerItemContext context) {
			Potion potion = PotionUtil.getPotion(key.copyTag());

			if (potion == Potions.WATER || potion == Potions.EMPTY) {
				return new BottleCompat(potion, context);
			} else {
				return null;
			}
		}

		private BottleCompat(Potion potion, ContainerItemContext context) {
			this.potion = potion;
			this.context = context;
		}

		@Override
		public int getFluidSlotCount() {
			return 1;
		}

		@Override
		public Fluid getFluid(int slot) {
			checkSingleSlot(slot);
			return potion == Potions.WATER ? Fluids.WATER : Fluids.EMPTY;
		}

		@Override
		public long getFluidAmount(int slot) {
			checkSingleSlot(slot);
			return potion == Potions.WATER ? FluidConstants.BOTTLE : 0;
		}

		@Override
		public boolean supportsFluidInsertion() {
			return true;
		}

		@Override
		public long insert(Fluid fluid, long amount, Simulation simulation) {
			if (context.getCount() == 0) return amount;
			if (potion != Potions.EMPTY) return amount;
			if (amount < FluidConstants.BOTTLE) return amount;
			if (fluid != Fluids.WATER) return amount;
			if (!context.transform(1, WATER_BOTTLE, simulation)) return amount;
			return amount - FluidConstants.BOTTLE;
		}

		@Override
		public boolean supportsFluidExtraction() {
			return true;
		}

		@Override
		public long extract(int slot, Fluid fluid, long maxAmount, Simulation simulation) {
			checkSingleSlot(slot);
			if (context.getCount() == 0) return 0;
			if (potion != Potions.WATER) return 0;
			if (maxAmount < FluidConstants.BOTTLE) return 0;
			if (!context.transform(1, ItemKey.of(Items.GLASS_BOTTLE), simulation)) return 0;
			return FluidConstants.BOTTLE;
		}
	}

	private static void checkSingleSlot(int slot) {
		if (slot != 0) {
			throw new IndexOutOfBoundsException("This item container only has 1 slot, this slot is out of bounds: " + slot);
		}
	}
}
