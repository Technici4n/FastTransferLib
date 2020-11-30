package dev.technici4n.fasttransferlib.api.fluid;

import dev.technici4n.fasttransferlib.api.ItemInteractionContext;
import dev.technici4n.fasttransferlib.impl.fluid.FluidImpl;
import dev.technici4n.fasttransferlib.impl.fluid.compat.lba.LbaCompat;
import net.fabricmc.fabric.api.provider.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.provider.v1.block.BlockApiLookupRegistry;
import net.fabricmc.fabric.api.provider.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.provider.v1.item.ItemApiLookupRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

public class FluidApi {
    public static final BlockApiLookup<FluidView, @NotNull Direction> SIDED_VIEW =
            BlockApiLookupRegistry.getLookup(new Identifier("fasttransferlib:sided_fluid_view"), FluidView.class, Direction.class);
    public static final BlockApiLookup<FluidView, Void> UNSIDED_VIEW =
            BlockApiLookupRegistry.getLookup(new Identifier("fasttransferlib:unsided_fluid_view"), FluidView.class, Void.class);
    public static final ItemApiLookup<FluidView, ItemInteractionContext> ITEM_VIEW =
            ItemApiLookupRegistry.getLookup(new Identifier("fasttransferlib:fluid_view"), FluidView.class, ItemInteractionContext.class);

    static {
        // TODO: Vanilla and full LBA compat for guaranteed epicness
        FluidImpl.loadLbaCompat();
    }
}
