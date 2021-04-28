package dev.technici4n.fasttransferlib.impl.energy.compat.ftl_from_tr;

import dev.technici4n.fasttransferlib.api.energy.EnergyIo;
import team.reborn.energy.EnergySide;
import team.reborn.energy.EnergyStorage;
import team.reborn.energy.EnergyTier;

public abstract class EnergyIoWrapper implements EnergyStorage {
	public abstract EnergyIo getIo(EnergySide side);

	@Override
	public double getStored(EnergySide side) {
		return getIo(side).getEnergy();
	}

	@Override
	public void setStored(double v) {
		throw new UnsupportedOperationException();
	}

	@Override
	public double getMaxStoredPower() {
		throw new UnsupportedOperationException();
	}

	@Override
	public EnergyTier getTier() {
		throw new UnsupportedOperationException();
	}

	@Override
	public double getMaxInput(EnergySide side) {
		throw new UnsupportedOperationException();
	}

	@Override
	public double getMaxOutput(EnergySide side) {
		throw new UnsupportedOperationException();
	}
}
