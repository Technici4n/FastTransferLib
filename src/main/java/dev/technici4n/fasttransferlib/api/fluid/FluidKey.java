package dev.technici4n.fasttransferlib.api.fluid;

import dev.technici4n.fasttransferlib.api.transfer.ResourceKey;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;

public interface FluidKey extends ResourceKey {
	FluidKey EMPTY = of(Fluids.EMPTY);

	Fluid getFluid();

	@Override
	default boolean isEmpty() {
		return this == EMPTY;
	}

	static FluidKey of(Fluid fluid) {
		return (FluidKey) fluid;
	}
}
