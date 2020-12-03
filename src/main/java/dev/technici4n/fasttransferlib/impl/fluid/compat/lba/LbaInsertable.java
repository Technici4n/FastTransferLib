package dev.technici4n.fasttransferlib.impl.fluid.compat.lba;

import java.math.RoundingMode;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import dev.technici4n.fasttransferlib.api.fluid.FluidInsertable;
import dev.technici4n.fasttransferlib.impl.compat.LbaUtil;

import net.minecraft.fluid.Fluid;

class LbaInsertable implements alexiil.mc.lib.attributes.fluid.FluidInsertable {
	private final FluidInsertable insertable;

	LbaInsertable(FluidInsertable insertable) {
		this.insertable = insertable;
	}

	@Override
	public FluidVolume attemptInsertion(FluidVolume fluidVolume, Simulation simulation) {
		Fluid fluid = fluidVolume.getFluidKey().getRawFluid();
		if (fluid == null) return fluidVolume;

		long inserted = fluidVolume.getAmount_F().asLong(81000, RoundingMode.DOWN);
		inserted -= insertable.insert(fluid, inserted, LbaUtil.getSimulation(simulation));
		return fluidVolume.getFluidKey().withAmount(fluidVolume.getAmount_F().sub(FluidAmount.of(inserted, 81000)));
	}
}
