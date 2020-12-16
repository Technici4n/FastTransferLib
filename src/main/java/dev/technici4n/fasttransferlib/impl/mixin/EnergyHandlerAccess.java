package dev.technici4n.fasttransferlib.impl.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import team.reborn.energy.EnergyHandler;

@Mixin(value = EnergyHandler.class, remap = false)
public interface EnergyHandlerAccess {
	@Accessor("simulate")
	void setSimulate(boolean simulate);
}
