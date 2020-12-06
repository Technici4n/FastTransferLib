package dev.technici4n.fasttransferlib.api.fluid;

import dev.technici4n.fasttransferlib.api.Simulation;

import net.minecraft.fluid.Fluid;

/**
 * A fluid inventory that supports inserting fluids.
 *
 * @see FluidExtractable
 */
public interface FluidInsertable extends FluidView {
	/**
	 * Insert fluid into this inventory, and return the amount of leftover fluid. The amounts are given in millidroplets (1/81000 of a bucket).
	 * Distribution is left entirely to the implementor.
	 *
	 * <p>If simulation is {@link Simulation#SIMULATE}, the result of the operation must be returned, but the underlying state of the fluid insertable must not change.
	 *
	 * @param fluid      The fluid to insert
	 * @param amount     The amount of fluid to insert, in millidroplets
	 * @param simulation If {@link Simulation#SIMULATE}, do not mutate the insertable
	 * @return the amount of fluid that could not be inserted
	 */
	long insert(Fluid fluid, long amount, Simulation simulation);
}
