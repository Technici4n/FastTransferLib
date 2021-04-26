package dev.technici4n.fasttransferlib.impl.mixin;

import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import team.reborn.energy.Energy;
import team.reborn.energy.EnergyStorage;

@Mixin(Energy.class)
public interface EnergyAccess {
	@Accessor("holderRegistry")
	static HashMap<Predicate<Object>, Function<Object, EnergyStorage>> getHolders() {
		throw new UnsupportedOperationException("Mixin apply failed!");
	}
}
