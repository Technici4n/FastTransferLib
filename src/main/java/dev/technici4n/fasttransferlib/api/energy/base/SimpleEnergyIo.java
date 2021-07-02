package dev.technici4n.fasttransferlib.api.energy.base;

import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.energy.EnergyIo;
import dev.technici4n.fasttransferlib.api.energy.EnergyPreconditions;

/**
 * A simple {@link EnergyIo} implementation with fixed capacity, and insert and extract limits per operation.
 * You can create a subclass if you need to override {@link #markDirty()}.
 */
public class SimpleEnergyIo implements EnergyIo {
	public double energy;
	public final double capacity;
	public final double maxInsert, maxExtract;

	/**
	 * @param capacity Maximum energy that can be stored.
	 * @param maxInsert Maximum energy that can be inserted in one insertion. If 0, insertion is not allowed.
	 * @param maxExtract Maximum energy that can be extracted in one extraction. If 0, extraction is not allowed.
	 */
	public SimpleEnergyIo(double capacity, double maxInsert, double maxExtract) {
		EnergyPreconditions.notNegative(capacity);
		EnergyPreconditions.notNegative(maxInsert);
		EnergyPreconditions.notNegative(maxExtract);

		this.capacity = capacity;
		this.maxInsert = maxInsert;
		this.maxExtract = maxExtract;
	}

	/**
	 * Override this to call markDirty or do other post-transfer logic.
	 */
	public void markDirty() {
	}

	@Override
	public double getEnergy() {
		return energy;
	}

	@Override
	public double getEnergyCapacity() {
		return capacity;
	}

	@Override
	public boolean supportsInsertion() {
		return maxInsert > 1e-9;
	}

	@Override
	public double insert(double maxAmount, Simulation simulation) {
		EnergyPreconditions.notNegative(maxAmount);
		double amountInserted = Math.min(maxAmount, capacity - energy);

		if (amountInserted > 1e-9) {
			if (simulation.isActing()) {
				energy += amountInserted;
				markDirty();
			}

			return maxAmount - amountInserted;
		}

		return maxAmount;
	}

	@Override
	public boolean supportsExtraction() {
		return maxExtract > 1e-9;
	}

	@Override
	public double extract(double maxAmount, Simulation simulation) {
		EnergyPreconditions.notNegative(maxAmount);
		double amountExtracted = Math.min(maxAmount, energy);

		if (amountExtracted > 1e-9) {
			if (simulation.isActing()) {
				energy -= amountExtracted;
				markDirty();
			}

			return amountExtracted;
		}

		return 0;
	}
}
