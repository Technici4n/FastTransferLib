package dev.technici4n.fasttransferlib.impl.fluid.compat.vanilla;

import dev.technici4n.fasttransferlib.api.fluid.FluidApi;
import net.minecraft.block.Blocks;

public class VanillaCompat {
    public static void load() {
        // initialize static
    }

    static {
        FluidApi.SIDED_VIEW.registerForBlocks((world, pos, state, direction) -> new CauldronWrapper(world, pos), Blocks.CAULDRON);
        FluidApi.UNSIDED_VIEW.registerForBlocks((world, pos, state, direction) -> new CauldronWrapper(world, pos), Blocks.CAULDRON);
    }
}
