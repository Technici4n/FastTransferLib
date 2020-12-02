package dev.technici4n.fasttransferlib.api.fluid;

import dev.technici4n.fasttransferlib.impl.fluid.FluidImpl;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;

/**
 * A view of a fluid inventory.
 *
 * @see FluidInsertable
 * @see FluidExtractable
 */
public interface FluidView {
    /**
     * Return the number of slots in the view.
     */
    int getFluidSlotCount();

    /**
     * Return the fluid stored in a slot, or {@link Fluids#EMPTY} if there is no fluid.
     *
     * @param slot The slot id, must be between 0 and {@link FluidView#getFluidSlotCount()}.
     * @return the fluid stored in the slot, or {@link Fluids#EMPTY} if there is no fluid.
     * @throws IndexOutOfBoundsException if the slot is not in the range [0, {@link FluidView#getFluidSlotCount()}).
     */
    Fluid getFluid(int slot);

    /**
     * Return the amount of fluid stored in a slot, or 0 if there is no fluid. The unit for the amount is given by {@link FluidView#getFluidUnit}.
     *
     * @param slot The slot id, must be between 0 and {@link FluidView#getFluidSlotCount()}.
     * @return the amount of fluid stored in the slot, or 0 if there is no fluid.
     * @throws IndexOutOfBoundsException if the slot is not in the range [0, {@link FluidView#getFluidSlotCount()}).
     */
    long getFluidAmount(int slot);

    /**
     * Return the version of this inventory. If this number is the same for two calls, it is expected
     * that the underlying inventory hasn't changed. There is however no guarantee that the inventory has changed if this number has changed.
     */
    default int getVersion() {
        return FluidImpl.version++;
    }

    /**
     * Return the fluid unit to use for all operations with this view. For example, returning 1 will cause all amounts to be
     * treated as buckets.
     * @return a positive integer, the denominator to use for all operations with this view.
     */
    long getFluidUnit();
}
