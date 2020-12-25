package dev.technici4n.fasttransferlib.impl.fluid.compat.lba;

import java.math.RoundingMode;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FixedFluidInvView;
import alexiil.mc.lib.attributes.fluid.FluidTransferable;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import dev.technici4n.fasttransferlib.api.transfer.ResourceIo;
import dev.technici4n.fasttransferlib.impl.compat.LbaUtil;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;

class LbaWrappedFluidIo implements FixedFluidInvView, FluidTransferable {
	private final ResourceIo<dev.technici4n.fasttransferlib.api.fluid.FluidKey> io;

	LbaWrappedFluidIo(ResourceIo<dev.technici4n.fasttransferlib.api.fluid.FluidKey> io) {
		this.io = io;
	}

	@Override
	public int getTankCount() {
		return io.getSlotCount();
	}

	@Override
	public FluidVolume getInvFluid(int i) {
		Fluid fluid = io.getResourceKey(i).getFluid();
		return fluid == Fluids.EMPTY ? FluidVolumeUtil.EMPTY : FluidKeys.get(fluid).withAmount(FluidAmount.of(io.getAmount(i), 81000));
	}

	@Override
	public boolean isFluidValidForTank(int i, FluidKey fluidKey) {
		return true; // let's say it is?
	}

	@Override
	public FluidVolume attemptExtraction(FluidFilter filter, FluidAmount maxAmount, Simulation simulation) {
		long maxExtract = maxAmount.asLong(81000, RoundingMode.DOWN);
		long extracted = 0;
		FluidKey extractedKey = FluidKeys.EMPTY;

		for (int i = 0; i < io.getSlotCount(); ++i) {
			Fluid fluid = io.getResourceKey(i).getFluid();
			if (fluid == Fluids.EMPTY) continue;
			FluidKey key = FluidKeys.get(fluid);
			if (!filter.matches(key)) continue;
			extracted += io.extract(i, dev.technici4n.fasttransferlib.api.fluid.FluidKey.of(fluid), maxExtract - extracted, LbaUtil.getSimulation(simulation));

			if (extracted > 0) {
				extractedKey = key;
				filter = key.exactFilter;
			}
		}

		return extractedKey.withAmount(FluidAmount.of(extracted, 81000));
	}

	@Override
	public FluidVolume attemptInsertion(FluidVolume fluidVolume, Simulation simulation) {
		Fluid fluid = fluidVolume.getFluidKey().getRawFluid();
		if (fluid == null || fluid == Fluids.EMPTY) return fluidVolume;

		long inserted = fluidVolume.getAmount_F().asLong(81000, RoundingMode.DOWN);
		inserted -= io.insert(dev.technici4n.fasttransferlib.api.fluid.FluidKey.of(fluid), inserted, LbaUtil.getSimulation(simulation));
		return fluidVolume.getFluidKey().withAmount(fluidVolume.getAmount_F().sub(FluidAmount.of(inserted, 81000)));
	}
}
