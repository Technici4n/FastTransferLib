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
import dev.technici4n.fasttransferlib.api.fluid.FluidIo;
import dev.technici4n.fasttransferlib.impl.compat.LbaUtil;

import net.minecraft.fluid.Fluid;

class LbaWrappedFluidIo implements FixedFluidInvView, FluidTransferable {
	private final FluidIo io;

	LbaWrappedFluidIo(FluidIo io) {
		this.io = io;
	}

	@Override
	public int getTankCount() {
		return io.getFluidSlotCount();
	}

	@Override
	public FluidVolume getInvFluid(int i) {
		return FluidKeys.get(io.getFluid(i)).withAmount(FluidAmount.of(io.getFluidAmount(i), 81000));
	}

	@Override
	public boolean isFluidValidForTank(int i, FluidKey fluidKey) {
		return true; // let's say it is?
	}

	@Override
	public FluidVolume attemptExtraction(FluidFilter filter, FluidAmount maxAmount, Simulation simulation) {
		for (int i = 0; i < io.getFluidSlotCount(); ++i) {
			Fluid fluid = io.getFluid(i);
			FluidKey key = FluidKeys.get(fluid);
			if (!filter.matches(key)) continue;
			long extracted = io.extract(i, fluid, maxAmount.asLong(81000, RoundingMode.DOWN), LbaUtil.getSimulation(simulation));

			if (extracted > 0) {
				return key.withAmount(FluidAmount.of(extracted, 81000));
			}
		}

		return FluidVolumeUtil.EMPTY;
	}

	@Override
	public FluidVolume attemptInsertion(FluidVolume fluidVolume, Simulation simulation) {
		Fluid fluid = fluidVolume.getFluidKey().getRawFluid();
		if (fluid == null) return fluidVolume;

		long inserted = fluidVolume.getAmount_F().asLong(81000, RoundingMode.DOWN);
		inserted -= io.insert(fluid, inserted, LbaUtil.getSimulation(simulation));
		return fluidVolume.getFluidKey().withAmount(fluidVolume.getAmount_F().sub(FluidAmount.of(inserted, 81000)));
	}
}
