package dev.technici4n.fasttransferlib.impl.fluid.compat.vanilla;

import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.fluid.FluidKey;
import dev.technici4n.fasttransferlib.api.transfer.ResourceIo;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

class CauldronWrapper implements ResourceIo<FluidKey> {
	private final World world;
	private final BlockPos pos;

	CauldronWrapper(World world, BlockPos pos) {
		this.world = world;
		this.pos = pos.toImmutable();
	}

	@Override
	public boolean supportsExtraction() {
		return true;
	}

	@Override
	public long extract(int slot, FluidKey fluid, long maxAmount, Simulation simulation) {
		if (fluid.getFluid() != Fluids.WATER) return 0;

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
	public boolean supportsInsertion() {
		return true;
	}

	@Override
	public long insert(FluidKey fluid, long amount, Simulation simulation) {
		if (fluid.getFluid() != Fluids.WATER) return amount;

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
	public int getSlotCount() {
		return 1;
	}

	@Override
	public FluidKey getResourceKey(int slot) {
		return getAmount(0) == 0 ? FluidKey.EMPTY : FluidKey.of(Fluids.WATER);
	}

	@Override
	public long getAmount(int slot) {
		Integer level = world.getBlockState(pos).get(CauldronBlock.LEVEL);
		return level == null ? 0 : level * 27000;
	}
}
