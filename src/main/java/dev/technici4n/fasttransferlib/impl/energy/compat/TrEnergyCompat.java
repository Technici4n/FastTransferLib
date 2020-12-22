package dev.technici4n.fasttransferlib.impl.energy.compat;

import dev.technici4n.fasttransferlib.api.energy.EnergyApi;
import team.reborn.energy.Energy;

public class TrEnergyCompat {
	public static void init() {
		// initialize static
	}

	static {
		EnergyApi.SIDED.registerFallback((world, pos, state, blockEntity, direction) -> {
			if (blockEntity != null && Energy.valid(blockEntity)) {
				return new TrWrappedEnergyHandler(Energy.of(blockEntity), direction);
			}

			return null;
		});
	}
}
