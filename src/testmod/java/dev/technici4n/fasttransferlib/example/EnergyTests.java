package dev.technici4n.fasttransferlib.example;

import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.energy.EnergyApi;
import dev.technici4n.fasttransferlib.api.energy.EnergyIo;
import dev.technici4n.fasttransferlib.api.energy.base.SimpleItemEnergyIo;
import team.reborn.energy.Energy;
import team.reborn.energy.EnergyHandler;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import net.fabricmc.api.ModInitializer;

public class EnergyTests implements ModInitializer {
	@Override
	public void onInitialize() {
		// Apples are now batteries
		EnergyApi.ITEM.registerForItems(SimpleItemEnergyIo.getProvider(500, 10, 20), Items.APPLE);

		ItemStack stack = new ItemStack(Items.APPLE);
		EnergyIo io = EnergyApi.ITEM.find(stack, null);
		EnergyHandler handler = Energy.of(stack);

		test(io.insert(30, Simulation.ACT) == 20, "Should have inserted 10 exactly.");
		test(handler.getEnergy() == 10, "Should have returned 10 stored energy.");

		System.out.println("FTL tests ok!");
	}

	private static void test(boolean expression, String error) {
		if (!expression) {
			throw new AssertionError(error);
		}
	}
}
