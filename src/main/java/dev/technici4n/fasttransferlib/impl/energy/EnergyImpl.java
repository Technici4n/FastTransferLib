package dev.technici4n.fasttransferlib.impl.energy;

import java.lang.reflect.Method;

import net.fabricmc.loader.api.FabricLoader;

public class EnergyImpl {
	public static void loadTrCompat() {
		if (FabricLoader.getInstance().isModLoaded("team_reborn_energy")) {
			try {
				Class<?> clazz = Class.forName("dev.technici4n.fasttransferlib.impl.energy.compat.TrEnergyCompat");
				Method init = clazz.getMethod("init");
				init.invoke(null);
			} catch (Exception ex) {
				throw new RuntimeException("TR energy was detected, but energy compat loading failed", ex);
			}
		}
	}
}
