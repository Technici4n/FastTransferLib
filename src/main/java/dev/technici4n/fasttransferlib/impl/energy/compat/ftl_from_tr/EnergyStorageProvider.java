package dev.technici4n.fasttransferlib.impl.energy.compat.ftl_from_tr;

import org.jetbrains.annotations.Nullable;
import team.reborn.energy.EnergyStorage;

public interface EnergyStorageProvider {
	@Nullable
	EnergyStorage find(Object object);
}
