package dev.technici4n.fasttransferlib.experimental.impl;

import dev.technici4n.fasttransferlib.experimental.impl.fluid.FluidApiImpl;

import net.fabricmc.api.ModInitializer;

public class FtlExperimental implements ModInitializer {
	@Override
	public void onInitialize() {
		FluidApiImpl.init();
	}
}
