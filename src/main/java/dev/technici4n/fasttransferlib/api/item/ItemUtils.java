package dev.technici4n.fasttransferlib.api.item;

import dev.technici4n.fasttransferlib.api.Simulation;
import net.minecraft.item.ItemStack;

public class ItemUtils {
    /**
     * Move some items from an {@link ItemExtractable} to an {@link ItemInsertable}.
     * @param maxCount The maximum number of items to move.
     * @return The number of items that were moved.
     */
    public static int moveMultiple(ItemExtractable from, ItemInsertable to, int maxCount) {
        int totalMoved = 0;
        for (int i = 0; i < from.getSlotCount() && maxCount > 0; ++i) {
            // Try to extract once
            ItemStack filter = from.extract(i, from.getStack(i), Simulation.SIMULATE);
            if (filter.isEmpty()) continue;
            // Try to insert
            filter = filter.copy();
            filter.setCount(Math.min(filter.getCount(), maxCount));
            ItemStack leftover = to.insert(filter.copy(), Simulation.SIMULATE);
            int moved = filter.getCount() - leftover.getCount();
            if (moved == 0) continue;
            filter.setCount(moved);
            // Try to extract again with the new count
            ItemStack extracted = from.extract(i, filter, Simulation.SIMULATE);
            if (extracted.getCount() != moved) continue;
            extracted = from.extract(i, filter, Simulation.ACT);
            // TODO: if extracted.getCount() != moved, throw
            leftover = to.insert(extracted, Simulation.ACT);
            // TODO: if !leftover.isEmpty(), throw
            totalMoved += moved;
        }
        return totalMoved;
    }
}
