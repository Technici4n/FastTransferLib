package dev.technici4n.fasttransferlib.impl.fluid.compat.vanilla;

import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.fluid.FluidConstants;
import dev.technici4n.fasttransferlib.api.fluid.FluidIo;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

class CauldronWrapper implements FluidIo {
	private static final Fluid[] EMPTY_FLUID_ARRAY = {};
	private static final Fluid[] WATER_ARRAY = {Fluids.WATER};
	private static final long[][] AMOUNTS = {
		{},
		{FluidConstants.BOTTLE},
		{FluidConstants.BOTTLE * 2},
		{FluidConstants.BUCKET}
	};

	private final World world;
	private final BlockPos pos;

	CauldronWrapper(World world, BlockPos pos) {
		this.world = world;
		this.pos = pos.toImmutable();
	}

	@Override
	public boolean supportsFluidExtraction() {
		return true;
	}

	@Override
	public long extract(Fluid fluid, long maxAmount, Simulation simulation) {
		if (fluid != Fluids.WATER) return 0;

		BlockState state = world.getBlockState(pos);

		if (state.isOf(Blocks.CAULDRON)) {
			int level = state.get(CauldronBlock.LEVEL);
			long extracted = Math.min(level, maxAmount / 27000);

			if (simulation.isActing()) {
				world.setBlockState(pos, state.with(CauldronBlock.LEVEL, (int) (level - extracted)));
			}

			return extracted * 27000;
		}

		return 0;
	}

	@Override
	public boolean supportsFluidInsertion() {
		return true;
	}

	@Override
	public long insert(Fluid fluid, long amount, Simulation simulation) {
		if (fluid != Fluids.WATER) return amount;

		BlockState state = world.getBlockState(pos);

		if (state.isOf(Blocks.CAULDRON)) {
			int level = state.get(CauldronBlock.LEVEL);
			long inserted = Math.min(amount / 27000, 3 - level);

			if (simulation.isActing()) {
				world.setBlockState(pos, state.with(CauldronBlock.LEVEL, (int) (level + inserted)));
			}

			return amount - inserted * 27000;
		}

		return amount;
	}

	@Override
	public Fluid[] getFluids() {
		if (world.getBlockState(pos).get(CauldronBlock.LEVEL) == 0) {
			return EMPTY_FLUID_ARRAY;
		} else {
			return WATER_ARRAY;
		}
	}

	@Override
	public long[] getFluidAmounts() {
		return AMOUNTS[world.getBlockState(pos).get(CauldronBlock.LEVEL)];
	}

	@Override
	public int getVersion() {
		return world.getBlockState(pos).get(CauldronBlock.LEVEL);
	}
}
