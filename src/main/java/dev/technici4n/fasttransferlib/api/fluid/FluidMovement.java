package dev.technici4n.fasttransferlib.api.fluid;

import dev.technici4n.fasttransferlib.api.Simulation;

import net.minecraft.fluid.Fluid;

/**
 * Utilities related to moving fluids from a {@link FluidExtractable} to another {@link FluidInsertable}.
 */
// TODO: add a Predicate<Fluid> parameter
public final class FluidMovement {
	/**
	 * Move some fluid from some slots of a {@link FluidExtractable} to a {@link FluidInsertable}.
	 *
	 * @param from          the extractable to move fluid from
	 * @param to            the insertable to move fluid to
	 * @param maxAmount     The maximum amount of fluid to move.
	 * @param maxAmountUnit The unit of the maximum amount of fluid to move.
	 * @param startSlot     The first slot of the range to move, inclusive.
	 * @param endSlot       The last slot of the range to move, exclusive.
	 * @return The amount of fluid that was moved.
	 * @throws IndexOutOfBoundsException if the start or end slot is out of bounds of the {@link FluidExtractable}'s slot count.
	 */
	public static long moveRange(FluidExtractable from, FluidInsertable to, long maxAmount, long maxAmountUnit, int startSlot, int endSlot) {
		long fromDenom = from.getFluidUnit();
		long toDenom = to.getFluidUnit();
		long d = gcd(gcd(fromDenom, toDenom), maxAmountUnit);
		long fromFactor = fromDenom / d;
		long toFactor = toDenom / d;
		long maxFactor = maxAmountUnit / d;
		maxAmount /= maxFactor;

		long totalMoved = 0;

		for (int i = startSlot; i < endSlot && maxAmount > 0; ++i) {
			// Try to extract the maximum amount
			Fluid fluid = from.getFluid(i);
			long moved = from.extract(i, fluid, maxAmount * fromDenom, Simulation.SIMULATE) / fromDenom;
			if (moved <= 0) continue;

			// Try to insert the maximum amount that can be extracted
			moved -= (to.insert(fluid, moved * toDenom, Simulation.SIMULATE) + toDenom - 1) / toDenom;
			if (moved <= 0) continue;

			// Move the fluid
			if (from.extract(i, fluid, moved * fromDenom, Simulation.ACT) != moved * fromDenom) {
				// TODO: throw
			}

			if (to.insert(fluid, moved * toDenom, Simulation.ACT) != 0) {
				// TODO: throw
			}

			totalMoved += moved;
			maxAmount -= moved;

			--i; // multiple attempts may be necessary
		}

		return totalMoved * maxFactor;
	}

	/**
	 * Move some fluid from a {@link FluidExtractable} to a {@link FluidInsertable}.
	 *
	 * @param from          the extractable to move fluid from
	 * @param to            the insertable to move fluid to
	 * @param maxAmount     The maximum amount of fluid to move.
	 * @param maxAmountUnit The unit of the maximum amount of fluid to move.
	 * @return The amount of fluid that was moved.
	 */
	public static long moveRange(FluidExtractable from, FluidInsertable to, long maxAmount, long maxAmountUnit) {
		return moveRange(from, to, maxAmount, maxAmountUnit, 0, from.getFluidSlotCount());
	}

	/**
	 * Return the greatest common divisor of two positive integers.
	 */
	public static long gcd(long a, long b) {
		if (a <= 0 || b <= 0) throw new IllegalArgumentException("Cannot compute gcd of negative numbers.");

		if (a > b) {
			long t = a;
			a = b;
			b = t;
		}

		while (a != 0) {
			long t = a;
			a = b % a;
			b = t;
		}

		return b;
	}

	private FluidMovement() {
	}
}
