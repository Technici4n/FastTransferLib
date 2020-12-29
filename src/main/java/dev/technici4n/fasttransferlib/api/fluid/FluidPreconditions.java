package dev.technici4n.fasttransferlib.api.fluid;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;

public class FluidPreconditions {
	public static void notEmpty(Fluid fluid) {
		if (fluid == Fluids.EMPTY) {
			throw new IllegalArgumentException("Fluid may not be empty.");
		}
	}
}
