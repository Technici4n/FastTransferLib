package dev.technici4n.fasttransferlib.api.energy;

import dev.technici4n.fasttransferlib.impl.energy.EnergyImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;

public class EnergyApi {
	public static final BlockApiLookup<EnergyIo, @NotNull Direction> SIDED =
			BlockApiLookup.get(new Identifier("fasttransferlib:sided_energy_io"), EnergyIo.class, Direction.class);
	public static final ItemApiLookup<EnergyIo, Void> ITEM =
			ItemApiLookup.get(new Identifier("fasttransferlib:energy_io"), EnergyIo.class, Void.class);

	public static @Nullable EnergyIo ofPlayerHand(PlayerEntity player, Hand hand) {
		return ITEM.find(player.getStackInHand(hand), null);
	}

	public static @Nullable EnergyIo ofPlayerCursor(PlayerEntity player) {
		return ITEM.find(player.inventory.getCursorStack(), null);
	}

	static {
		EnergyImpl.loadTrCompat();
	}
}
