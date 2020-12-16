package dev.technici4n.fasttransferlib.api.energy;

import dev.technici4n.fasttransferlib.api.Simulation;

/**
 * Utilities related to moving energy between two {@link EnergyIo}'s.
 */
public final class EnergyMovement {
	/**
	 * Move some items from some {@link EnergyIo} to another {@link EnergyIo}.
	 *
	 * @param from      the io to move energy from
	 * @param to       	the io to move the energy to
	 * @param maxAmount the maximum number of items to move.
	 * @return the amount of energy that was moved
	 */
	public static double move(EnergyIo from, EnergyIo to, double maxAmount) {
		maxAmount = from.extract(maxAmount, Simulation.SIMULATE);
		maxAmount -= to.insert(maxAmount, Simulation.ACT);
		double extracted = from.extract(maxAmount, Simulation.ACT);

		if (Math.abs(extracted - maxAmount) > 1e-9) {
			// TODO: throw
		}

		return maxAmount;
	}

	private EnergyMovement() {
	}
}
