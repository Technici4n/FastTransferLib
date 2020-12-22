package dev.technici4n.fasttransferlib.api.energy;

import dev.technici4n.fasttransferlib.api.ContainerItemContext;
import dev.technici4n.fasttransferlib.api.item.ItemKey;
import dev.technici4n.fasttransferlib.api.item.ItemKeyApiLookup;
import dev.technici4n.fasttransferlib.api.item.ItemKeyApiLookupRegistry;
import dev.technici4n.fasttransferlib.impl.energy.EnergyImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookupRegistry;

public class EnergyApi {
	public static final BlockApiLookup<EnergyIo, @NotNull Direction> SIDED =
			BlockApiLookupRegistry.getLookup(new Identifier("fasttransferlib:sided_energy_io"), EnergyIo.class, Direction.class);
	public static final ItemKeyApiLookup<EnergyIo, ContainerItemContext> ITEM =
			ItemKeyApiLookupRegistry.getLookup(new Identifier("fasttransferlib:energy_io"), EnergyIo.class, ContainerItemContext.class);

	public static @Nullable EnergyIo ofPlayerHand(PlayerEntity player, Hand hand) {
		return ITEM.get(ItemKey.of(player.getStackInHand(hand)), ContainerItemContext.ofPlayerHand(player, hand));
	}

	public static @Nullable EnergyIo ofPlayerCursor(PlayerEntity player) {
		return ITEM.get(ItemKey.of(player.inventory.getCursorStack()), ContainerItemContext.ofPlayerCursor(player));
	}

	static {
		EnergyImpl.loadTrCompat();
	}
}
