package dev.technici4n.fasttransferlib.api.fluid;

import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.impl.fluid.FluidImpl;

import net.minecraft.fluid.Fluid;

/**
 * A fluid inventory.
 */
public interface FluidIo {
	/**
	 * Returns the fluids contained in this, no duplicate fluids or empty fluids should be returned. The return value should not be mutated by the caller or this.
	 */
	Fluid[] getFluids();

	/**
	 * Returns the amount of each fluid gotten by getFluids. The return value should not be mutated by the caller or this.
	 */
	long[] getFluidAmounts();

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
		return 0;
	}
}
