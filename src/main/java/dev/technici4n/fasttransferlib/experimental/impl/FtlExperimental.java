package dev.technici4n.fasttransferlib.experimental.impl;

import dev.technici4n.fasttransferlib.experimental.api.energy.EnergyStorage;
import dev.technici4n.fasttransferlib.experimental.impl.fluid.FluidApiImpl;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public class FtlExperimental implements ModInitializer {
	@Override
	public void onInitialize() {
		FluidApiImpl.init();
	}

	public static final EnergyStorage EMPTY_ENERGY_STORAGE = new EnergyStorage() {
		@Override
		public double getAmount() {
			return 0;
		}

		@Override
		public double getCapacity() {
			return 0;
		}

		@Override
		public boolean supportsExtraction() {
			return false;
		}

		@Override
		public double extract(double maxAmount, TransactionContext transaction) {
			return 0;
		}

		@Override
		public boolean supportsInsertion() {
			return false;
		}

		@Override
		public double insert(double maxAmount, TransactionContext transaction) {
			return 0;
		}
	};
}
