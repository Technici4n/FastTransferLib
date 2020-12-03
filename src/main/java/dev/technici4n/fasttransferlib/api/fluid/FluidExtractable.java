package dev.technici4n.fasttransferlib.api.fluid;

import dev.technici4n.fasttransferlib.api.Simulation;

import net.minecraft.fluid.Fluid;

/**
 * A fluid inventory that supports extracting fluids.
 *
 * @see FluidInsertable
 */
public interface FluidExtractable extends FluidView {
	/**
	 * Extract some fluid from this extractable, with the same semantics as {@link FluidExtractable#extract(Fluid, long, Simulation) the slotless variant}.
	 * The slot parameter, as long as it is in range, can be anything.
	 * It is however expected that calling this in a loop will be faster for callers that need to move a lot of fluid.
	 *
	 * @param slot       The slot id, must be between 0 and {@link FluidView#getFluidSlotCount()}.
	 * @param fluid      The filter for the fluid to extract
	 * @param maxAmount  The amount of fluid to extract at most
	 * @param simulation If {@link Simulation#SIMULATE}, do not mutate the insertable
	 * @return The amount of fluid extracted
	 */
	long extract(int slot, Fluid fluid, long maxAmount, Simulation simulation);

	/**
	 * Extract some fluid from this extractable, matching the passed fluid. The unit for the amount is given by {@link FluidView#getFluidUnit}.
	 *
	 * <p>If simulation is {@link Simulation#SIMULATE}, the result of the operation must be returned, but the underlying state of the fluid extractable must not change.
	 *
	 * @param fluid      The filter for the fluid to extract
	 * @param maxAmount  The amount of fluid to extract at most
	 * @param simulation If {@link Simulation#SIMULATE}, do not mutate the insertable
	 * @return The amount of fluid extracted
	 * @implNote Implementations are encouraged to override this method with a more performant implementation.
	 */
	default long extract(Fluid fluid, long maxAmount, Simulation simulation) {
		for (int i = 0; i < getFluidSlotCount(); ++i) {
			long extracted = extract(i, fluid, maxAmount, simulation);

			if (extracted > 0) {
				return extracted;
			}
		}

		return 0;
	}
}
