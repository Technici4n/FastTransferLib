package dev.technici4n.fasttransferlib.impl.energy.compat;

import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.energy.EnergyIo;
import dev.technici4n.fasttransferlib.impl.mixin.EnergyHandlerAccess;
import team.reborn.energy.EnergyHandler;

import net.minecraft.util.math.Direction;

public class TrWrappedEnergyHandler implements EnergyIo {
	private final EnergyHandler handler;

	public TrWrappedEnergyHandler(EnergyHandler handler, Direction direction) {
		this.handler = handler.side(direction);
	}

	@Override
	public double getEnergy() {
		return handler.getEnergy();
	}

	@Override
	public double getEnergyCapacity() {
		return handler.getMaxStored();
	}

	@Override
	public boolean supportsInsertion() {
		return handler.getMaxInput() > 1e-9;
	}

	@Override
	public double insert(double amount, Simulation simulation) {
		startSimulating(simulation);
		double inserted = handler.insert(amount);
		stopSimulating(simulation);
		return amount - inserted;
	}

	@Override
	public boolean supportsExtraction() {
		return handler.getMaxOutput() > 1e-9;
	}

	@Override
	public double extract(double maxAmount, Simulation simulation) {
		startSimulating(simulation);
		double extracted = handler.extract(maxAmount);
		stopSimulating(simulation);
		return extracted;
	}

	private void startSimulating(Simulation simulation) {
		if (simulation.isSimulating()) {
			handler.simulate();
		}
	}

	private void stopSimulating(Simulation simulation) {
		if (simulation.isSimulating()) {
			((EnergyHandlerAccess) (Object) handler).setSimulate(false);
		}
	}
}
