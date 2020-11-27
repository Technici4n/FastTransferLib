package dev.technici4n.fasttransferlib.api.item;

import dev.technici4n.fasttransferlib.api.ItemInteractionContext;
import net.fabricmc.fabric.api.provider.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.provider.v1.block.BlockApiLookupRegistry;
import net.fabricmc.fabric.api.provider.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.provider.v1.item.ItemApiLookupRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

public class ItemApi {
    public static final BlockApiLookup<ItemView, @NotNull Direction> SIDED_VIEW =
            BlockApiLookupRegistry.getLookup(new Identifier("fasttransferlib:sided_item_view"), ItemView.class, Direction.class);
    public static final BlockApiLookup<ItemView, Void> UNSIDED_VIEW =
            BlockApiLookupRegistry.getLookup(new Identifier("fasttransferlib:unsided_item_view"), ItemView.class, Void.class);
    public static final ItemApiLookup<ItemView, ItemInteractionContext> ITEM_VIEW =
            ItemApiLookupRegistry.getLookup(new Identifier("fasttransferlib:item_view"), ItemView.class, ItemInteractionContext.class);

    static {
        // TODO: Vanilla and LBA compat for guaranteed epicness
    }
}
