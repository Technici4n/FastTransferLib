package dev.technici4n.fasttransferlib.api.fluid;

import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.impl.FtlImpl;

import net.minecraft.fluid.Fluid;

/**
 * Utilities related to moving fluids between two {@link FluidIo}'s.
 */
// TODO: add a Predicate<Fluid> parameter
public final class FluidMovement {
	/**
	 * Move some fluid from some slots of a {@link FluidIo} to another {@link FluidIo}.
	 *
	 * @param from          The io to move fluid from.
	 * @param to            The io to move fluid to.
	 * @param maxAmount     The maximum amount of fluid to move, in droplets.
	 * @param startSlot     The first slot of the range to move, inclusive.
	 * @param endSlot       The last slot of the range to move, exclusive.
	 * @return The amount of fluid that was moved.
	 * @throws IndexOutOfBoundsException if somes slots are out of bounds of the source {@link FluidIo}'s slot count.
	 */
	public static long moveRange(FluidIo from, FluidIo to, long maxAmount, int startSlot, int endSlot) {
		if (!from.supportsFluidExtraction() || !to.supportsFluidInsertion()) return 0;

		long totalMoved = 0;

		for (int i = startSlot; i < endSlot && maxAmount > 0; ++i) {
			// Try to extract the maximum amount
			Fluid fluid = from.getFluid(i);
			long moved = from.extract(i, fluid, maxAmount, Simulation.SIMULATE);
			if (moved <= 0) continue;

			// Try to insert the maximum amount that can be extracted
			moved -= to.insert(fluid, moved, Simulation.SIMULATE);
			if (moved <= 0) continue;

			// Try to extract that amount to make sure it can be extracted
			if (from.extract(i, fluid, moved, Simulation.SIMULATE) != moved) continue;

			// Move the fluid, extraction part
			long extracted = from.extract(i, fluid, moved, Simulation.ACT);

			// This extraction should never fail, as it was simulated just before being acted.
			// If it fails nonetheless, throw an exception.
			if (extracted != moved) {
				String errorMessage = String.format(
						"Bad FluidIo implementation: %s.\n"
						+ "Extraction of %d droplets was simulated, but only %d droplets could really be extracted.\n"
						+ "Slot: %d, fluid: %s",
						from, moved, extracted, i, FluidTextHelper.toString(fluid)
				);
				throw new AssertionError(errorMessage);
			}

			// Move the fluid, insertion part
			long leftover = to.insert(fluid, moved, Simulation.ACT);

			// This insertion can fail in some edge cases. If that happens, we try to re-insert in the source.
			// If that fails, we simply void the fluid that could not be inserted, and we print a warning in the console.
			if (leftover != 0) {
				long reinsertLeftover = from.insert(fluid, leftover, Simulation.ACT);

				if (reinsertLeftover != 0) {
					String errorMessage = String.format(
							"Bad FluidIo interaction while moving from %s to %s.\n"
									+ "Insertion of %d droplets was simulated, but only %d droplets could really be inserted.\n"
									+ "%d droplets could be re-inserted, and %d droplets were voided.\n"
									+ "Fluid: %s",
							from, to, moved, moved - leftover, moved - reinsertLeftover, reinsertLeftover, FluidTextHelper.toString(fluid)
					);
					FtlImpl.LOGGER.warn(errorMessage);
				}
			}

			totalMoved += moved;
			maxAmount -= moved;
		}

		return totalMoved;
	}

	/**
	 * Move some fluid from a {@link FluidIo} to another {@link FluidIo}.
	 *
	 * @param from          the io to move fluid from
	 * @param to            the io to move fluid to
	 * @param maxAmount     The maximum amount of fluid to move.
	 * @return The amount of fluid that was moved.
	 */
	public static long moveMultiple(FluidIo from, FluidIo to, long maxAmount) {
		return moveRange(from, to, maxAmount, 0, from.getFluidSlotCount());
	}

	private FluidMovement() {
	}
}
