package dev.technici4n.fasttransferlib.api.item;

import net.fabricmc.fabric.api.provider.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.provider.v1.block.BlockApiLookupRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

public class ItemApi {
    public static final BlockApiLookup<ItemView, @NotNull Direction> SIDED_VIEW =
            BlockApiLookupRegistry.getLookup(new Identifier("fasttransferlib:sided_item_view"), ItemView.class, Direction.class);

    static {
        // TODO: Vanilla and LBA compat for guaranteed epicness
    }
}
