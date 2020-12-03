package dev.technici4n.fasttransferlib.impl.fluid.compat.lba;

import alexiil.mc.lib.attributes.fluid.FixedFluidInvView;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import dev.technici4n.fasttransferlib.api.fluid.FluidView;

class LbaFixedInvView implements FixedFluidInvView {
	private final FluidView view;

	LbaFixedInvView(FluidView view) {
		this.view = view;
	}

	@Override
	public int getTankCount() {
		return view.getFluidSlotCount();
	}

	@Override
	public FluidVolume getInvFluid(int i) {
		return FluidKeys.get(view.getFluid(i)).withAmount(FluidAmount.of(view.getFluidAmount(i), 81000));
	}

	@Override
	public boolean isFluidValidForTank(int i, FluidKey fluidKey) {
		return true; // let's say it is?
	}
}
