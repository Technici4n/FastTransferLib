package dev.technici4n.fasttransferlib.api.fluid;

import dev.technici4n.fasttransferlib.api.ContainerItemContext;
import dev.technici4n.fasttransferlib.api.item.ItemKey;
import dev.technici4n.fasttransferlib.api.item.ItemKeyApiLookup;
import dev.technici4n.fasttransferlib.api.item.ItemKeyApiLookupRegistry;
import dev.technici4n.fasttransferlib.impl.fluid.FluidImpl;
import dev.technici4n.fasttransferlib.impl.fluid.compat.vanilla.VanillaCompat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookupRegistry;

public class FluidApi {
	public static final BlockApiLookup<FluidIo, @NotNull Direction> SIDED =
			BlockApiLookupRegistry.getLookup(new Identifier("fasttransferlib:sided_fluid_io"), FluidIo.class, Direction.class);
	public static final ItemKeyApiLookup<FluidIo, ContainerItemContext> ITEM =
			ItemKeyApiLookupRegistry.getLookup(new Identifier("fasttransferlib:fluid_io"), FluidIo.class, ContainerItemContext.class);

	public static @Nullable FluidIo ofPlayerHand(PlayerEntity player, Hand hand) {
		return ITEM.get(ItemKey.of(player.getStackInHand(hand)), ContainerItemContext.ofPlayerHand(player, hand));
	}

	public static @Nullable FluidIo ofPlayerCursor(PlayerEntity player) {
		return ITEM.get(ItemKey.of(player.inventory.getCursorStack()), ContainerItemContext.ofPlayerCursor(player));
	}

	static {
		VanillaCompat.load();
		FluidImpl.loadLbaCompat();
	}
}
