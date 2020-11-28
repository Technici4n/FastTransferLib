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
     * @param from the extractable to move fluid from
     * @param to the insertable to move fluid to
     * @param maxAmount The maximum amount of fluid to move.
     * @param startSlot The first slot of the range to move, inclusive.
     * @param endSlot The last slot of the range to move, exclusive.
     * @return The amount of fluid that was moved.
     * @throws IndexOutOfBoundsException if the start or end slot is out of bounds of the {@link FluidExtractable}'s slot count.
     */
    public static long moveRange(FluidExtractable from, FluidInsertable to, long maxAmount, int startSlot, int endSlot) {
        long totalMoved = 0;
        for (int i = startSlot; i < endSlot && maxAmount > 0; ++i) {
            // Try to extract the maximum amount
            Fluid fluid = from.getFluid(i);
            long moved = from.extract(i, fluid, maxAmount, Simulation.SIMULATE);
            if (moved <= 0) continue;

            // Try to insert the maximum amount that can be extracted
            moved -= to.insert(fluid, moved, Simulation.SIMULATE);
            if (moved <= 0) continue;

            // Try to extract again with the maximum amount for the insertable, and return if that doesn't match
            if (from.extract(i, fluid, moved, Simulation.SIMULATE) != moved) continue;

            // Move the fluid at last
            if (from.extract(i, fluid, moved, Simulation.ACT) != moved) {
                // TODO: throw
            }
            if (to.insert(fluid, moved, Simulation.ACT) != 0) {
                // TODO: throw
            }

            totalMoved += moved;
            maxAmount -= moved;

            --i; // multiple attempts may be necessary
        }
        return totalMoved;
    }

    /**
     * Move some fluid from a {@link FluidExtractable} to a {@link FluidInsertable}.
     *
     * @param from the extractable to move fluid from
     * @param to the insertable to move fluid to
     * @param maxAmount The maximum amount of fluid to move.
     * @return The amount of fluid that was moved.
     */
    public static long moveRange(FluidExtractable from, FluidInsertable to, long maxAmount) {
        return moveRange(from, to, maxAmount, 0, from.getFluidSlotCount());
    }

    private FluidMovement() {
    }
}
