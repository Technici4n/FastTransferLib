package dev.technici4n.fasttransferlib.api.fluid;

import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.impl.fluid.FluidImpl;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;

/**
 * A fluid inventory.
 */
public interface FluidIo {
	/**
	 * Return the number of slots in the view.
	 */
	int getFluidSlotCount();

	/**
	 * Return the fluid stored in a slot, or {@link Fluids#EMPTY} if there is no fluid.
	 *
	 * @param slot The slot id, must be between 0 and {@link FluidIo#getFluidSlotCount()}.
	 * @return the fluid stored in the slot, or {@link Fluids#EMPTY} if there is no fluid.
	 * @throws IndexOutOfBoundsException if the slot is not in the range [0, {@link FluidIo#getFluidSlotCount()}).
	 */
	Fluid getFluid(int slot);

	/**
	 * Return the amount of fluid stored in a slot, or 0 if there is no fluid. The amount is given in droplets (1/81000 of a bucket).
	 *
	 * @param slot The slot id, must be between 0 and {@link FluidIo#getFluidSlotCount()}.
	 * @return the amount of fluid stored in the slot, or 0 if there is no fluid.
	 * @throws IndexOutOfBoundsException if the slot is not in the range [0, {@link FluidIo#getFluidSlotCount()}).
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
	 * Return false if this object does not support insertion at all, meaning that insertion will always return the passed amount,
	 * and insert-only pipes should not connect.
	 */
	default boolean supportsFluidInsertion() {
		return false;
	}

	/**
	 * Insert fluid into this inventory, and return the amount of leftover fluid. The amounts are given in droplets (1/81000 of a bucket).
	 * Distribution is left entirely to the implementor.
	 *
	 * <p>If simulation is {@link Simulation#SIMULATE}, the result of the operation must be returned, but the underlying state of the fluid insertable must not change.
	 *
	 * @param fluid      The fluid to insert
	 * @param amount     The amount of fluid to insert, in droplets
	 * @param simulation If {@link Simulation#SIMULATE}, do not mutate the insertable
	 * @return the amount of fluid that could not be inserted
	 */
	default long insert(Fluid fluid, long amount, Simulation simulation) {
		return amount;
	}

	/**
	 * Return false if this object does not support extraction at all, meaning that extraction will always return 0,
	 * and extract-only pipes should not connect.
	 */
	default boolean supportsFluidExtraction() {
		return false;
	}

	/**
	 * Extract some fluid from this extractable, with the same semantics as {@link FluidIo#extract(Fluid, long, Simulation) the slotless variant}.
	 * The slot parameter, as long as it is in range, can be anything.
	 * It is however expected that calling this in a loop will be faster for callers that need to move a lot of fluid.
	 *
	 * @param slot       The slot id, must be between 0 and {@link FluidIo#getFluidSlotCount()}.
	 * @param fluid      The filter for the fluid to extract
	 * @param maxAmount  The amount of fluid to extract at most, in droplets
	 * @param simulation If {@link Simulation#SIMULATE}, do not mutate the insertable
	 * @return The amount of fluid extracted
	 */
	default long extract(int slot, Fluid fluid, long maxAmount, Simulation simulation) {
		return 0;
	}

	/**
	 * Extract some fluid from this extractable, matching the passed fluid. The amounts are given in droplets (1/81000 of a bucket).
	 *
	 * <p>If simulation is {@link Simulation#SIMULATE}, the result of the operation must be returned, but the underlying state of the fluid extractable must not change.
	 *
	 * @param fluid      The filter for the fluid to extract
	 * @param maxAmount  The amount of fluid to extract at most, in droplets
	 * @param simulation If {@link Simulation#SIMULATE}, do not mutate the insertable
	 * @return The amount of fluid extracted
	 * @implNote Implementations are encouraged to override this method with a more performant implementation.
	 */
	default long extract(Fluid fluid, long maxAmount, Simulation simulation) {
		if (!supportsFluidExtraction()) return 0;

		for (int i = 0; i < getFluidSlotCount(); ++i) {
			long extracted = extract(i, fluid, maxAmount, simulation);

			if (extracted > 0) {
				return extracted;
			}
		}

		return 0;
	}
}
