package dev.technici4n.fasttransferlib.api.fluid;

import dev.technici4n.fasttransferlib.api.ContainerItemContext;
import dev.technici4n.fasttransferlib.impl.fluid.FluidImpl;
import dev.technici4n.fasttransferlib.impl.fluid.compat.vanilla.VanillaCompat;
import org.jetbrains.annotations.NotNull;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.provider.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.provider.v1.block.BlockApiLookupRegistry;
import net.fabricmc.fabric.api.provider.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.provider.v1.item.ItemApiLookupRegistry;

public class FluidApi {
	public static final BlockApiLookup<FluidIo, @NotNull Direction> SIDED =
			BlockApiLookupRegistry.getLookup(new Identifier("fasttransferlib:sided_fluid_io"), FluidIo.class, Direction.class);
	public static final BlockApiLookup<FluidIo, Void> UNSIDED =
			BlockApiLookupRegistry.getLookup(new Identifier("fasttransferlib:unsided_fluid"), FluidIo.class, Void.class);
	public static final ItemApiLookup<FluidIo, ContainerItemContext> ITEM =
			ItemApiLookupRegistry.getLookup(new Identifier("fasttransferlib:fluid_io"), FluidIo.class, ContainerItemContext.class);

	static {
		VanillaCompat.load();
		FluidImpl.loadLbaCompat();
	}
}
