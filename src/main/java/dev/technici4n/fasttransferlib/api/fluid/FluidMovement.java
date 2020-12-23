package dev.technici4n.fasttransferlib.api.fluid;

import dev.technici4n.fasttransferlib.api.Simulation;

import net.minecraft.fluid.Fluid;

/**
 * Utilities related to moving fluids between two {@link FluidIo}'s.
 */
// TODO: add a Predicate<Fluid> parameter
public final class FluidMovement {
	/**
	 * Move some fluid from a {@link FluidIo} to another {@link FluidIo}.
	 *
	 * @param from          the io to move fluid from
	 * @param to            the io to move fluid to
	 * @param maxAmount     The maximum amount of fluid to move.
	 * @return The amount of fluid that was moved.
	 */
	public static long move(FluidIo from, FluidIo to, long maxAmount) {
		if (!from.supportsFluidExtraction() || !to.supportsFluidInsertion()) return 0;

		Fluid[] fluids = from.getFluids();
		int fluidcount = fluids.length;

		long totalMoved = 0;

		for (int i = 0; i < fluidcount && maxAmount > 0; ++i) {
			// Try to extract the maximum amount
			Fluid fluid = fluids[i];
			long moved = from.extract(fluid, maxAmount, Simulation.SIMULATE);
			if (moved <= 0) continue;

			// Try to insert the maximum amount that can be extracted
			moved -= to.insert(fluid, moved, Simulation.SIMULATE);
			if (moved <= 0) continue;

			// Try to extract that amount to make sure it can be extracted
			if (from.extract(fluid, moved, Simulation.SIMULATE) != moved) continue;

			// Move the fluid
			if (from.extract(fluid, moved, Simulation.ACT) != moved) {
				// TODO: throw
			}

			if (to.insert(fluid, moved, Simulation.ACT) != 0) {
				// TODO: throw
			}

			totalMoved += moved;
			maxAmount -= moved;
		}

		return totalMoved;
	}

	private FluidMovement() {
	}
}
