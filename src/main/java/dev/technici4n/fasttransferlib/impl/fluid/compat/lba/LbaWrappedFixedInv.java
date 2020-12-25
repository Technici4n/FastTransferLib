package dev.technici4n.fasttransferlib.impl.fluid.compat.lba;

import java.math.RoundingMode;

import alexiil.mc.lib.attributes.fluid.FixedFluidInv;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.fluid.FluidKey;
import dev.technici4n.fasttransferlib.api.transfer.ResourceIo;

import net.minecraft.fluid.Fluid;

class LbaWrappedFixedInv implements ResourceIo<FluidKey> {
	private final FixedFluidInv wrapped;

	LbaWrappedFixedInv(FixedFluidInv wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public boolean supportsExtraction() {
		return true;
	}

	@Override
	public long extract(int slot, FluidKey fluid, long maxAmount, Simulation simulation) {
		FluidVolume extracted = wrapped.getTank(slot).attemptExtraction(key -> key.getRawFluid() == fluid.getFluid(), FluidAmount.of(maxAmount, 81000), alexiil.mc.lib.attributes.Simulation.SIMULATE);

		if (extracted.amount().mul(81000).numerator != 0) { // no fluid loss
			return 0;
		}

		if (simulation.isActing()) {
			extracted = wrapped.getTank(slot).attemptExtraction(key -> key.getRawFluid() == fluid.getFluid(), FluidAmount.of(maxAmount, 81000), alexiil.mc.lib.attributes.Simulation.ACTION);
		}

		return extracted.amount().asLong(81000, RoundingMode.DOWN);
	}

	@Override
	public boolean supportsInsertion() {
		return true;
	}

	@Override
	public long insert(FluidKey fluid, long amount, Simulation simulation) {
		FluidVolume leftover = wrapped.getInsertable().attemptInsertion(FluidKeys.get(fluid.getFluid()).withAmount(FluidAmount.of(amount, 81000)), alexiil.mc.lib.attributes.Simulation.SIMULATE);

		if (leftover.amount().mul(81000).numerator != 0) { // no fluid loss
			return 0;
		}

		if (simulation.isActing()) {
			leftover = wrapped.getInsertable().attemptInsertion(FluidKeys.get(fluid.getFluid()).withAmount(FluidAmount.of(amount, 81000)), alexiil.mc.lib.attributes.Simulation.ACTION);
		}

		return leftover.amount().asLong(81000, RoundingMode.DOWN);
	}

	@Override
	public int getSlotCount() {
		return wrapped.getTankCount();
	}

	@Override
	public FluidKey getResourceKey(int slot) {
		Fluid fluid = wrapped.getTank(slot).get().getFluidKey().getRawFluid();
		return fluid == null ? FluidKey.EMPTY : FluidKey.of(fluid);
	}

	@Override
	public long getAmount(int slot) {
		return wrapped.getTank(slot).get().amount().asLong(81000, RoundingMode.DOWN);
	}
}
