package dev.technici4n.fasttransferlib.impl.energy.compat.ftl_from_tr;

import dev.technici4n.fasttransferlib.api.energy.EnergyIo;

public class EmptyEnergyIo implements EnergyIo {
	@Override
	public double getEnergy() {
		return 0;
	}

	@Override
	public double getEnergyCapacity() {
		return 0;
	}

	@Override
	public boolean supportsInsertion() {
		return false;
	}

	@Override
	public boolean supportsExtraction() {
		return false;
	}
}
